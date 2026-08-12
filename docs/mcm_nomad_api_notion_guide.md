# 🚀 MCM Nomad Passport AI — 프론트엔드 API 연동 명세서

---

## 🌐 1. 공통 환경 정보 (Base URL & Specs)

* **배포 서버 Base URL**: `https://mcm-nomad-backend.onrender.com`
* **Swagger UI (실시간 테스트)**: `https://mcm-nomad-backend.onrender.com/swagger-ui/index.html`
* **데이터 포맷**: `Content-Type: application/json`

---

## 📱 PART 1. 유저 (Customer / VIP) 앱 전용 API 명세

### 🛫 Phase 1: Pre-Flight (출국 전 / 대기 / 온라인)

#### 1. 로그인 & 노마드 허브 접속
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/auth/login`
* **설명**: 회원 로그인 후 VIP 티어 및 보유 마일리지 정보를 수신합니다.
* **Request Body**:
  ```json
  {
    "email": "vip@mcmworldwide.com",
    "name": "김노마드 (VIP)"
  }
  ```
* **Response Key**: `memberId`, `email`, `name`, `vipTier`, `nomadMiles`

#### 2. 보딩패스 Vision OCR 스캔 & 여정 등록
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/journey/scan`
* **설명**: 탑승권 OCR 스캔 또는 PNR을 입력하여 비행 여정을 생성합니다.
* **Request Body**:
  ```json
  {
    "memberId": 1,
    "pnr": "MCM999",
    "rawOcrText": "BOARDING PASS PNR MCM999",
    "origin": "ICN",
    "destination": "BKK"
  }
  ```
* **Response Key**: `journeyId`, `pnr`, `origin`, `destination`, `departureDateTime`

#### 3. SCR-102 AI 라이브 카드 위젯 (카운트다운 & 게이트 & 라운지)
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/journey/live-card/{journeyId}`
* **설명**: 탑승까지 남은 시간(분), 게이트 번호, 공항 MCM VIP 라운지 대기 시간을 반환합니다.
* **Response Key**: `remainingMinutesToDeparture`, `gate`, `loungeLocation`, `loungeWaitTime`, `liveGuideMessage`

#### 4. SCR-203/301 목적지 기후 분석 & OpenAI GPT-4o 맞춤 Curation
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/journey/analysis/{journeyId}`
* **설명**: 실시간 기상 API 및 OpenAI가 분석한 기후 룩북과 맞춤 상품을 추천합니다.
* **Response Key**: `weatherInfo`, `climateSummary`, `recommendationReason` (OpenAI 룩북 문구), `recommendedProducts`

#### 5. Apple Wallet 디지털 탑승권 (.pkpass) 생성
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/journey/apple-wallet-pass/{journeyId}`
* **설명**: 비행 탑승권 및 VIP 패스카드를 Apple Wallet `.pkpass` 표준 구조로 생성합니다.
* **Response Key**: `passTypeIdentifier`, `serialNumber`, `boardingPassDetails`, `pkpassDownloadUrl`

#### 6. 스마트 장바구니 추가 & ChoiceFit (VIP 피팅 신청) 설정
* **상품 추가**: `POST /api/v1/cart/add`
  ```json
  { "memberId": 1, "productId": 1, "quantity": 1 }
  ```
* **피팅 신청 플래그 변경**: `PUT /api/v1/cart/choice-fit`
  ```json
  { "memberId": 1, "choiceFit": true }
  ```
* **내 장바구니 조회**: `GET /api/v1/cart/my?memberId=1`

---

### 🏪 Phase 2: Airport Store (공항 면세점 오토 체크인 & 결제)

#### 7. BLE / NFC / QR 오토 체크인
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/store/check-in`
* **설명**: 면세 부티크 접근 시 자동 체크인 및 웰컴 할인 쿠폰 메시지를 발송합니다.
* **Request Body**:
  ```json
  {
    "memberId": 1,
    "checkInType": "BLE",
    "qrCode": null
  }
  ```
* **Response Key**: `visitId`, `choiceFitRequested`, `welcomeCouponMessage`, `assistantNotified`

#### 8. 면세점 재방문 (Re-entry Flow) 선택 분기 조회
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/store/re-entry-options/{memberId}`
* **설명**: 구매 미완료 고객 재방문 시 "바로 결제 / 다시 피팅 / 새 상품 보기" 안내 팝업을 띄웁니다.
* **Response Key**: `hasPendingCart`, `pendingCartItemCount`, `recommendedAction`, `availableOptions`

#### 9. 면세 Fast Checkout 결제 & 마일리지 적립
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/order/checkout`
* **설명**: VIP 면세 할인(5%~15%) 자동 적용 및 결제, Nomad Miles(5%) 적립을 진행합니다.
* **Request Body**:
  ```json
  {
    "memberId": 1,
    "journeyId": 1
  }
  ```
* **Response Key**: `totalAmount`, `dutyFreeDiscount`, `finalAmount`, `earnedMiles`

---

### 🧳 Phase 3: Post-Flight (귀국 후 / 현지 로열티)

#### 10. OpenAI 기반 현지 기후 가죽 케어 AI 가이드
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/care/ai-care-tip`
* **Query Params**: `productName=MCM 비세토스 백팩`, `weather=방콕 습도 88% 열대성 스콜`
* **설명**: OpenAI GPT-4o가 실시간으로 생성한 맞춤형 가죽 관리 가이드 문구를 반환합니다.

#### 11. Google Maps API 실시간 MCM 매장 & Care Desk 지도 탐색
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/care/google-maps`
* **Query Params**: `destination=Bangkok`
* **설명**: 구글 맵스 API 기반 실시간 MCM 매장의 위도·경도 좌표 및 Google Maps 길안내 URL을 반환합니다.

#### 12. 현지 시티 패스포트 스탬프 획득 & 보상 마일리지
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/care/stamp-checkin`
* **Request Body**:
  ```json
  { "memberId": 1, "spotName": "MCM 방콕 시암파라곤" }
  ```
* **Response Key**: `earnedMiles` (1000 마일 적립), `totalMiles`, `message`

---

## 🏪 PART 2. 매장 직원 (Store Staff / Assistant) 태블릿 전용 API 명세

### 1. VIP 고객 부티크 입장 알림 처리 (Check-in Monitor)
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/store/check-in`
* **용도**: 고객이 수동 QR 스캔을 하거나 BLE 센서가 고객을 감지하면 태블릿 화면에 입장 알림 팝업을 생성합니다.
* **직원 확인 데이터**:
  - `memberName`: 고객 성함
  - `vipTier`: 회원 VIP 등급 (`VIP`, `PLATINUM`, `GOLD`)
  - `choiceFitRequested`: 사전 피팅 신청 여부 (`true` / `false`)

### 2. VIP 사전 피팅 신청 품목(ChoiceFit) 사전 세팅 조회
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/cart/my?memberId={memberId}`
* **용도**: VIP 피팅 신청 고객(`choiceFitRequested: true`) 입장 시, 직원이 태블릿에서 미리 세팅할 상품 목록을 조회합니다.
* **직원 확인 데이터**:
  - `items`: 피팅 상품명, 상품 카테고리, 이미지 URL, 수량, 가격

### 3. 미구매 고객 재방문 (Re-entry) 현장 세일즈 가이드
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/store/re-entry-options/{memberId}`
* **용도**: 이전 방문 시 장바구니 상품을 결제하지 않았던 고객이 재방문했을 때 태블릿에 세일즈 가이드를 표시합니다.
* **직원 확인 데이터**:
  - `hasPendingCart`: 미결제 장바구니 존재 여부
  - `recommendedAction`: 직원 세일즈 권장 멘트 텍스트
  - `availableOptions`: 세일즈 분기 옵션 목록

### 4. 면세 한도 할인 계산 & Fast Checkout 수령 처리
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/order/checkout`
* **용도**: 현장 피팅 후 즉시 착장 수령 및 결제 처리 시 호출합니다.
* **직원 확인 데이터**:
  - `dutyFreeDiscount`: 적용된 VIP 면세 할인 금액
  - `finalAmount`: 최종 결제 처리 금액
  - `earnedMiles`: 새로 적립된 로열티 마일리지

---

## 📑 한눈에 보는 API 분류 요약표

| 구분 | 주요 화면/기능 | 백엔드 API 엔드포인트 | 비고 |
| :--- | :--- | :--- | :--- |
| **유저** | 로그인 / 프로필 | `POST /api/v1/auth/login` | VIP 티어 & 마일리지 |
| **유저** | 보딩패스 OCR 스캔 | `POST /api/v1/journey/scan` | PNR 여정 자동 등록 |
| **유저** | AI 라이브 카드 | `GET /api/v1/journey/live-card/{journeyId}` | 탑승 카운트다운 위젯 |
| **유저** | AI 룩북 Curation | `GET /api/v1/journey/analysis/{journeyId}` | Open-Meteo + GPT-4o |
| **유저** | Apple Wallet 패스 | `GET /api/v1/journey/apple-wallet-pass/{journeyId}` | PKPass 디지털 카드 |
| **유저** | 장바구니 / 피팅신청 | `POST /api/v1/cart/add`, `PUT /api/v1/cart/choice-fit` | ChoiceFit 플래그 |
| **유저/직원**| 부티크 체크인 | `POST /api/v1/store/check-in` | BLE/NFC/QR 체크인 |
| **유저/직원**| 재방문 분기 | `GET /api/v1/store/re-entry-options/{memberId}` | Re-entry 세일즈 가이드 |
| **유저/직원**| 면세 결제 | `POST /api/v1/order/checkout` | VIP 면세 할인 & 적립 |
| **유저** | AI 가죽 케어 가이드 | `GET /api/v1/care/ai-care-tip` | GPT-4o 가죽 관리 팁 |
| **유저** | Google Maps 스팟 | `GET /api/v1/care/google-maps` | 구글 지도 실시간 매장 |
| **유저** | 시티 패스포트 스탬프 | `POST /api/v1/care/stamp-checkin` | +1000 마일리지 적립 |
