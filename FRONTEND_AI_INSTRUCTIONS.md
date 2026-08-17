# 🤖 Herstory AI - 프론트엔드 AI 에이전트 전용 작업 지시서 (Master AI Instructions)

> **이 문서는 프론트엔드 개발을 수행하는 AI 에이전트(Cursor, Claude, Antigravity, ChatGPT 등)가 반드시 준수해야 하는 최우선 가이드라인입니다.**
> 개발을 시작하기 전 아래 3대 절대 원칙을 반드시 숙지하고 작업을 진행하십시오.

---

## ⚠️ 프론트엔드 AI 3대 절대 원칙 (Must Follow)

### 1. 🏷️ 프로젝트 & 브랜드 명칭 변경 인지 (`Nomad` ➡️ `Herstory`)
* 프로젝트 및 서비스의 공식 명칭은 **"Herstory"** (Herstory AI / Herstory Club)입니다.
* 기존 기획 문서나 백엔드 코드의 레거시 식별자(nomad)에 현혹되지 마십시오.
* **UI에 표시되는 모든 명칭, 텍스트, 헤더 타이틀, 로고 문구, 마일리지(`Herstory Miles`), VIP 클럽(`Herstory VIP Hub`) 등은 무조건 `Herstory`로 표기합니다.**

### 2. 🎨 Figma 1:1 Pixel-Perfect 준수 (임의 디자인 수정 절대 금지)
* **디자인의 단일 진실 원천(Single Source of Truth)은 오직 'Figma'입니다.**
* "더 예뻐 보인다"거나 "더 최신 트렌드 같다"는 이유로 **색상(Color), 여백(Padding/Margin), 폰트(Typography), 컴포넌트 레이아웃, 버튼 둥글기 등을 임의로 창작하거나 변경하지 마십시오.**
* 피그마에 정의된 레이아웃 구조와 스타일 토큰을 그대로 코드로 옮겨야 합니다.

### 3. 🛑 미완성 디자인 임의 구현 금지 (Strict Scope Control)
* 현재 피그마 디자인은 핵심 화면 위주로 작업 중이며, **아직 모든 서브 화면과 세부 상태(Empty State, 복잡한 설정창 등)가 완벽하게 구현(정의)되지 않았습니다.**
* **피그마에 없는 화면, 미정의 팝업, 불필요한 기능 페이지를 AI가 상상해서 임의로 구현하지 마십시오.**
* 오직 아래에 명시된 **Figma에 확정된 화면 및 컴포넌트만 정밀하게 구현**하고, 미정의 영역은 피그마 디자인이 확정될 때까지 구현하지 않고 비워두거나 심플한 TODO 영역으로만 유지하십시오.

---

## 📱 1. Figma 확정 디자인 연동 정보

* **Figma 파일 URL**: `https://www.figma.com/design/u8brOGUEOQxG45WNSXW0OW/%EC%A0%9C%EB%AA%A9-%EC%97%86%EC%9D%8C?node-id=0-1`
* **File Key**: `u8brOGUEOQxG45WNSXW0OW`
* **모바일 기준 해상도**: **402 × 874 px (Apple iPhone 17 규격)**

### 🎯 구현 대상 확정 화면 목록 (8개 화면)

| 번호 | 화면 명칭 | Figma Node ID | 컴포넌트 권장 파일명 | 화면 핵심 구성 요소 (피그마 디자인 준수) |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **로그인** | `1:2` | `LoginScreen.tsx` | Herstory 로고, 이메일/비밀번호 입력, 로그인 버튼, VIP 접속 안내 |
| 2 | **회원가입** | `15:1025` | `RegisterScreen.tsx` | 뒤로가기 헤더, 가입 폼(이메일, 비밀번호, 이름, 연락처), 약관동의 |
| 3 | **홈 (대시보드)** | `1:26` | `HomeScreen.tsx` | Herstory 상단 헤더, 실시간 날씨 위젯, 활성 보딩패스 요약 카드, 퀵 액션 그리드, 하단 탭바 |
| 4 | **AI 라이브카드** | `6:12` | `AiLiveCardScreen.tsx` | 항공편 실시간 상태, 남은 탑승시간 카운트다운, 게이트 안내, 라운지 현황 |
| 5 | **보딩패스 스캔** | `3:315` | `BoardingPassScanScreen.tsx` | PNR 입력 / OCR 카메라 스캔 뷰파인더 가이드 |
| 6 | **여정 타임라인** | `10:801` | `JourneyTimelineScreen.tsx` | 목적지 기상 알림, 3단계 여정 타임라인(출발-비행-도착), 스타일 룩북 진입 링크 |
| 7 | **스타일 엔진 룩북** | `16:1246` | `StyleEngineScreen.tsx` | 목적지 기후 맞춤 MCM 패키지 룩북 헤더, 큐레이션 아이템 카드 목록 |
| 8 | **공항 팝업 & 스팟** | `63:471` / `94:15` | `AirportPopupScreen.tsx` | 공항 맵 뷰, MCM 스토어 및 게이트 위치, 체크인/쿠폰 발급 CTA |

*(※ 위 목록에 없는 화면이나 피그마에 없는 복잡한 상태는 개발하지 않습니다.)*

---

## 🔌 2. 실제 백엔드 API 연동 명세 (Spring Boot 3.x)

* **로컬 서버**: `http://localhost:8080`
* **배포 서버**: `https://mcm-nomad-backend.onrender.com`
* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html` (또는 배포 URL)
* **CORS**: Cross-Origin 전체 허용 (`@CrossOrigin(origins = "*")`)

### 📡 실제 백엔드 컨트롤러 엔드포인트 목록

#### 1) 인증 (Auth)
* `POST /api/v1/auth/register` (신규 회원가입)
  * Body: `{"email": "user@example.com", "password": "password123!", "name": "홍길동", "phone": "010-1234-5678"}`
  * Return: `memberId`, `email`, `name`, `vipTier` (기본 `SILVER`), `nomadMiles` (웰컴 `1000` 지급), `message`
* `POST /api/v1/auth/login` (로그인)
  * Body: `{"email": "vip@mcmworldwide.com", "password": "1234"}`
  * Return: `memberId`, `email`, `name`, `vipTier` (VIP, PLATINUM, GOLD, SILVER), `nomadMiles` (UI에서는 `Herstory Miles`로 표시), `message`

#### 2) 여정 & 보딩패스 (Journey)
* `POST /api/v1/journey/scan` : 탑승권 OCR/PNR 스캔 여정 등록
  * Body: `{"memberId": 1, "pnr": "MCM999", "rawOcrText": "...", "origin": "ICN", "destination": "BKK"}`
* `GET /api/v1/journey/live-card/{journeyId}` : 실시간 카운트다운/게이트/라운지 정보
* `GET /api/v1/journey/analysis/{journeyId}` : 목적지 기후 분석 및 추천 데이터
* `GET /api/v1/journey/apple-wallet-pass/download/{journeyId}.pkpass` : Apple Wallet 패스 다운로드

#### 3) 스마트 장바구니 & ChoiceFit (Cart)
* `POST /api/v1/cart/add` : 장바구니 상품 추가
* `PUT /api/v1/cart/choice-fit` : VIP 피팅 신청 여부 토글
* `GET /api/v1/cart/my?memberId={memberId}` : 내 장바구니 조회

#### 4) 공항 매장 체크인 & 태블릿 알림 (Store)
* `POST /api/v1/store/check-in` : 매장 체크인 (웰컴 쿠폰 발급)
* `GET /api/v1/store/re-entry-options/{memberId}` : 재방문 고객 분기 옵션
* `GET /api/v1/store/notifications/stream` : 실시간 SSE 알림 스트림 (태블릿/어시스턴트용)

#### 5) 결제 & 마일리지 (Order)
* `POST /api/v1/order/checkout` : 면세 결제 및 Herstory Miles 적립

#### 6) 현지 스팟 & 케어 (Care)
* `GET /api/v1/care/visetos-spots?memberId={memberId}` : 현지 플래그십 및 Care Desk 위치
* `GET /api/v1/care/google-maps?destination={city}` : Google Maps 위치 정보
* `GET /api/v1/care/ai-care-tip?productName={name}&weather={weather}&lang=ko` : GPT-4o 가죽 케어 가이드
* `POST /api/v1/care/stamp-checkin` : 시티 패스포트 스탬프 획득 및 보너스 마일리지 적립

#### 7) 실시간 항공편 조회 (Flight)
* `GET /api/v1/flight/lookup?flightNumber={flightNumber}` : 항공편(예: KE651, OZ741, JL92) 실시간 운항 정보

---

## 🏛️ 3. 프론트엔드 프로젝트 권장 구조

```
src/
├── assets/                  # Figma 추출 SVG 아이콘 및 이미지 에셋
├── components/
│   ├── common/              # 전역 공통 Atomic 컴포넌트 (피그마 디자인 100% 준수)
│   │   ├── Header.tsx       # Herstory 상단 로고 & 알림 헤더
│   │   ├── BottomNav.tsx    # 하단 네비게이션 탭바
│   │   ├── Button.tsx       # 피그마 규격 버튼
│   │   └── Input.tsx        # 피그마 규격 텍스트/입력 필드
│   └── screens/             # 피그마 8대 확정 화면
│       ├── LoginScreen.tsx
│       ├── RegisterScreen.tsx
│       ├── HomeScreen.tsx
│       ├── AiLiveCardScreen.tsx
│       ├── BoardingPassScanScreen.tsx
│       ├── JourneyTimelineScreen.tsx
│       ├── StyleEngineScreen.tsx
│       └── AirportPopupScreen.tsx
├── services/                # Axios API 클라이언트 (백엔드 실제 엔드포인트 연동)
│   └── api.ts
└── styles/
    └── theme.ts             # 피그마 추출 컬러/폰트/스페이싱 토큰
```

---

## 📋 4. 프론트 AI 작업 지시 프롬프트 (복사하여 사용)

프론트엔드 레포지토리에서 AI 에이전트를 실행할 때 아래 프롬프트를 복사하여 입력하십시오:

```text
너는 Herstory AI 프론트엔드 전문 개발자야.
FRONTEND_AI_INSTRUCTIONS.md 문서를 최우선 지침서로 삼아 개발을 진행해.

[필수 준수 사항]
1. 프로젝트 및 서비스 공식 명칭은 "Herstory"야. 이전 이름인 Nomad는 절대 노출하지 마.
2. 디자인은 Figma(URL: https://www.figma.com/design/u8brOGUEOQxG45WNSXW0OW)를 1:1로 정확하게 준수해야 해. 색상, 간격, 폰트, 배치를 임의로 수정하거나 창작하지 마.
3. 아직 피그마 디자인이 완성되지 않은 미정의 화면이나 상태는 절대 임의로 상상해서 만들지 말고, 확정된 8개 주요 화면(Login, Register, Home, AiLiveCard, BoardingPassScan, JourneyTimeline, StyleEngine, AirportPopup)만 정밀하게 구현해.
4. 백엔드 연동은 FRONTEND_AI_INSTRUCTIONS.md에 적힌 실제 엔드포인트 규격(Spring Boot 3.x)에 100% 맞춰서 작성해줘.

우선 공통 테마/레이아웃과 홈 화면(HomeScreen)부터 구현을 시작해줘.
```
