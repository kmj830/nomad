# 🚀 Herstory AI - 프론트엔드 개발 공식 인계 명세서 (Frontend Handoff Guide)

> **이 문서는 프론트엔드 프로젝트(React, Next.js, React Native Expo 등)를 구축할 때, 백엔드 실제 엔드포인트 연동과 Figma 디자인 컨텍스트를 정확하게 이어받아 개발할 수 있도록 작성된 공식 인계 문서입니다.**

---

## ⚠️ 최우선 개발 원칙 (Critical Principles)

1. **브랜드/프로젝트 명칭**: 공식 명칭은 **`Herstory`** (Herstory AI / Herstory Club)입니다. (구 명칭 Nomad 표기 금지)
2. **Figma 1:1 준수**: 피그마 디자인을 단일 진실 원천(Single Source of Truth)으로 삼으며, 임의의 색상/여백/레이아웃 변형을 엄격히 금지합니다.
3. **미완성 디자인 구현 금지**: 피그마에 정의되지 않은 서브 화면이나 임의의 상태는 상상하여 개발하지 않고, 피그마 확정 화면 8개만 정밀 구현합니다.

---

## 1. 🎨 Figma 디자인 연동 정보 (Design Context)

* **Figma 파일 URL**: `https://www.figma.com/design/u8brOGUEOQxG45WNSXW0OW/%EC%A0%9C%EB%AA%A9-%EC%97%86%EC%9D%8C?node-id=0-1`
* **File Key**: `u8brOGUEOQxG45WNSXW0OW`
* **모바일 기준 해상도**: **402 × 874 px (Apple iPhone 17 규격)**

### 📱 8개 주요 확정 화면 매핑 테이블

| 번호 | 화면 명칭 | Figma Node ID | 프론트엔드 컴포넌트 | 화면 핵심 구성 요소 |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **로그인** | `1:2` | `LoginScreen.tsx` | Herstory 로고, 이메일/비밀번호 입력, 로그인 버튼, VIP 안내 |
| 2 | **회원가입** | `15:1025` | `RegisterScreen.tsx` | 뒤로가기 헤더, 가입 폼 필드(이메일, 비밀번호, 이름, 연락처), 약관동의 |
| 3 | **홈 (대시보드)** | `1:26` | `HomeScreen.tsx` | 상단 Herstory 헤더, 실시간 날씨 위젯, 활성 보딩패스 요약 카드, 퀵 메뉴, 하단 탭바 |
| 4 | **AI 라이브카드** | `6:12` | `AiLiveCardScreen.tsx` | 항공편 실시간 상태, 남은 탑승시간 카운트다운, 탑승구(Gate) 안내, 라운지 현황 |
| 5 | **보딩패스 스캔** | `3:315` | `BoardingPassScanScreen.tsx` | PNR 입력 / OCR 카메라 스캔 뷰파인더 가이드 |
| 6 | **여정 타임라인** | `10:801` | `JourneyTimelineScreen.tsx` | 목적지 기상 알림 배너, 3단계 여정 타임라인(출발-비행-도착), 스타일 룩북 진입 링크 |
| 7 | **스타일 엔진 룩북** | `16:1246` | `StyleEngineScreen.tsx` | 목적지 기후 맞춤 MCM 패키지 룩북 헤더, 큐레이션 아이템 카드 목록 |
| 8 | **공항 팝업 & 스팟** | `63:471` / `94:15` | `AirportPopupScreen.tsx` | 공항 맵 뷰, MCM 스토어 및 게이트 위치, 체크인/쿠폰 발급 CTA |

---

## 2. 🔌 백엔드 실제 REST API 명세 (Spring Boot 3.x)

* **로컬 백엔드 서버**: `http://localhost:8080`
* **배포 서버**: `https://mcm-nomad-backend.onrender.com`
* **CORS 설정**: 전체 출처 허용 (`@CrossOrigin(origins = "*")`)

### 📡 실제 엔드포인트 목록

| 도메인 | Method | Endpoint | 기능 설명 |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/v1/auth/register` | 신규 회원가입 (이메일, 비밀번호, 이름, 연락처) |
| | `POST` | `/api/v1/auth/login` | 로그인 (이메일, 비밀번호 $\rightarrow$ VIP 티어, Herstory Miles 반환) |
| **Journey** | `POST` | `/api/v1/journey/scan` | 탑승권 OCR/PNR 스캔 여정 등록 |
| | `GET` | `/api/v1/journey/live-card/{journeyId}` | 실시간 탑승 카운트다운/게이트/라운지 정보 |
| | `GET` | `/api/v1/journey/analysis/{journeyId}` | 목적지 기후 분석 및 맞춤 큐레이션 추천 |
| | `GET` | `/api/v1/journey/apple-wallet-pass/download/{journeyId}.pkpass` | Apple Wallet .pkpass 파일 다운로드 |
| **Cart** | `POST` | `/api/v1/cart/add` | 스마트 장바구니 상품 추가 |
| | `PUT` | `/api/v1/cart/choice-fit` | ChoiceFit VIP 피팅 신청 상태 토글 |
| | `GET` | `/api/v1/cart/my?memberId={memberId}` | 내 장바구니 조회 |
| **Store** | `POST` | `/api/v1/store/check-in` | 면세점 매장 체크인 & 웰컴 쿠폰 발급 |
| | `GET` | `/api/v1/store/re-entry-options/{memberId}` | 재방문 고객 분기 옵션 조회 |
| | `GET` | `/api/v1/store/notifications/stream` | 실시간 SSE 알림 스트림 (Server-Sent Events) |
| **Order** | `POST` | `/api/v1/order/checkout` | 면세 결제 및 Herstory Miles 적립 |
| **Care** | `GET` | `/api/v1/care/visetos-spots?memberId={memberId}` | 현지 MCM 스팟 & Care Desk 정보 |
| | `GET` | `/api/v1/care/google-maps?destination={city}` | Google Maps 위치 좌표 및 길안내 |
| | `GET` | `/api/v1/care/ai-care-tip` | OpenAI GPT-4o 맞춤 가죽 케어 가이드 |
| | `POST` | `/api/v1/care/stamp-checkin` | 시티 스탬프 획득 및 보너스 마일리지 적립 |
| **Flight** | `GET` | `/api/v1/flight/lookup?flightNumber={code}` | 편명(예: KE651, OZ741, JL92) 실시간 운항 정보 |
| **Health** | `GET` | `/api/v1/health` | 백엔드 시스템 및 연동 API 헬스 체크 |

---

## 3. 🤖 프론트엔드 AI 에이전트 시작 프롬프트

프론트엔드 작업 시 `FRONTEND_AI_INSTRUCTIONS.md` 문서를 전달하거나 아래 프롬프트를 입력하십시오:

```text
FRONTEND_AI_INSTRUCTIONS.md를 최우선 지침으로 삼아 개발해줘.
1. 서비스명은 Nomad가 아닌 'Herstory'로 통일할 것.
2. 피그마 디자인을 1:1로 준수하고 임의로 디자인을 수정/창작하지 말 것.
3. 피그마에 없는 미완성 화면/상태는 임의로 개발하지 말 것.
4. 백엔드 실제 엔드포인트에 100% 매핑하여 HomeScreen부터 구축을 시작해줘.
```
