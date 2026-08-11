# 🚀 MCM Nomad Passport AI - 백엔드 시스템 요구사항 명세서 (PRD for AI)

## 1. 시스템 환경 및 아키텍처
*   **Framework:** Spring Boot
*   **Database:** PostgreSQL (또는 MySQL) on Render DB
*   **Deployment:** Render Web Service
*   **Architecture Pattern:** RESTful API, MVC Pattern (Controller - Service - Repository)

---

## 2. 핵심 도메인 (Entity) 모델링 가이드
AI가 DB 스키마를 설계할 때 참조할 5가지 핵심 도메인입니다.

*   **`Member` (사용자/VIP):** 사용자 기본 정보, VIP 티어, Nomad Miles 잔여 포인트 관리.
*   **`Journey` (여정):** PNR(예약번호), 출발지, 목적지, 출국일시, 항공편 상태.
*   **`Product` (상품):** MCM 카탈로그, 카테고리(방수, 백팩 등), 재고 정보.
*   **`SmartCart` (스마트 장바구니/피팅):** 사용자가 담은 상품 목록, ChoiceFit(피팅 신청 여부) 상태 플래그.
*   **`StoreVisit` (매장 방문 및 체크인):** BLE/NFC 기반 체크인 상태, 어시스턴트 태블릿 연동 상태, 최종 구매 여부.

---

## 3. 단계별 핵심 비즈니스 로직 (`흐름도.png` 및 IA 기반)

### Phase 1: Pre-Flight (출국 전 / 온라인)
*   **보딩패스 스캔 (OCR/PNR):** 사용자가 탑승권을 스캔하면 Vision OCR을 통해 PNR을 추출하고 `Journey` 엔티티에 저장.
*   **데이터 결합 및 큐레이션:** 항공편 일정 + 목적지 날씨(Global Weather API)를 분석하여 `Product` 테이블에서 맞춤형 상품 추천.
*   **의사결정 트리 (ChoiceFit):** 장바구니에 상품을 담은 후 **피팅 신청 여부**를 분기 처리. (신청 O / 신청 X)

### Phase 2: Airport Store (공항 면세점 / 오프라인)
*   **오토 체크인:** 
    *   **정상:** 공항 도착 시 BLE/NFC로 자동 체크인 (웹소켓 또는 FCM 푸시로 매장 직원 태블릿에 알림 전송).
    *   **예외(Fallback):** 블루투스 OFF/위치 권한 거부 시 QR코드 또는 바코드를 통한 수동 체크인 로직 필수.
*   **VIP 스마트 피팅 분기:**
    *   **피팅 신청자:** 매장 어시스턴트 태블릿과 연동하여 자동 피팅 및 추천 진행. 
    *   **피팅 미신청자:** 일반 매장 방문 모드로 전환되며 웰컴 쿠폰 발급 로직 실행.
*   **구매 결제 분기 (ChoiceBuy):**
    *   **구매 O:** 면세 한도 계산 및 즉시 결제 처리.
    *   **구매 X (장바구니 유지):** 상태를 '구매 미완료'로 유지. 추후 재방문(Re-entry Flow) 시 "바로 결제 / 다시 피팅 / 새 상품 보기" 중 선택하도록 분기 제공.

### Phase 3: Post-Flight (귀국 후 / 로열티)
*   **조건부 알림 트리거:** 구매 완료자(구매 이력 존재)에 한하여 목적지 도착 시 현지 가죽 케어 메시지 발송 (Push Notification 스케줄러).
*   **로열티 적립:** 마일리지(`Nomad Miles`) 적립 트랜잭션 발생.

---

## 4. 주요 REST API 엔드포인트 설계 (초안)

| Domain | HTTP Method | Endpoint | Description | 연동 기술/API |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | POST | `/api/v1/auth/login` | 앱 로그인 및 노마드 허브 접속 | - |
| **Journey** | POST | `/api/v1/journey/scan` | 보딩패스 OCR 스캔 및 여정 등록 | Vision OCR |
| **Journey** | GET | `/api/v1/journey/analysis/{journeyId}` | 목적지 기후 및 여행 분석 데이터 반환 | Global Weather API |
| **Cart** | POST | `/api/v1/cart/add` | 스마트 장바구니 상품 추가 | - |
| **Cart** | PUT | `/api/v1/cart/choice-fit` | ChoiceFit(피팅 신청 여부) 상태 업데이트 | - |
| **Store** | POST | `/api/v1/store/check-in` | BLE/NFC 또는 수동(QR) 체크인 처리 | WebSocket (Staff) |
| **Order** | POST | `/api/v1/order/checkout` | 선속 결제 및 면세 한도 계산 적용 | Duty-Free Calc |
| **Care** | GET | `/api/v1/care/visetos-spots` | 현지 비세토스 스팟(MCM 매장) 탐색 | Google Maps API |
