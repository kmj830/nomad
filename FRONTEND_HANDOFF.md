# 🚀 Herstory AI - 프론트엔드 개발 인계 명세서 (Frontend Handoff Guide)

> **이 문서는 새로운 프론트엔드 프로젝트(React, Next.js, React Native Expo 등)를 구축할 때, AI 에이전트와 개발자가 백엔드 연동 및 피그마 디자인 컨텍스트를 100% 즉시 이어받아 개발할 수 있도록 작성된 공식 인계 문서입니다.**

---

## 1. 🎨 Figma 디자인 연동 정보 (Design Context)

* **Figma 파일 URL**: `https://www.figma.com/design/u8brOGUEOQxG45WNSXW0OW/%EC%A0%9C%EB%AA%A9-%EC%97%86%EC%9D%8C?node-id=0-1`
* **File Key**: `u8brOGUEOQxG45WNSXW0OW`
* **Personal Access Token**: `figd_YOUR_PERSONAL_ACCESS_TOKEN` (Figma Settings에서 발급)
* **모바일 기준 해상도**: **402 × 874 px (Apple iPhone 17 규격)**

### 📱 8개 주요 화면 노드 매핑 테이블

| 번호 | 화면 명칭 | Figma Node ID | 프론트엔드 화면 컴포넌트 | 주요 구성 요소 및 기능 |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **로그인** | `1:2` | `LoginScreen.tsx` | 브랜드 로고, 이메일/비밀번호 인풋, 로그인 CTA, 소셜 로그인, 가입 링크 |
| 2 | **회원가입** | `15:1025` | `RegisterScreen.tsx` | 뒤로가기 헤더, 이름/이메일/전화번호/비밀번호 입력폼, 약관동의, 가입 완료 |
| 3 | **홈 (대시보드)** | `1:26` | `HomeScreen.tsx` | 상단 로고 헤더, 실시간 날씨 위젯, 활성 항공권(JL92) 요약 카드, 퀵 액션 그리드, 하단 탭바 |
| 4 | **AI 라이브카드** | `6:12` | `AiLiveCardScreen.tsx` | 실시간 항공편 상태 배너, AI 여정 추천(예상 소요시간 25분, 게이트 최적 경로), 스마트 체크리스트 |
| 5 | **보딩패스 스캔** | `3:315` | `BoardingPassScanScreen.tsx` | 스캔 모드 탭(QR 카메라/PNR 수동/Apple Wallet), 카메라 뷰파인더 가이드, OCR 지원 뱃지 |
| 6 | **여정 타임라인** | `10:801` | `JourneyTimelineScreen.tsx` | 도쿄 기상 알림 배너(강수확률 76%), 3단계 여정 타임라인(출발-비행-도착), 날씨 매칭 룩북 진입 칩 |
| 7 | **개인화 스타일 엔진** | `16:1246` | `StyleEngineScreen.tsx` | MCM 패키지 룩북 헤더, 방수 트렌치코트/레인 트라우저/레인부츠/모노그램 우산·가방 추천 카드 |
| 8 | **공항 팝업 스팟** | `63:471` / `94:15` | `AirportPopupScreen.tsx` | 공항 맵 뷰, MCM 타임스토어(GATE 12) & 한정판 팝업(T1 2F) 정보, 인증번호/쿠폰 발급 CTA |

---

## 2. 🔌 백엔드 API 연동 명세 (Backend Integration)

* **로컬 백엔드 서버 URL**: `http://localhost:8080` (Spring Boot 3.x)
* **CORS 설정**: 전체 출처 허용 (`@CrossOrigin(origins = "*")` 설정 완료)
* **인증 방식**: `Authorization: Bearer <JWT_TOKEN>` 헤더

### 📡 핵심 API 엔드포인트 요약

| 분류 | HTTP Method | 엔드포인트 경로 | 설명 |
| :--- | :--- | :--- | :--- |
| **인증 (Auth)** | `POST` | `/api/v1/auth/login` | 이메일/비밀번호 로그인 (JWT 토큰 발급) |
| | `POST` | `/api/v1/auth/register` | 신규 회원가입 |
| | `GET` | `/api/v1/auth/me` | 현재 로그인 사용자 정보 조회 |
| **항공편 (Flight)** | `GET` | `/api/v1/flights/{flightNumber}` | 항공편 상세 실시간 조회 (예: JL92, OZ102) |
| | `GET` | `/api/v1/flights/search` | 출발지/도착지 기준 항공편 목록 검색 |
| **탑승권 (Pass)** | `GET` | `/api/v1/passes` | 사용자의 등록된 보딩패스 목록 조회 |
| | `POST` | `/api/v1/passes/scan-qr` | QR 코드 / OCR 데이터 기반 보딩패스 자동 등록 |
| | `GET` | `/api/v1/passes/download/{id}/pkpass` | **Apple Wallet용 .pkpass 파일 다운로드** |
| **타임라인 (Timeline)** | `GET` | `/api/v1/journeys/{id}/timeline` | 출발/환승/도착 실시간 타임라인 단계 조회 |
| **AI 추천 (AI Engine)** | `POST` | `/api/v1/ai/recommendations` | 날씨, 환승 시간 기반 AI 스마트 추천 생성 |
| **스타일 (Style Engine)** | `GET` | `/api/v1/styles/recommendations` | 목적지 날씨 맞춤 MCM 룩북/아이템 추천 목록 |
| **팝업 스팟 (Spot)** | `GET` | `/api/v1/spots` | 공항 게이트별 MCM 팝업스토어 및 라운지 위치 |
| | `POST` | `/api/v1/spots/claim-coupon` | 팝업 스팟 방문 인증번호/쿠폰 발급 |
| **실시간 스트림** | `GET (SSE)` | `/api/v1/staff/stream` | 스태프 및 실시간 공항 알림 수신 (Server-Sent Events) |

> 📖 *상세 DTO 스키마 및 Swagger 명세는 저장소의 `api-spec.md` 또는 `openapi.json` 파일을 참조하세요.*

---

## 3. 🏛️ 프론트엔드 아키텍처 및 폴더 구조 권장안

디자인 수정이 빈번한 환경을 고려하여 **UI 뷰(Presentational)와 비즈니스 로직(Container/Hooks)을 명확히 분리**합니다.

```
src/
├── assets/                  # 피그마 에셋 및 아이콘
├── components/
│   ├── common/              # 전역 공통 Atomic 컴포넌트
│   │   ├── AppHeader.tsx    # 상단 로고 & 메뉴 버튼 헤더
│   │   ├── BottomNavBar.tsx # 하단 5탭 네비게이션
│   │   ├── Button.tsx       # Primary / Secondary 공통 버튼
│   │   ├── InputField.tsx   # 텍스트/비밀번호 공통 인풋
│   │   └── TabBar.tsx       # 상단 세그먼트 탭
│   └── features/            # 도메인별 세부 UI 컴포넌트
│       ├── auth/            # LoginForm, RegisterForm
│       ├── home/            # WeatherCard, FlightSummaryCard, QuickMenu
│       ├── scanner/         # ViewfinderFrame, OcrBadge
│       ├── timeline/        # TimelineStepCard, WeatherAlertBanner
│       ├── style/           # StyleProductCard, LookbookHeader
│       └── spot/            # SpotLocationCard, CouponModal
├── hooks/                   # API 통신 및 상태 관리 Custom Hooks (로직 격리)
│   ├── useAuth.ts
│   ├── useFlight.ts
│   ├── useBoardingPass.ts
│   └── useStyleEngine.ts
├── pages/                   # 8개 메인 화면 컨테이너
├── services/                # Axios API 클라이언트
│   ├── apiClient.ts
│   └── endpoints/
├── styles/                  # 전역 스타일 및 디자인 토큰
│   ├── tokens.ts            # 색상, 간격, 폰트 정의
│   └── globals.css
└── types/                   # TypeScript 인터페이스 (DTO 매핑)
```

---

## 4. 🎨 디자인 토큰 (Design Tokens)

* **Primary Blue (브랜드 메인)**: `#0169A1` (Hover: `#0284C7`)
* **Secondary Light Blue (배경/카드 톤)**: `#EBF6FD` / `#F0F8FF`
* **MCM Luxury Accent (코냑/골드)**: `#8B4513` / `#B8860B` / `#D97706`
* **Background Default**: `#FFFFFF` / `#F8FAFC`
* **Text Main**: `#1E293B`
* **Text Muted/Sub**: `#64748B` / `#94A3B8`
* **Border Color**: `#E2E8F0`
* **Corner Radius**:
  * 카드: `16px` (`rounded-2xl`)
  * 버튼/인풋: `12px` (`rounded-xl`)
  * 뱃지/칩: `9999px` (`rounded-full`)

---

## 5. 🤖 새 프론트엔드 에이전트 시작 프롬프트

새 저장소에서 `agy`를 실행한 후 아래 문장을 그대로 입력하시면 즉시 완벽하게 작업을 시작합니다:

```text
FRONTEND_HANDOFF.md와 api-spec.md를 기반으로 프론트엔드 프로젝트를 초기화하고, 디자인 토큰과 공통 레이아웃(AppHeader, BottomNavBar)을 구축한 후 홈 화면(HomeScreen)부터 구현해줘.
```
