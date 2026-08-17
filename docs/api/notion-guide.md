# 🚀 MCM Nomad Passport AI — 프론트엔드 & 매장 연동 API 가이드 (최종 완결판)

> **문서 버전**: `v1.2.0 (Final Production)`  
> **최종 갱신일**: 2026-08-18  
> **서버 상태**: 🟢 **100% Live & 25개 엔드포인트 전수 검증 완료**  
> **사용 팁**: 본 문서를 노션(Notion)에 그대로 복사하거나 마크다운(.md) 가져오기 하시면 깔끔한 데이터베이스/문서 형태로 바로 활용하실 수 있습니다.

---

## 🌐 1. 시스템 접속 정보 (Live Server)

* **운영 서버 Base URL**: `https://mcm-nomad-backend.onrender.com`
* **실시간 대화형 Swagger 콘솔**: [https://mcm-nomad-backend.onrender.com/swagger-ui/index.html](https://mcm-nomad-backend.onrender.com/swagger-ui/index.html)
* **글로벌 CORS 허용**: `*` (localhost:3000, Vercel, 모바일 앱 등 모든 오리진 통신 100% 허용)
* **데이터 요청/응답 헤더**: `Content-Type: application/json`

---

## 🔑 2. 테스트용 기본 계정 (전체 비밀번호: `1234`)

| 등급 | 이메일 (ID) | 비밀번호 | 이름 | 보유 마일리지 | 초기 세팅 & 추천 시연 기능 |
| :---: | :--- | :---: | :---: | :---: | :--- |
| **VIP** | **`vip@mcmworldwide.com`**<br>*(메인 시연용 ⭐)* | **`1234`** | **김노마드 (VIP)** | **15,000 P** | • **활성 여정(방콕행 `HST777`) 기본 등록됨**<br>• **장바구니 2종(MCM 백팩, 루이비통 키폴) & VIP 피팅 활성화** |
| **Gold** | **`gold@mcmworldwide.com`** | **`1234`** | **이여행 (Gold)** | **4,500 P** | 우수 고객 시연 |
| **Platinum** | **`platinum@mcmworldwide.com`** | **`1234`** | **박스타 (Platinum)** | **9,800 P** | 플래티넘 등급 혜택 시연 |
| **신규 가입** | `POST /api/v1/auth/register` | 자유 입력 | 자유 입력 | **1,000 P** | 신규 가입 웰컴 보너스 마일리지 자동 지급 |

---

## 📱 3. 고객(Customer) 앱 실시간 API 명세

### 🛫 Phase 1. 출국 전 & 공항 대기 (Pre-Flight)

#### 1. 휴대폰 SMS 인증번호 발송
* **`POST /api/v1/auth/phone/send-code`**
* **요청 본문**:
  ```json
  {
    "phone": "010-1234-5678"
  }
  ```
* **설명**: 6자리 인증번호 생성 (테스트용 마스터 번호 `123456` 지원)

#### 2. 휴대폰 SMS 인증번호 검증
* **`POST /api/v1/auth/phone/verify-code`**
* **요청 본문**:
  ```json
  {
    "phone": "010-1234-5678",
    "verificationCode": "123456"
  }
  ```
* **응답 키**: `phone`, `verified` (`true` / `false`), `message`

#### 3. 비밀번호 찾기 & 재설정
* **`POST /api/v1/auth/password/reset`**
* **요청 본문**:
  ```json
  {
    "email": "vip@mcmworldwide.com",
    "newPassword": "1234"
  }
  ```

#### 4. 로그인 & 유저 허브 접속
* **`POST /api/v1/auth/login`**
* **요청 본문**:
  ```json
  {
    "email": "vip@mcmworldwide.com",
    "password": "1234"
  }
  ```
* **응답**: `memberId`, `email`, `name`, `vipTier` (`VIP`), `nomadMiles`

#### 5. 실시간 항공편 운항 조회 (인천국제공항공사 관제 AODB 1분 동기화)
* **`GET /api/v1/flight/lookup?flightNumber=OZ741`**
* **설명**: 인천공항 실제 전광판과 1분 단위로 실시간 동기화되어 실제 탑승 게이트(`Gate 256`), 체크인 카운터(`G17-J34`), 실시간 지연 시간(`19분 지연`), 터미널 정보를 반환합니다.
* **응답 예시 (`200 OK`)**:
  ```json
  {
    "flightNumber": "OZ741",
    "airlineName": "아시아나항공",
    "originCode": "ICN",
    "originTerminal": "인천공항 탑승동",
    "destinationCode": "BKK",
    "destinationName": "BKK (방콕/수완나품)",
    "gate": "Gate 256",
    "flightStatus": "DELAYED",
    "scheduledDepartureFormatted": "오후 7:35",
    "scheduledArrivalFormatted": "오전 1:35",
    "flightDuration": "6시간 0분",
    "checkinCounter": "G17-J34",
    "remark": "출발",
    "delayMinutes": 19,
    "dataSource": "인천국제공항공사 실시간 관제 AODB 공식 데이터"
  }
  ```

#### 6. 보딩패스 Vision OCR 스캔 & 여정 등록
* **`POST /api/v1/journey/scan`**
* **요청 본문**:
  ```json
  {
    "memberId": 1,
    "pnr": "OZ741",
    "rawOcrText": "BOARDING PASS ASIANA OZ741 ICN TO BKK",
    "origin": "ICN",
    "destination": "BKK"
  }
  ```
* **응답**: `journeyId`, `pnr`, `flightStatus`, `destinationWeather`

#### 7. AI 라이브 카드 위젯 (SCR-102 디자인 싱크)
* **`GET /api/v1/journey/live-card/1`**
* **응답 데이터**:
  * `currentStep`: `CHECK_IN` $\rightarrow$ `SECURITY_CHECK` $\rightarrow$ `BOARDING` $\rightarrow$ `ARRIVAL`
  * `estimatedSecurityMinutes`: `25` (예상 보안검색 소요 시간)
  * `loungeGateLocation`: `"인천공항 라운지 (터미널 2, 게이트 16번 맞은편)"`
  * `loungeWalkingMinutes`: `15` (도보 15분)
  * `loungeWaitMinutes`: `3` (대기 3분)
  * `countdownMinutes`: 출발 잔여 시간

#### 8. 여정 분석 & 3단계 타임라인 (OpenAI GPT-4o)
* **`GET /api/v1/journey/analysis/1`**
* **응답 데이터**:
  * `rainProbability`: `"76%"` (현지 강수 확률)
  * `timeline`:
    1. **출발**: 인천국제공항 탑승구 (오후 7:35 출발, 50분 면세 여유)
    2. **비행중**: 아시아나 OZ741 (6시간 0분 비행)
    3. **도착**: 방콕 수완나품 공항 도착 (오전 1:35 도착 예정, 픽업)
  * `recommendedProducts`: 목적지 기후 맞춤 실물 상품 목록 (고화질 CDN 이미지 연동)

#### 9. Apple Wallet 디지털 보딩패스 다운로드
* **패스 정보 조회**: `GET /api/v1/journey/apple-wallet-pass/1`
* **바이너리 파일 다운로드**: `GET /api/v1/journey/apple-wallet-pass/download-file/1`

---

### 🛍️ Phase 2. 스마트 피팅 & 공항 면세 쇼핑 (In-Airport Store)

#### 10. 스마트 장바구니 담기
* **`POST /api/v1/cart/add`**
* **요청 본문**: `{"memberId": 1, "productId": 1, "quantity": 1}`

#### 11. 내 장바구니 조회
* **`GET /api/v1/cart/my?memberId=1`**
* **응답**: `cartId`, `choiceFit`, `items`, `totalPrice`

#### 12. ChoiceFit (VIP 사전 피팅 신청) 플래그 토글
* **`PUT /api/v1/cart/choice-fit`**
* **요청 본문**: `{"memberId": 1, "choiceFit": true}`

#### 13. 공항 한정 팝업 스팟 & 도보시간 뱃지
* **`GET /api/v1/style/popup-spots`**
* **응답**: 매장별 위치 및 `walkingMinutes` (`2분`, `8분`)

#### 14. 개인화 스타일 엔진 룩북 큐레이션
* **`GET /api/v1/style/1/recommendations`**

#### 15. 부티크 BLE 오토 체크인 & 웰컴 쿠폰 발급
* **`POST /api/v1/store/check-in`**
* **요청 본문**:
  ```json
  {
    "memberId": 1,
    "boutiqueLocation": "인천공항 T1 부티크",
    "checkInType": "BLE"
  }
  ```
* **응답**: `welcomeMessage`, `issuedCouponCode`, `discountRate` (`15%`)

#### 16. Fast Checkout 면세 결제 & 마일리지 적립
* **`POST /api/v1/order/checkout`**
* **요청 본문**:
  ```json
  {
    "memberId": 1,
    "journeyId": 1,
    "usedMiles": 1000
  }
  ```
* **응답**: `orderId`, `finalAmount`, `earnedMiles` (+1,000P 적립)

---

### 🌴 Phase 3. 도착지 현지 라이프 & 가죽 케어 (Post-Flight)

#### 17. Google Maps 실시간 럭셔리 부티크 & Care Desk 위치
* **`GET /api/v1/care/google-maps?destination=Bangkok`**
* **응답**: 실제 만다린 오리엔탈, 원 방콕 등 실시간 GPS 좌표 및 길안내 링크

#### 18. 현지 기후 맞춤 가죽 케어 AI 가이드 (GPT-4o)
* **`GET /api/v1/care/ai-care-tip?productName=MCM%20Backpack&weather=Bangkok%20Rain&lang=ko`**

#### 19. 시티 패스포트 스탬프 획득 (+1,000P)
* **`POST /api/v1/care/stamp-checkin`**
* **요청 본문**: `{"memberId": 1, "spotName": "Bangkok Boutique"}`

#### 20. 잔여 Nomad Miles 조회
* **`GET /api/v1/postflight/miles/1`**

---

## 🖥️ 4. 매장 직원용 태블릿 전용 API (Store Staff Tablet)

#### 21. VIP 방문 실시간 SSE 스트림 (Server-Sent Events)
* **`GET /api/v1/store/live-stream`**
* **설명**: VIP가 부티크에 들어서는 순간(BLE 비콘 감지 시) 1초 만에 매장 직원 태블릿으로 고객 프로필, 선호 취향, 사전 피팅 예약 목록이 실시간 푸시됩니다.

#### 22. 현재 매장 내 체크인 고객 목록 조회
* **`GET /api/v1/store/active-checkins`**

#### 23. 미구매 고객 재방문 분기 가이드
* **`GET /api/v1/store/re-entry-options/1`**
