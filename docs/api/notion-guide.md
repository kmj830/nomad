# 🚀 MCM Nomad Passport AI — 프론트엔드 & 매장 연동 API 명세서 (최종 완결판)

---

## 🌐 1. 시스템 환경 정보 (Base URL & Specs)

* **배포 서버 Base URL**: `https://mcm-nomad-backend.onrender.com`
* **Swagger UI (실시간 테스트)**: `https://mcm-nomad-backend.onrender.com/swagger-ui/index.html`
* **글로벌 CORS 설정**: 모든 도메인(`localhost:3000`, Vercel, Netlify 등) Cross-Origin 통신 100% 허용
* **데이터 포맷**: `Content-Type: application/json`

---

## 📱 PART 1. 유저 (Customer / VIP) 앱 전용 API 명세

### 🛫 Phase 1: Pre-Flight (출국 전 / 공항 대기 / 온라인)

#### 1. 로그인 & 노마드 허브 접속
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/auth/login`
* **설명**: 회원 로그인 후 VIP 티어 및 보유 Nomad Miles 정보를 수신합니다.
* **Request Body**:
  ```json
  {
    "email": "vip@mcmworldwide.com",
    "name": "김노마드 (VIP)"
  }
  ```
* **Response Key**: `memberId`, `email`, `name`, `vipTier` (`VIP`, `PLATINUM`, `GOLD`), `nomadMiles`

#### 2. 실시간 항공편 운항 정보 조회 (Flight API)
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/flight/lookup`
* **Query Params**: `flightNumber=KE651` (또는 `OZ741`, `SQ607`, `LH713`)
* **설명**: 항공 편명 기준 항공사, 출발 터미널(T1/T2), 탑승구 게이트, 목적지 정보를 실시간 반환합니다.
* **Response Key**: `flightNumber`, `airlineName`, `originTerminal`, `destinationCode`, `gate`, `flightStatus`

#### 3. 보딩패스 Vision OCR 스캔 & 여정 등록
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

#### 4. SCR-102 AI 라이브 카드 위젯 (카운트다운 & 게이트 & 라운지)
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/journey/live-card/{journeyId}`
* **설명**: 탑승까지 남은 시간(분), 게이트 번호, 공항 MCM VIP 라운지 대기 현황을 반환합니다.

#### 5. SCR-203/301 목적지 기후 분석 & OpenAI GPT-4o 맞춤 Curation
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/journey/analysis/{journeyId}`
* **설명**: Open-Meteo 실시간 기상 API 및 OpenAI가 분석한 목적지 기후 룩북과 방수/케어 상품을 추천합니다. (Spring Cache 0ms 최적화)

#### 6. SCR-201 Apple Wallet 디지털 탑승권 (.pkpass) 생성 & 바이너리 다운로드
* **패스 JSON 조회**: `GET /api/v1/journey/apple-wallet-pass/{journeyId}`
* **iOS Safari 호환 다운로드**: `GET /api/v1/journey/apple-wallet-pass/download-file/{journeyId}`
* **설명**: 아이폰 지갑(Wallet) 앱 연동을 위한 표준 `.pkpass` 바이너리 지장/보딩패스 파일을 생성합니다.

#### 7. 스마트 장바구니 추가 & ChoiceFit (VIP 피팅 신청) 설정
* **상품 추가**: `POST /api/v1/cart/add` (`{ "memberId": 1, "productId": 1, "quantity": 1 }`)
* **피팅 신청 플래그 변경**: `PUT /api/v1/cart/choice-fit` (`{ "memberId": 1, "choiceFit": true }`)
* **내 장바구니 조회**: `GET /api/v1/cart/my?memberId=1`

---

### 🏪 Phase 2: Airport Store (공항 면세점 오토 체크인 & 결제)

#### 8. BLE / NFC / QR 오토 체크인
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/store/check-in`
* **설명**: 면세 부티크 접근 시 자동 체크인 및 웰컴 할인 쿠폰 발급, 매장 직원 태블릿 실시간 알림을 트리거합니다.

#### 9. 면세점 재방문 (Re-entry Flow) 선택 분기 조회
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/store/re-entry-options/{memberId}`
* **설명**: 구매 미완료 고객 재방문 시 "바로 결제 / 다시 피팅 / 새 상품 보기" 안내 팝업을 제공합니다.

#### 10. 면세 Fast Checkout 결제 & 마일리지 적립
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/order/checkout`
* **설명**: VIP 면세 할인(5%~15%) 자동 적용 및 결제, Nomad Miles(5%) 적립을 처리합니다.

---

### 🧳 Phase 3: Post-Flight (귀국 후 / 현지 로열티)

#### 11. OpenAI 기반 현지 기후 가죽 케어 AI 가이드 (다국어 지원)
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/care/ai-care-tip`
* **Query Params**: `productName=MCM 비세토스 백팩`, `weather=방콕 습도 88% 열대성 스콜`, `lang=ko` (지원: `ko`, `en`, `ja`, `zh`)
* **설명**: OpenAI GPT-4o가 사용자의 언어 설정(한국어/영어/일본어/중국어)에 맞춰 실시간 명품 가죽 관리 가이드를 생성합니다.

#### 12. Google Maps API 실시간 MCM 매장 & Care Desk 지도 탐색
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/care/google-maps`
* **Query Params**: `destination=Bangkok`
* **설명**: 구글 맵스 API 기반 현지 MCM 매장의 실시간 GPS 좌표 및 Google Maps 길안내 URL을 반환합니다. (Spring Cache 0ms 최적화)

#### 13. 현지 시티 패스포트 스탬프 획득 & 보상 마일리지
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/care/stamp-checkin`
* **설명**: 목적지 MCM 부티크 방문 시 시티 스탬프를 획득하고 +1000 Nomad Miles를 적립합니다.

#### 14. FCM 디바이스 푸시 알림
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/care/push-test`
* **Query Params**: `title=MCM VIP 알림`, `body=수완나품 공항 면세점 방문을 환영합니다`

---

## 🏪 PART 2. 매장 직원 (Store Staff / Assistant) 태블릿 전용 API 명세

#### 1. SCR-402 매장 직원 태블릿 실시간 SSE 알림 스트림 (Server-Sent Events)
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/store/notifications/stream`
* **Content-Type**: `text/event-stream`
* **용도**: 고객이 부티크에 입장하여 체크인하는 순간, 매장 직원 태블릿 화면에 **실시간 팝업 이벤트를 띵동 자동 수신**합니다.

#### 2. VIP 고객 부티크 입장 알림 처리 (Check-in Monitor)
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/store/check-in`
* **용도**: 직원 태블릿에서 VIP 고객 등급(`vipTier`), 성함, 피팅 신청 여부(`choiceFitRequested`)를 확인합니다.

#### 3. VIP 사전 피팅 신청 품목(ChoiceFit) 사전 세팅 조회
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/cart/my?memberId={memberId}`
* **용도**: VIP 피팅 신청 고객 입장 시, 직원이 태블릿에서 미리 세팅할 상품 목록(상품명, 사진, 수량, 가격)을 조회합니다.

#### 4. 미구매 고객 재방문 (Re-entry) 현장 세일즈 가이드
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/store/re-entry-options/{memberId}`
* **용도**: 미결제 재방문 고객 시 직원 태블릿에 이전 장바구니 상품 수 및 **권장 세일즈 멘트 가이드(`recommendedAction`)**를 표시합니다.

#### 5. 면세 한도 할인 계산 & Fast Checkout 수령 처리
* **HTTP Method**: `POST`
* **Endpoint**: `/api/v1/order/checkout`
* **용도**: 현장 즉시 착장 수령 및 결제 처리 시 호출합니다.

---

## ⚙️ PART 3. 시스템 모니터링 API (System Health)

#### 1. 서버 및 외부 API 실시간 상태 체크
* **HTTP Method**: `GET`
* **Endpoint**: `/api/v1/health`
* **설명**: Render PostgreSQL DB, OpenAI GPT-4o, Google Maps API, Flight API, Weather API 통신 상태 및 서버 업타임을 점검합니다.

---

## 📑 한눈에 보는 전체 API 요약표

| 구분 | 도메인 / 기능 | 백엔드 API 엔드포인트 | 비고 |
| :--- | :--- | :--- | :--- |
| **유저** | 로그인 / 프로필 | `POST /api/v1/auth/login` | VIP 티어 & 마일리지 |
| **유저** | 항공편 운항 조회 | `GET /api/v1/flight/lookup` | 편명 실시간 검색 |
| **유저** | 보딩패스 OCR 스캔 | `POST /api/v1/journey/scan` | PNR 여정 자동 등록 |
| **유저** | AI 라이브 카드 | `GET /api/v1/journey/live-card/{journeyId}` | 탑승 카운트다운 위젯 |
| **유저** | AI 룩북 Curation | `GET /api/v1/journey/analysis/{journeyId}` | Open-Meteo + GPT-4o |
| **유저** | Apple Wallet 패스 | `GET /api/v1/journey/apple-wallet-pass/download-file/{journeyId}` | .pkpass 지갑 다운로드 |
| **유저** | 장바구니 / 피팅신청 | `POST /api/v1/cart/add`, `PUT /api/v1/cart/choice-fit` | ChoiceFit 플래그 |
| **유저/직원**| 부티크 체크인 | `POST /api/v1/store/check-in` | BLE/NFC/QR 체크인 |
| **직원** | 태블릿 실시간 알림 | `GET /api/v1/store/notifications/stream` | **SSE 실시간 스트림** |
| **유저/직원**| 재방문 분기 | `GET /api/v1/store/re-entry-options/{memberId}` | Re-entry 세일즈 가이드 |
| **유저/직원**| 면세 결제 | `POST /api/v1/order/checkout` | VIP 면세 할인 & 적립 |
| **유저** | AI 가죽 케어 가이드 | `GET /api/v1/care/ai-care-tip` | **다국어(ko/en/ja/zh)** |
| **유저** | Google Maps 스팟 | `GET /api/v1/care/google-maps` | 구글 지도 실시간 매장 |
| **유저** | 시티 패스포트 스탬프 | `POST /api/v1/care/stamp-checkin` | +1000 마일리지 적립 |
| **시스템**| 헬스 체크 | `GET /api/v1/health` | DB & API 상태 점검 |
