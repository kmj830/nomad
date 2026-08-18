# 🚀 Herstory AI — Backend REST API Specification

> **버전**: `v1.0.0`  
> **생성일자**: 2026-08-15  
> **기반 프로젝트**: Herstory AI (Spring Boot 3.x / Java 17)  
> **문서 목적**: 프론트엔드(React/Next.js/iOS), 매장 어시스턴트 태블릿, 외부 시스템과의 완벽한 연동을 위한 전체 API 엔드포인트, Request/Response DTO 스키마, 파라미터, HTTP 메서드 및 상태 코드 표준 명세서

---

## 📑 목차 (Table of Contents)

1. [시스템 환경 및 공통 규격](#1-시스템-환경-및-공통-규격)
2. [도메인 열거형 타입 (Enum Definitions)](#2-도메인-열거형-타입-enum-definitions)
3. [공통 에러 응답 규격 (Error Response)](#3-공통-에러-응답-규격-error-response)
4. [전체 API 엔드포인트 요약 인덱스](#4-전체-api-엔드포인트-요약-인덱스)
5. [상세 API 명세](#5-상세-api-명세)
   - [1. Auth API (인증 & VIP Herstory 허브)](#51-auth-api-인증--vip-herstory-허브)
   - [2. Journey API (여정 & 보딩패스 & Apple Wallet)](#52-journey-api-여정--보딩패스--apple-wallet)
   - [3. Cart API (스마트 장바구니 & ChoiceFit)](#53-cart-api-스마트-장바구니--choicefit)
   - [4. Store API (공항 면세점 체크인 & 매장 직원 태블릿 SSE)](#54-store-api-공항-면세점-체크인--매장-직원-태블릿-sse)
   - [5. Order API (면세 결제 & 마일리지 적립)](#55-order-api-면세-결제--마일리지-적립)
   - [6. Care API (현지 비세토스 스팟 & AI 가죽 케어)](#56-care-api-현지-비세토스-스팟--ai-가죽-케어)
   - [7. Flight API (실시간 항공편 운항 정보)](#57-flight-api-실시간-항공편-운항-정보)
   - [8. System Health API (시스템 모니터링)](#58-system-health-api-시스템-모니터링)
6. [DTO 스키마 사전 (Data Transfer Objects)](#6-dto-스키마-사전-data-transfer-objects)

---

## 1. 시스템 환경 및 공통 규격

### 1.1 서버 Base URL

| 환경 | Base URL | 설명 |
| :--- | :--- | :--- |
| **Production (Live)** | `https://herstory-backend.onrender.com` | Render 클라우드 배포 서버 |
| **Swagger UI** | `https://herstory-backend.onrender.com/swagger-ui/index.html` | 대화형 API 테스트 콘솔 |
| **OpenAPI Spec (JSON)** | `https://herstory-backend.onrender.com/v3/api-docs` | OpenAPI 3.0 JSON 스키마 |
| **Local Development** | `http://localhost:8080` | 로컬 개발 서버 |

### 1.2 공통 HTTP 헤더 (Common Headers)

| 헤더명 | 기본값 / 예시 | 필수 여부 | 설명 |
| :--- | :--- | :---: | :--- |
| `Content-Type` | `application/json` | Y (바이너리/SSE 제외) | 요청 본문 미디어 타입 |
| `Accept` | `application/json` | N | 수신 희망 데이터 포맷 |

> **CORS 정책**: 모든 도메인(`*`) 및 모든 HTTP 메서드(GET, POST, PUT, DELETE, OPTIONS, PATCH), 모든 헤더에 대해 Cross-Origin 통신이 허용되어 있습니다.

---

## 2. 도메인 열거형 타입 (Enum Definitions)

### 2.1 VipTier (회원 VIP 등급)
| Enum 값 | 설명 | 혜택 및 할인율 |
| :--- | :--- | :--- |
| `SILVER` | 실버 회원 | 면세 5% 할인 |
| `GOLD` | 골드 회원 | 면세 10% 할인 |
| `PLATINUM` | 플래티넘 회원 | 면세 12% 할인 |
| `VIP` | 최상위 VIP Herstory 회원 | 면세 15% 할인 + 라운지 전용 혜택 |

### 2.2 FlightStatus (항공편 운항 상태)
| Enum 값 | 설명 |
| :--- | :--- |
| `SCHEDULED` | 정시 운항 예정 |
| `BOARDING` | 탑승 수속 중 |
| `COMPLETED` | 운항 완료 / 착륙 |
| `CANCELLED` | 결항 / 취소 |

### 2.3 CartStatus (장바구니 상태)
| Enum 값 | 설명 |
| :--- | :--- |
| `IN_CART` | 담김 (진행 중) |
| `CHECKED_OUT` | 결제 완료 |
| `CANCELLED` | 취소됨 |

### 2.4 OrderStatus (주문 상태)
| Enum 값 | 설명 |
| :--- | :--- |
| `PAID` | 결제 및 마일리지 적립 완료 |
| `PENDING` | 결제 대기 중 |
| `CANCELLED` | 주문 취소됨 |

### 2.5 ProductCategory (상품 카테고리)
| Enum 값 | 카테고리 한글명 | 추천 기후/상황 |
| :--- | :--- | :--- |
| `WATERPROOF` | 워터프루프 & 방수 컬렉션 | 고습도, 열대성 스콜, 우천 |
| `TRAVEL_BAG` | 트래블 백 & 캐리어 | 장거리 이동, 기내 수하물 |
| `LEATHER_CARE` | 럭셔리 레더 케어 키트 | 건조/습도 변화 시 가죽 보호 |
| `ACCESSORY` | 트래블 악세서리 & 소품 | 공항 VIP 편의, 여권 케이스 |
| `CLOTHING` | 럭셔리 어패럴 & 트렌치 | 기내 체온 조절, 아우터 |

### 2.6 CheckInType (체크인 방식)
| Enum 값 | 설명 |
| :--- | :--- |
| `BLE` | 비콘(Bluetooth Low Energy) 자동 감지 체크인 |
| `NFC` | 스마트폰 NFC 터치 태그 체크인 |
| `QR` | 모바일 패스포트 QR 코드 스캔 체크인 |
| `MANUAL` | 매장 직원 수동 체크인 처리 |

### 2.7 CheckInStatus (체크인 상태)
| Enum 값 | 설명 |
| :--- | :--- |
| `COMPLETED` | 체크인 성공 및 입장 완료 |
| `PENDING` | 체크인 인증 대기 |
| `EXPIRED` | 체크인 유효 시간 만료 |

### 2.8 PurchaseStatus (매장 내 구매 상태)
| Enum 값 | 설명 |
| :--- | :--- |
| `PURCHASED` | 면세품 결제 완료 |
| `PENDING_REENTRY` | 미결제 퇴장 후 재방문 대기 (Re-entry 대상) |
| `ABANDONED` | 피팅/장바구니 이탈 |

---

## 3. 공통 에러 응답 규격 (Error Response)

모든 예외 상황 발생 시 `GlobalExceptionHandler`를 통해 표준 포맷으로 반환됩니다.

### 3.1 ErrorResponse Schema
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "해당 회원을 찾을 수 없습니다: memberId=999",
  "timestamp": "2026-08-15T17:30:00.123456"
}
```

### 3.2 HTTP 에러 상태 코드 매핑
| HTTP 상태 코드 | 예외 클래스 | 원인 및 설명 |
| :---: | :--- | :--- |
| `400 Bad Request` | `IllegalArgumentException` | 필수 파라미터 누락, 존재하지 않는 ID 참조 등 잘못된 요청 |
| `409 Conflict` | `IllegalStateException` | 중복 체크인, 빈 장바구니 결제 시도 등 리소스 상태 불일치 |
| `500 Internal Server Error` | `Exception` | 서버 내부 로직 처리 실패 및 외부 API 통신 장애 |

---

## 4. 전체 API 엔드포인트 요약 인덱스

| # | 도메인 | HTTP | 엔드포인트 | 요약 설명 |
| :-: | :--- | :-: | :--- | :--- |
| **1** | Auth | `POST` | `/api/v1/auth/register` | 신규 회원가입 & 웰컴 마일리지 지급 |
| **2** | Auth | `POST` | `/api/v1/auth/login` | 앱 로그인 (이메일, 비밀번호) |
| **3** | Auth | `POST` | `/api/v1/auth/phone/send-code` | 회원가입 휴대폰 SMS 인증번호 발송 |
| **4** | Auth | `POST` | `/api/v1/auth/phone/verify-code` | 휴대폰 SMS 인증번호 검증 |
| **5** | Auth | `POST` | `/api/v1/auth/password/reset` | 비밀번호 찾기 및 재설정 |
| **6** | Journey | `POST` | `/api/v1/journey/scan` | 보딩패스 Vision OCR 스캔 & 여정 등록 |
| **4** | Journey | `GET` | `/api/v1/journey/{journeyId}` | 여정 기본 상세 단건 조회 |
| **5** | Journey | `GET` | `/api/v1/journey/analysis/{journeyId}` | 목적지 기후 분석 & 맞춤 럭셔리 상품 Curation |
| **4** | Journey | `GET` | `/api/v1/journey/live-card/{journeyId}` | SCR-102 실시간 AI 라이브 카드 위젯 |
| **5** | Journey | `GET` | `/api/v1/journey/apple-wallet-pass/{journeyId}` | SCR-201 Apple Wallet 패스 메타데이터 조회 |
| **6** | Journey | `GET` | `/api/v1/journey/apple-wallet-pass/download/{journeyId}` | Apple Wallet (.pkpass) 바이너리 다운로드 |
| **7** | Journey | `GET` | `/api/v1/journey/apple-wallet-pass/download-file/{journeyId}` | iOS Safari 호환 범용 .pkpass 파일 다운로드 |
| **8** | Cart | `POST` | `/api/v1/cart/add` | 스마트 장바구니 상품 추가 |
| **9** | Cart | `PUT` | `/api/v1/cart/choice-fit` | ChoiceFit (VIP 피팅 신청) 여부 업데이트 |
| **10** | Cart | `GET` | `/api/v1/cart/my` | 내 스마트 장바구니 및 품목 목록 조회 |
| **11** | Store | `POST` | `/api/v1/store/check-in` | 공항 면세점 BLE/NFC/QR 오토 체크인 |
| **12** | Store | `GET` | `/api/v1/store/re-entry-options/{memberId}` | 미구매 고객 재방문(Re-entry) 분기 조회 |
| **13** | Store | `GET` | `/api/v1/store/notifications/stream` | SCR-402 매장 직원 태블릿 실시간 SSE 스트림 |
| **14** | Order | `POST` | `/api/v1/order/checkout` | 면세 Fast Checkout 결제 & 마일리지 적립 |
| **15** | Care | `GET` | `/api/v1/care/visetos-spots` | 목적지 현지 전 브랜드 럭셔리 스팟 & 케어 가이드 조회 |
| **16** | Care | `GET` | `/api/v1/care/google-maps` | SCR-502 Google Maps 실시간 멀티 브랜드 매장 탐색 |
| **17** | Care | `GET` | `/api/v1/care/ai-care-tip` | SCR-501 OpenAI GPT-4o 다국어 명품 가죽 케어 팁 |
| **18** | Care | `POST` | `/api/v1/care/push-test` | FCM 디바이스 푸시 알림 발송 테스트 |
| **19** | Care | `POST` | `/api/v1/care/stamp-checkin` | SCR-502 시티 패스포트 스탬프 획득 & 적립 |
| **20** | Flight | `GET` | `/api/v1/flight/lookup` | 편명 기준 실시간 항공편 운항 정보 조회 |
| **21** | Health | `GET` | `/api/v1/health` | 시스템 헬스 ("status": "ok") 모니터링 |
| **22** | Preflight | `GET` | `/api/v1/preflight/hub` | 프론트엔드 허브 종합 데이터 조회 |
| **23** | Preflight | `GET` | `/api/v1/preflight/live-card` | 프론트엔드 AI 라이브 카드 조회 |
| **24** | Style | `GET` | `/api/v1/style/popup-spots` | 팝업 스팟 목록 조회 |
| **25** | Postflight| `GET` | `/api/v1/postflight/miles/{memberId}` | 회원 마일리지/등급 조회 |
| **26** | Airport | `POST`| `/api/v1/airport/{journeyId}/fitting` | VIP 피팅 시작 토글 |

---

## 5. 상세 API 명세

---

### 5.1 Auth API (인증 & VIP Herstory 허브)

#### `POST /api/v1/auth/register`
- **설명**: 이메일, 비밀번호, 이름, 연락처를 통해 신규 회원을 등록하고 웰컴 마일리지(1,000 마일)를 지급합니다.
- **Content-Type**: `application/json`

##### Request Body (`AuthDto.RegisterRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| `email` | `String` | Y | - | 회원 이메일 (예: `user@example.com`) |
| `password` | `String` | Y | - | 계정 비밀번호 (예: `password123!`) |
| `name` | `String` | Y | - | 회원 이름 (예: `홍길동`) |
| `phone` | `String` | N | - | 회원 연락처 (예: `010-1234-5678`) |

```json
{
  "email": "user@example.com",
  "password": "password123!",
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

##### Response Body (`200 OK` - `AuthDto.RegisterResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `memberId` | `Long` | 생성된 회원 고유 식별자 ID |
| `email` | `String` | 회원 이메일 |
| `name` | `String` | 회원 이름 |
| `vipTier` | `String (Enum)` | 초기 VIP 등급 (`SILVER`) |
| `nomadMiles` | `Long` | 초기 부여된 Herstory 마일리지 잔액 (1,000) |
| `message` | `String` | 가입 완료 안내 메시지 |

```json
{
  "memberId": 4,
  "email": "user@example.com",
  "name": "홍길동",
  "vipTier": "SILVER",
  "nomadMiles": 1000,
  "message": "Herstory Club 회원가입이 완료되었습니다. (웰컴 1,000 마일리지 적립)"
}
```

---

#### `POST /api/v1/auth/login`
- **설명**: 회원 이메일과 비밀번호를 통해 로그인하며 VIP 등급(VipTier) 및 보유 Herstory Miles를 반환합니다.
- **Content-Type**: `application/json`

##### Request Body (`AuthDto.LoginRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| `email` | `String` | Y | - | 회원 이메일 (예: `vip@herstory.com`) |
| `password` | `String` | Y | - | 계정 비밀번호 (예: `1234`) |

```json
{
  "email": "vip@herstory.com",
  "password": "1234"
}
```

##### Response Body (`200 OK` - `AuthDto.LoginResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `memberId` | `Long` | 회원 고유 식별자 ID |
| `email` | `String` | 회원 이메일 |
| `name` | `String` | 회원 이름 |
| `vipTier` | `String (Enum)` | VIP 등급 (`SILVER`, `GOLD`, `PLATINUM`, `VIP`) |
| `nomadMiles` | `Long` | 보유 Herstory 마일리지 잔액 |
| `message` | `String` | 로그인 성공 안내 메시지 |

```json
{
  "memberId": 1,
  "email": "vip@herstory.com",
  "name": "김노마드 (VIP)",
  "vipTier": "VIP",
  "nomadMiles": 15000,
  "message": "Herstory Hub에 성공적으로 접속되었습니다."
}
```

---

### 5.2 Journey API (여정 & 보딩패스 & Apple Wallet)

#### `POST /api/v1/journey/scan`
- **설명**: 탑승권 Vision OCR 스캔 결과 또는 PNR 코드를 입력받아 비행 여정을 등록합니다.
- **Content-Type**: `application/json`

##### Request Body (`JourneyDto.ScanRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `memberId` | `Long` | Y | - | 회원 ID |
| `pnr` | `String` | N | `"HST999"` | 예약 번호(PNR) 6자리 |
| `rawOcrText` | `String` | N | - | 보딩패스 OCR 원문 텍스트 |
| `origin` | `String` | N | `"ICN"` | 출발 공항 IATA 코드 |
| `destination` | `String` | N | `"BKK"` | 도착 공항 IATA 코드 |

```json
{
  "memberId": 1,
  "pnr": "HST999",
  "rawOcrText": "BOARDING PASS PASSENGER: KIM/NOMAD FLIGHT: KE651 ICN BKK SEAT: 02A",
  "origin": "ICN",
  "destination": "BKK"
}
```

##### Response Body (`200 OK` - `JourneyDto.ScanResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `journeyId` | `Long` | 생성된 여정 ID |
| `pnr` | `String` | PNR 코드 |
| `origin` | `String` | 출발지 공항 |
| `destination` | `String` | 목적지 공항 |
| `departureDateTime` | `LocalDateTime` | 출발 예정 일시 (ISO-8601) |
| `flightStatus` | `String (Enum)` | 운항 상태 (`SCHEDULED`, `BOARDING`, `COMPLETED`, `CANCELLED`) |
| `message` | `String` | 처리 결과 메시지 |

```json
{
  "journeyId": 1,
  "pnr": "HST999",
  "origin": "ICN",
  "destination": "BKK",
  "departureDateTime": "2026-08-15T21:30:00",
  "flightStatus": "SCHEDULED",
  "message": "보딩패스 OCR 스캔 완료! PNR [HST999] 여정이 등록되었습니다."
}
```

---

#### `GET /api/v1/journey/{journeyId}`
- **설명**: 여정 ID를 통해 PNR, 출발/도착 공항, 출발 일시, 운항 상태 및 기후 분석 정보를 조회합니다. (호환성을 위해 `/api/v1/journeys/{journeyId}` 지원)
- **Content-Type**: `application/json`

##### Request Path Parameter
| 파라미터명 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `journeyId` | `Long` | Y | 조회할 여정 식별자 ID |

##### Response Body (`200 OK` - `JourneyDto.JourneyResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `journeyId` | `Long` | 여정 ID |
| `memberId` | `Long` | 여정 소유 회원 ID |
| `memberName` | `String` | 회원 이름 |
| `pnr` | `String` | 6자리 PNR 코드 |
| `origin` | `String` | 출발 공항 |
| `destination` | `String` | 도착 공항 |
| `departureDateTime` | `LocalDateTime` | 출발 예정 일시 |
| `flightStatus` | `String (Enum)` | 운항 상태 (`SCHEDULED`, `BOARDING`, `COMPLETED`, `CANCELLED`) |
| `destinationWeather` | `String` | 목적지 날씨 요약 |
| `recommendationReason` | `String` | 맞춤 큐레이션 추천 사유 |

```json
{
  "journeyId": 1,
  "memberId": 1,
  "memberName": "김노마드 (VIP)",
  "pnr": "HST999",
  "origin": "ICN",
  "destination": "BKK",
  "departureDateTime": "2026-08-20T14:30:00",
  "flightStatus": "SCHEDULED",
  "destinationWeather": "열대성 스콜 (기온 32°C, 습도 85%)",
  "recommendationReason": "목적지 비/습도 기후에 적합한 럭셔리 방수 레더 컬렉션 맞춤 제안"
}
```

---

#### `GET /api/v1/journey/analysis/{journeyId}`
- **설명**: 목적지의 실시간 기후 데이터(Open-Meteo)와 OpenAI 분석을 결합하여 맞춤형 룩북 및 추천 상품을 제공합니다. (Spring Cache 0ms 응답 최적화)

##### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `journeyId` | `Long` | Y | 여정 ID (예: `1`) |

##### Response Body (`200 OK` - `JourneyDto.JourneyAnalysisResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `journeyId` | `Long` | 여정 ID |
| `destination` | `String` | 목적지 도시/공항 명칭 |
| `weatherInfo` | `String` | 실시간 기온, 습도, 강수량 요약 |
| `climateSummary` | `String` | 기후 분석 요약 멘트 |
| `recommendationReason` | `String` | AI 큐레이션 추천 사유 |
| `recommendedProducts` | `List<Product>` | 추천 럭셔리 상품 객체 목록 |

```json
{
  "journeyId": 1,
  "destination": "BKK (방콕 수완나품)",
  "weatherInfo": "기온 32.5°C, 습도 88%, 열대성 스콜 잦음",
  "climateSummary": "고온 다습한 열대 몬순 기후로 급작스러운 스콜과 강한 자외선이 예상됩니다.",
  "recommendationReason": "방콕의 고습도 환경에 대비한 럭셔리 방수 에센셜 라인업과 쾌적한 이동을 위한 가벼운 트래블 백팩을 제안합니다.",
  "recommendedProducts": [
    {
      "id": 1,
      "name": "럭셔리 클래식 방수 스타크 백팩 (Medium)",
      "category": "BACKPACK",
      "price": 1250000.00,
      "stock": 45,
      "imageUrl": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
      "description": "열대 스콜로부터 소지품을 안전하게 보호하는 방수 코팅 캔버스 백팩",
      "isVipExclusive": false
    },
    {
      "id": 2,
      "name": "럭셔리 레더 러기지 여권 케이스",
      "category": "ACCESSORY",
      "price": 380000.00,
      "stock": 80,
      "imageUrl": "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80",
      "description": "VIP 노마드를 위한 시그니처 가죽 여권 지갑",
      "isVipExclusive": false
    }
  ]
}
```

---

#### `GET /api/v1/journey/live-card/{journeyId}`
- **설명**: SCR-102 실시간 항공편 탑승 카운트다운(분), 게이트 번호, 공항 VIP 라운지 대기 현황 및 안내 메시지를 반환합니다.

##### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `journeyId` | `Long` | Y | 여정 ID (예: `1`) |

##### Response Body (`200 OK` - `JourneyDto.LiveCardResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `journeyId` | `Long` | 여정 ID |
| `pnr` | `String` | PNR 코드 |
| `destination` | `String` | 목적지 공항 |
| `departureDateTime` | `LocalDateTime` | 출발 일시 |
| `remainingMinutesToDeparture` | `long` | 탑승까지 남은 시간 (분 단위) |
| `gate` | `String` | 출발 게이트 번호 (예: `"Gate 24 (T1)"`) |
| `flightStatus` | `String (Enum)` | 운항 상태 |
| `loungeLocation` | `String` | 공항 라운지 위치 안내 |
| `loungeWaitTime` | `String` | 라운지 현재 대기 상황 |
| `liveGuideMessage` | `String` | 고객 상황별 실시간 맞춤 가이드 멘트 |

```json
{
  "journeyId": 1,
  "pnr": "HST999",
  "destination": "BKK",
  "departureDateTime": "2026-08-15T21:30:00",
  "remainingMinutesToDeparture": 125,
  "gate": "Gate 24 (T1)",
  "flightStatus": "SCHEDULED",
  "loungeLocation": "인천공항 제1여객터미널 4층 Herstory VIP 라운지",
  "loungeWaitTime": "대기 없음 (즉시 입장 가능)",
  "liveGuideMessage": "탑승까지 125분 남았습니다. 24번 게이트 인근 Herstory 면세 부티크에서 예약하신 피팅 상품을 확인해보세요."
}
```

---

#### `GET /api/v1/journey/apple-wallet-pass/{journeyId}`
- **설명**: 비행 탑승권 및 VIP 여정 보딩패스를 Apple Wallet PKPass 포맷 JSON 메타데이터로 조회합니다.

##### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `journeyId` | `Long` | Y | 여정 ID |

##### Response Body (`200 OK` - `PassKitService.AppleWalletPassResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `passTypeIdentifier` | `String` | Apple Pass Type ID (`pass.com.herstory.passport`) |
| `serialNumber` | `String` | 유니크 패스 시리얼 번호 |
| `teamIdentifier` | `String` | Apple Developer Team ID |
| `organizationName` | `String` | 발급 조직명 (`Herstory`) |
| `description` | `String` | 패스 상세 설명 |
| `logoText` | `String` | 패스 로고 텍스트 |
| `boardingPassDetails` | `Map<String, Object>` | PKPass 보딩패스 세부 필드 맵 |
| `pkpassDownloadUrl` | `String` | 직접 다운로드 URL 링크 |

```json
{
  "passTypeIdentifier": "pass.com.herstory.passport",
  "serialNumber": "HERSTORY-PASS-HST999-1",
  "teamIdentifier": "HST99PASSKIT",
  "organizationName": "Herstory",
  "description": "Herstory VIP Flight Boarding & Fitting Pass",
  "logoText": "HERSTORY AI",
  "boardingPassDetails": {
    "transitType": "PKTransitTypeAir",
    "headerFields": {
      "key": "gate",
      "label": "GATE",
      "value": "Gate 24 (T1)"
    },
    "primaryFields": {
      "key": "destination",
      "label": "DESTINATION",
      "value": "BKK (방콕 수완나품)"
    },
    "secondaryFields": {
      "key": "pnr",
      "label": "PNR REQ",
      "value": "HST999"
    },
    "auxiliaryFields": {
      "key": "tier",
      "label": "VIP MEMBERSHIP",
      "value": "VIP HERSTORY"
    }
  },
  "pkpassDownloadUrl": "/api/v1/journey/apple-wallet-pass/download/HST999.pkpass"
}
```

---

#### `GET /api/v1/journey/apple-wallet-pass/download/{journeyId}` (또는 `.pkpass`)
- **설명**: iOS 아이폰 지갑(Wallet) 앱에 즉시 추가할 수 있는 `.pkpass` 바이너리 ZIP 스트림을 다운로드합니다.
- **Path Parameter**: `journeyId` (`String`)
- **Produces**: `application/vnd.apple.pkpass`
- **Response Headers**: `Content-Disposition: attachment; filename="herstory-boarding-pass.pkpass"`
- **Response Body**: Binary (`byte[]`)

---

#### `GET /api/v1/journey/apple-wallet-pass/download-file/{journeyId}` (또는 `.zip`)
- **설명**: iOS Safari 보안 차단 없이 아이폰 파일 앱(Downloads)으로 직접 내려받을 수 있는 범용 바이너리 다운로드 엔드포인트입니다.
- **Path Parameter**: `journeyId` (`String`)
- **Produces**: `application/octet-stream`
- **Response Headers**: `Content-Disposition: attachment; filename="herstory-pass-{journeyId}.pkpass"`
- **Response Body**: Binary (`byte[]`)

---

### 5.3 Cart API (스마트 장바구니 & ChoiceFit)

#### `POST /api/v1/cart/add`
- **설명**: 추천 럭셔리 상품을 스마트 장바구니에 추가합니다.
- **Content-Type**: `application/json`

##### Request Body (`CartDto.AddItemRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `memberId` | `Long` | Y | - | 회원 ID |
| `productId` | `Long` | Y | - | 상품 ID |
| `quantity` | `Integer` | N | `1` | 수량 |

```json
{
  "memberId": 1,
  "productId": 1,
  "quantity": 1
}
```

##### Response Body (`200 OK` - `CartDto.CartResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `cartId` | `Long` | 장바구니 고유 ID |
| `memberId` | `Long` | 회원 ID |
| `choiceFit` | `Boolean` | VIP 사전 피팅 신청 활성화 여부 |
| `status` | `String (Enum)` | 장바구니 상태 (`IN_CART`, `CHECKED_OUT`, `CANCELLED`) |
| `items` | `List<ItemDetail>` | 장바구니 담긴 품목 목록 |
| `totalPrice` | `BigDecimal` | 품목 총합 금액 |

```json
{
  "cartId": 1,
  "memberId": 1,
  "choiceFit": false,
  "status": "IN_CART",
  "items": [
    {
      "cartItemId": 1,
      "productId": 1,
      "productName": "럭셔리 클래식 방수 스타크 백팩 (Medium)",
      "category": "BACKPACK",
      "price": 1250000.00,
      "quantity": 1,
      "imageUrl": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80"
    }
  ],
  "totalPrice": 1250000.00
}
```

---

#### `PUT /api/v1/cart/choice-fit`
- **설명**: 스마트 장바구니 내 ChoiceFit(면세 부티크 방문 전 사전 피팅 준비 신청) 플래그를 변경합니다.
- **Content-Type**: `application/json`

##### Request Body (`CartDto.ChoiceFitRequest`)
| 필드명 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `memberId` | `Long` | Y | 회원 ID |
| `choiceFit` | `Boolean` | Y | 피팅 신청 여부 (`true` / `false`) |

```json
{
  "memberId": 1,
  "choiceFit": true
}
```

##### Response Body (`200 OK` - `CartDto.CartResponse`)
- 반환 형식은 `POST /api/v1/cart/add` 응답과 동일 (`choiceFit: true` 반영).

---

#### `GET /api/v1/cart/my`
- **설명**: 회원의 활성화된 스마트 장바구니 및 품목 목록을 조회합니다. (매장 직원 태블릿에서도 피팅 준비 목록 확인 시 호출)

##### Query Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `memberId` | `Long` | Y | 회원 ID (예: `1`) |

##### Response Body (`200 OK` - `CartDto.CartResponse`)
- 반환 형식은 `CartDto.CartResponse` 스키마와 동일.

---

### 5.4 Store API (공항 면세점 체크인 & 매장 직원 태블릿 SSE)

#### `POST /api/v1/store/check-in`
- **설명**: 고객이 공항 면세 부티크에 접근할 때 BLE 비콘, NFC 태그 또는 QR 스캔을 통해 오토 체크인을 수행합니다. 매장 직원 태블릿에 실시간 SSE 알림이 전송되고 웰컴 쿠폰 메시지가 생성됩니다.
- **Content-Type**: `application/json`

##### Request Body (`StoreDto.CheckInRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `memberId` | `Long` | Y | - | 회원 ID |
| `checkInType` | `String (Enum)` | N | `BLE` | 체크인 방식 (`BLE`, `NFC`, `QR`, `MANUAL`) |
| `qrCode` | `String` | N | - | QR 체크인 시 스캔된 코드값 |

```json
{
  "memberId": 1,
  "checkInType": "BLE",
  "qrCode": "HST-VIP-GATE24"
}
```

##### Response Body (`200 OK` - `StoreDto.CheckInResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `visitId` | `Long` | 매장 방문 기록 ID |
| `memberId` | `Long` | 회원 ID |
| `memberName` | `String` | 회원 이름 |
| `vipTier` | `String` | 회원 VIP 등급 |
| `checkInType` | `String (Enum)` | 체크인 방식 |
| `checkInStatus` | `String (Enum)` | 체크인 상태 (`COMPLETED`, `PENDING`, `EXPIRED`) |
| `assistantNotified` | `Boolean` | 직원 태블릿 알림 전달 성공 여부 |
| `choiceFitRequested` | `Boolean` | 고객의 사전 피팅 신청 여부 |
| `welcomeCouponMessage` | `String` | 즉시 사용 가능한 웰컴 할인 쿠폰 안내 |
| `purchaseStatus` | `String (Enum)` | 현재 구매 상태 (`PURCHASED`, `PENDING_REENTRY`, `ABANDONED`) |
| `visitedAt` | `LocalDateTime` | 체크인 일시 |

```json
{
  "visitId": 101,
  "memberId": 1,
  "memberName": "김노마드 (VIP)",
  "vipTier": "VIP",
  "checkInType": "BLE",
  "checkInStatus": "COMPLETED",
  "assistantNotified": true,
  "choiceFitRequested": true,
  "welcomeCouponMessage": "✨ [VIP 전용] 인천공항 T1 Herstory 부티크 15% 즉시할인 웰컴 쿠폰이 적용되었습니다.",
  "purchaseStatus": "PENDING_REENTRY",
  "visitedAt": "2026-08-15T18:00:00"
}
```

---

#### `GET /api/v1/store/re-entry-options/{memberId}`
- **설명**: 장바구니에 상품을 담고 결제하지 않은 고객이 매장을 재방문(Re-entry)했을 때 맞춤 분기 팝업과 직원 응대 세일즈 멘트를 제공합니다.

##### Path Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `memberId` | `Long` | Y | 회원 ID |

##### Response Body (`200 OK` - `StoreDto.ReEntryResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `memberId` | `Long` | 회원 ID |
| `memberName` | `String` | 회원 이름 |
| `purchaseStatus` | `String (Enum)` | 구매 상태 |
| `hasPendingCart` | `Boolean` | 미결제 장바구니 존재 여부 |
| `pendingCartItemCount` | `int` | 대기 중인 장바구니 품목 수 |
| `recommendedAction` | `String` | 추천 응대 가이드 및 권장 멘트 |
| `availableOptions` | `List<String>` | 고객 선택 가능 분기 액션 리스트 |

```json
{
  "memberId": 1,
  "memberName": "김노마드 (VIP)",
  "purchaseStatus": "PENDING_REENTRY",
  "hasPendingCart": true,
  "pendingCartItemCount": 1,
  "recommendedAction": "이전 담아두신 '럭셔리 클래식 방수 스타크 백팩' 피팅 룸이 준비되어 있습니다.",
  "availableOptions": [
    "바로 면세 결제 진행",
    "피팅룸에서 다시 착용해보기",
    "방콕 맞춤 신상품 추가 둘러보기"
  ]
}
```

---

#### `GET /api/v1/store/notifications/stream`
- **설명**: SCR-402 매장 직원 태블릿 전용 실시간 Server-Sent Events (SSE) 스트림 엔드포인트입니다. VIP 고객 체크인 시 실시간 이벤트가 브로드캐스팅됩니다.
- **Produces**: `text/event-stream`
- **Response**: `SseEmitter` 스트림

##### SSE 이벤트 데이터 구조 예시
```text
event: STAFF_CHECKIN_ALERT
data: {"type":"VIP_CHECKIN","memberId":1,"memberName":"김노마드 (VIP)","vipTier":"VIP","choiceFit":true,"timestamp":"2026-08-15T18:00:00"}
```

---

### 5.5 Order API (면세 결제 & 마일리지 적립)

#### `POST /api/v1/order/checkout`
- **설명**: 스마트 장바구니 품목을 면세 결제하고, VIP 등급별 할인(5%~15%) 자동 차감 후 결제 금액의 5%를 Herstory Miles로 적립합니다.
- **Content-Type**: `application/json`

##### Request Body (`OrderDto.CheckoutRequest`)
| 필드명 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `memberId` | `Long` | Y | - | 회원 ID |
| `journeyId` | `Long` | N | `1` | 연동 여정 ID |

```json
{
  "memberId": 1,
  "journeyId": 1
}
```

##### Response Body (`200 OK` - `OrderDto.OrderResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `orderId` | `Long` | 주문 고유 번호 |
| `memberId` | `Long` | 회원 ID |
| `journeyId` | `Long` | 연동 여정 ID |
| `totalAmount` | `BigDecimal` | 정상 판매 정가 총액 |
| `dutyFreeDiscount` | `BigDecimal` | VIP 면세 할인 차감 금액 (15%) |
| `finalAmount` | `BigDecimal` | 최종 결제 청구 금액 |
| `earnedMiles` | `Integer` | 이번 주문으로 적립된 Herstory Miles (+5%) |
| `orderStatus` | `String (Enum)` | 주문 상태 (`PAID`, `PENDING`, `CANCELLED`) |
| `items` | `List<OrderItemDetail>` | 구매 완료된 주문 품목 목록 |
| `createdAt` | `LocalDateTime` | 주문 결제 일시 |

```json
{
  "orderId": 501,
  "memberId": 1,
  "journeyId": 1,
  "totalAmount": 1250000.00,
  "dutyFreeDiscount": 187500.00,
  "finalAmount": 1062500.00,
  "earnedMiles": 53125,
  "orderStatus": "PAID",
  "items": [
    {
      "productId": 1,
      "productName": "럭셔리 클래식 방수 스타크 백팩 (Medium)",
      "quantity": 1,
      "price": 1250000.00
    }
  ],
  "createdAt": "2026-08-15T18:15:30"
}
```

---

### 5.6 Care API (현지 부티크 스팟 & AI 가죽 케어)

#### `GET /api/v1/care/visetos-spots`
- **설명**: 회원의 현재 여정 목적지에 위치한 현지 럭셔리 플래그십 스토어 및 Care Desk 정보와 가죽 관리 푸시 가이드를 반환합니다.

##### Query Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `memberId` | `Long` | Y | 회원 ID |

##### Response Body (`200 OK` - `CareDto.CareResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `destination` | `String` | 목적지 도시/공항 명칭 |
| `pushNotificationMessage` | `String` | 현지 도착 푸시 알림 가이드 문구 |
| `visetosSpots` | `List<VisetosSpot>` | 현지 럭셔리 부티크 및 Care Desk 스팟 목록 |

```json
{
  "destination": "Bangkok",
  "pushNotificationMessage": "방콕 수완나품에 도착하셨습니다! 습도 88% 열대 기후에서는 럭셔리 가죽 표면의 습기를 부드러운 천으로 닦아주세요.",
  "visetosSpots": [
    {
      "spotName": "Herstory Siam Paragon Flagship Boutique",
      "address": "991 Rama I Rd, Pathum Wan, Bangkok 10330",
      "locationType": "Herstory Flagship & VIP Lounge",
      "latitude": 13.7466,
      "longitude": 100.5349,
      "careServiceAvailable": "무상 가죽 방수 코팅 & 클리닝 케어 서비스 제공"
    },
    {
      "spotName": "Herstory EmQuartier Boutique & Care Desk",
      "address": "693 Sukhumvit Rd, Khlong Tan Nuea, Bangkok 10110",
      "locationType": "Duty Free Care Desk",
      "latitude": 13.7312,
      "longitude": 100.5698,
      "careServiceAvailable": "현지 시티 스탬프 적립 & 엠보싱 각인 서비스"
    }
  ]
}
```

---

#### `GET /api/v1/care/google-maps`
- **설명**: SCR-502 Google Maps Places API 기반으로 목적지 도시의 현지 럭셔리 부티크 위치, 좌표 및 길안내 링크를 반환합니다. (API 키 미설정 시 지능형 스마트 폴백 작동 및 캐싱 지원)

##### Query Parameters
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `destination` | `String` | N | `"Bangkok"` | 목적지 도시명 (예: `Bangkok`, `Tokyo`, `Paris`) |

##### Response Body (`200 OK` - `List<CareDto.VisetosSpot>`)
```json
[
  {
    "spotName": "Herstory Siam Paragon Flagship",
    "address": "991 Rama I Rd, Pathum Wan, Bangkok 10330",
    "locationType": "Luxury Boutique",
    "latitude": 13.7466,
    "longitude": 100.5349,
    "careServiceAvailable": "VIP Leather Care Desk & Complimentary Refreshment"
  },
  {
    "spotName": "Herstory ICONSIAM Luxury Wing",
    "address": "299 Charoen Nakhon Rd, Khlong San, Bangkok 10600",
    "locationType": "Luxury Boutique",
    "latitude": 13.7267,
    "longitude": 100.5108,
    "careServiceAvailable": "Passport City Stamp & Waterproof Spray Service"
  }
]
```

---

#### `GET /api/v1/care/ai-care-tip`
- **설명**: SCR-501 OpenAI GPT-4o를 호출하여 구매 제품, 현지 기후 및 선택 언어(`ko`, `en`, `ja`, `zh`)에 최적화된 맞춤형 가죽 관리 가이드를 생성합니다.

##### Query Parameters
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `productName` | `String` | N | `"럭셔리 레더 백팩"` | 소지 제품명 |
| `weather` | `String` | N | `"습도 88% 열대성 스콜"` | 현지 날씨/기후 조건 |
| `lang` | `String` | N | `"ko"` | 지원 언어 코드 (`ko`, `en`, `ja`, `zh`) |

##### Response Body (`200 OK` - `String`)
- **한국어 (`lang=ko`) 응답 예시**:
```text
[Herstory Leather Care Guide]
방콕의 고온다습한 기후(습도 88%)에서는 럭셔리 코팅 캔버스의 방수 기능이 탁월하게 작동합니다. 갑작스러운 스콜에 노출되었을 경우 즉시 마른 극세사 천으로 물기를 톡톡 두드리듯 닦아내시고 직사광선을 피해 통풍이 잘되는 그늘에서 건조해 주세요. Siam Paragon 플래그십 매장에서 무상 가죽 보호 코팅 서비스를 받으실 수 있습니다.
```

---

#### `POST /api/v1/care/push-test`
- **설명**: Firebase Cloud Messaging (FCM HTTP v1) 푸시 알림 발송 테스트를 수행합니다.

##### Query Parameters
| 파라미터 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `title` | `String` | Y | 알림 제목 (예: `Herstory VIP 알림`) |
| `body` | `String` | Y | 알림 본문 (예: `수완나품 공항 면세점 방문을 환영합니다`) |

##### Response Body (`200 OK` - `FcmService.PushResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `success` | `boolean` | 푸시 발송 성공 여부 |
| `messageId` | `String` | FCM 고유 메시지 ID |
| `title` | `String` | 알림 제목 |
| `body` | `String` | 알림 본문 |
| `statusMessage` | `String` | 발송 상태 메시지 |

```json
{
  "success": true,
  "messageId": "projects/herstory-nomad-ai/messages/fcm-1771057800000",
  "title": "Herstory VIP 알림",
  "body": "수완나품 공항 면세점 방문을 환영합니다",
  "statusMessage": "FCM HTTP v1 푸시 메시지가 정상 발송되었습니다."
}
```

---

#### `POST /api/v1/care/stamp-checkin`
- **설명**: SCR-502 여행 목적지 도시의 럭셔리 부티크 방문 시 디지털 패스포트 시티 스탬프를 획득하고 보너스 마일리지(+1,000 Miles)를 즉시 적립합니다.
- **Content-Type**: `application/json`

##### Request Body (`CareDto.StampRequest`)
| 필드명 | 타입 | 필수 | 설명 |
| :--- | :--- | :---: | :--- |
| `memberId` | `Long` | Y | 회원 ID |
| `spotName` | `String` | Y | 방문한 매장명 (예: `"Herstory Siam Paragon"`) |

```json
{
  "memberId": 1,
  "spotName": "Herstory Siam Paragon Flagship"
}
```

##### Response Body (`200 OK` - `CareDto.StampResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `memberId` | `Long` | 회원 ID |
| `spotName` | `String` | 스탬프 획득 매장명 |
| `cityName` | `String` | 스탬프 획득 도시명 |
| `earnedMiles` | `int` | 이번 스탬프로 획득한 마일리지 (`1000`) |
| `totalMiles` | `Long` | 적립 후 총 보유 마일리지 잔액 |
| `message` | `String` | 스탬프 획득 축하 메시지 |

```json
{
  "memberId": 1,
  "spotName": "Herstory Siam Paragon Flagship",
  "cityName": "Bangkok",
  "earnedMiles": 1000,
  "totalMiles": 69525,
  "message": "🎉 [Bangkok] 시티 패스포트 스탬프를 획득하셨습니다! +1,000 Herstory Miles가 적립되었습니다."
}
```

---

### 5.7 Flight API (실시간 항공편 운항 정보)

#### `GET /api/v1/flight/lookup`
- **설명**: IATA 편명(예: `OZ741`, `KE651`, `JL92`, `SQ607`, `LH713`)을 입력받아 인천국제공항공사 관제탑(AODB) 1분 단위 실시간 관제 데이터 및 공식 스케줄을 조회합니다. (실시간 게이트 번호, 체크인 카운터, 19분 지연 분 단위 자동 계산, 터미널 정보 포함)

##### Query Parameters
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
| :--- | :--- | :---: | :--- | :--- |
| `flightNumber` | `String` | N | `"OZ741"` | 항공 편명 (예: `OZ741`, `KE651`) |

##### Response Body (`200 OK` - `FlightDto.FlightInfoResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `flightNumber` | `String` | 항공 편명 (예: `"OZ741"`) |
| `airlineName` | `String` | 항공사 명칭 (예: `"아시아나항공"`) |
| `originCode` | `String` | 출발지 공항 IATA 코드 (`"ICN"`) |
| `originName` | `String` | 출발지 공항 이름 (`"ICN (인천국제공항)"`) |
| `originTerminal` | `String` | 출발 여객 터미널 (예: `"인천공항 제1여객터미널"`, `"인천공항 제2여객터미널"`, `"인천공항 탑승동"`) |
| `destinationCode` | `String` | 도착지 공항 IATA 코드 (`"BKK"`) |
| `destinationName` | `String` | 도착지 공항 이름 (`"BKK (방콕/수완나품)"`) |
| `gate` | `String` | 실시간 배정 탑승구 게이트 (예: `"Gate 256"`) |
| `flightStatus` | `String (Enum)` | 운항 상태 (`SCHEDULED`, `BOARDING`, `DELAYED`, `COMPLETED`, `CANCELLED`) |
| `scheduledDepartureTime` | `LocalDateTime` | 정시 출발 예정 일시 |
| `estimatedDepartureTime` | `LocalDateTime` | 실시간 변경 출발 일시 (지연 반영) |
| `scheduledArrivalTime` | `LocalDateTime` | 도착 예정 일시 |
| `scheduledDepartureFormatted` | `String` | 화면 표시용 출발 시각 (예: `"오후 7:35"`) |
| `scheduledArrivalFormatted` | `String` | 화면 표시용 도착 시각 (예: `"오전 1:35"`) |
| `flightDuration` | `String` | 비행 소요 시간 (예: `"6시간 0분"`) |
| `checkinCounter` | `String` | 실시간 체크인 카운터 구역 (예: `"G17-J34"`) |
| `remark` | `String` | 실시간 관제 상태 (예: `"출발"`, `"지연"`, `"탑승중"`, `"마감"`) |
| `delayMinutes` | `int` | 실시간 지연 시간 (분, 예: `19`) |
| `dataSource` | `String` | 운항 데이터 제공 소스 (`"인천국제공항공사 실시간 관제 AODB 공식 데이터"`) |

```json
{
  "flightNumber": "OZ741",
  "airlineName": "아시아나항공",
  "originCode": "ICN",
  "originName": "ICN (인천국제공항)",
  "originTerminal": "인천공항 탑승동",
  "destinationCode": "BKK",
  "destinationName": "BKK (방콕/수완나품)",
  "gate": "Gate 256",
  "flightStatus": "DELAYED",
  "scheduledDepartureTime": "2026-08-15T19:35:00",
  "estimatedDepartureTime": "2026-08-15T19:54:00",
  "scheduledArrivalTime": "2026-08-16T01:35:00",
  "scheduledDepartureFormatted": "오후 7:35",
  "scheduledArrivalFormatted": "오전 1:35",
  "flightDuration": "6시간 0분",
  "checkinCounter": "G17-J34",
  "remark": "출발",
  "delayMinutes": 19,
  "dataSource": "인천국제공항공사 실시간 관제 AODB 공식 데이터"
}
```

---

### 5.8 System Health API (시스템 모니터링)

#### `GET /api/v1/health`
- **설명**: Render PostgreSQL 데이터베이스, OpenAI GPT-4o, Google Maps API, Aviationstack Flight API, Open-Meteo Weather API 연동 상태 및 시스템 업타임을 실시간 점검합니다.

##### Response Body (`200 OK` - `HealthController.HealthStatusResponse`)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `status` | `String` | 전체 시스템 상태 (`HEALTHY` / `DEGRADED`) |
| `serverUptime` | `String` | 서버 구동 상태 (`OK`) |
| `timestamp` | `LocalDateTime` | 체크 일시 |
| `services` | `Map<String, Object>` | 외부 서비스별 세부 연결 상태 |

```json
{
  "status": "HEALTHY",
  "serverUptime": "OK",
  "timestamp": "2026-08-15T17:35:00.999",
  "services": {
    "database": "UP (Render PostgreSQL Connected)",
    "openAiGpt4o": "UP (Real-time OpenAI Key Active)",
    "googleMapsApi": "UP (Real-time Google Maps Key Active)",
    "flightApi": "UP (Real-time Aviationstack Key Active)",
    "weatherApi": "UP (Open-Meteo REST Active)"
  }
}
```

---

## 6. DTO 스키마 사전 (Data Transfer Objects)

### 6.1 `AuthDto`
```java
// 신규 회원가입 요청
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
}

// 신규 회원가입 응답
public class RegisterResponse {
    private Long memberId;
    private String email;
    private String name;
    private VipTier vipTier;
    private Long nomadMiles;
    private String message;
}

// 로그인 요청
public class LoginRequest {
    private String email;
    private String password;
}

// 로그인 응답
public class LoginResponse {
    private Long memberId;
    private String email;
    private String name;
    private VipTier vipTier;
    private Long nomadMiles;
    private String message;
}
```

### 6.2 `JourneyDto`
```java
// OCR 스캔 요청
public class ScanRequest {
    private Long memberId;
    private String pnr;
    private String rawOcrText;
    private String origin;
    private String destination;
}

// OCR 스캔 응답
public class ScanResponse {
    private Long journeyId;
    private String pnr;
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private FlightStatus flightStatus;
    private String message;
}

// 기후 분석 큐레이션 응답
public class JourneyAnalysisResponse {
    private Long journeyId;
    private String destination;
    private String weatherInfo;
    private String climateSummary;
    private String recommendationReason;
    private List<Product> recommendedProducts;
}

// SCR-102 라이브 카드 위젯 응답
public class LiveCardResponse {
    private Long journeyId;
    private String pnr;
    private String destination;
    private LocalDateTime departureDateTime;
    private long remainingMinutesToDeparture;
    private String gate;
    private FlightStatus flightStatus;
    private String loungeLocation;
    private String loungeWaitTime;
    private String liveGuideMessage;
}

// 여정 기본 상세 단건 응답
public class JourneyResponse {
    private Long journeyId;
    private Long memberId;
    private String memberName;
    private String pnr;
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private FlightStatus flightStatus;
    private String destinationWeather;
    private String recommendationReason;
}
```

### 6.3 `CartDto`
```java
// 장바구니 상품 추가
public class AddItemRequest {
    private Long memberId;
    private Long productId;
    private Integer quantity;
}

// 피팅 신청 여부 변경
public class ChoiceFitRequest {
    private Long memberId;
    private Boolean choiceFit;
}

// 장바구니 품목 세부 정보
public class ItemDetail {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
}

// 장바구니 응답
public class CartResponse {
    private Long cartId;
    private Long memberId;
    private Boolean choiceFit;
    private CartStatus status;
    private List<ItemDetail> items;
    private BigDecimal totalPrice;
}
```

### 6.4 `StoreDto`
```java
// 공항 체크인 요청
public class CheckInRequest {
    private Long memberId;
    private CheckInType checkInType;
    private String qrCode;
}

// 체크인 응답
public class CheckInResponse {
    private Long visitId;
    private Long memberId;
    private String memberName;
    private String vipTier;
    private CheckInType checkInType;
    private CheckInStatus checkInStatus;
    private Boolean assistantNotified;
    private Boolean choiceFitRequested;
    private String welcomeCouponMessage;
    private PurchaseStatus purchaseStatus;
    private LocalDateTime visitedAt;
}

// 재방문 분기 응답
public class ReEntryResponse {
    private Long memberId;
    private String memberName;
    private PurchaseStatus purchaseStatus;
    private Boolean hasPendingCart;
    private int pendingCartItemCount;
    private String recommendedAction;
    private List<String> availableOptions;
}
```

### 6.5 `OrderDto`
```java
// 주문 결제 요청
public class CheckoutRequest {
    private Long memberId;
    private Long journeyId;
}

// 주문 품목 세부 정보
public class OrderItemDetail {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}

// 결제 응답
public class OrderResponse {
    private Long orderId;
    private Long memberId;
    private Long journeyId;
    private BigDecimal totalAmount;
    private BigDecimal dutyFreeDiscount;
    private BigDecimal finalAmount;
    private Integer earnedMiles;
    private OrderStatus orderStatus;
    private List<OrderItemDetail> items;
    private LocalDateTime createdAt;
}
```

### 6.6 `CareDto`
```java
// 현지 럭셔리 부티크 스팟 정보
public class VisetosSpot {
    private String spotName;
    private String address;
    private String locationType;
    private Double latitude;
    private Double longitude;
    private String careServiceAvailable;
}

// 현지 케어 응답
public class CareResponse {
    private String destination;
    private String pushNotificationMessage;
    private List<VisetosSpot> visetosSpots;
}

// 시티 스탬프 체크인 요청
public class StampRequest {
    private Long memberId;
    private String spotName;
}

// 시티 스탬프 응답
public class StampResponse {
    private Long memberId;
    private String spotName;
    private String cityName;
    private int earnedMiles;
    private Long totalMiles;
    private String message;
}
```

### 6.7 `FlightDto`
```java
// 항공편 운항 정보 응답
public class FlightInfoResponse {
    private String flightNumber;
    private String airlineName;
    private String originCode;
    private String originName;
    private String originTerminal;
    private String destinationCode;
    private String destinationName;
    private String gate;
    private FlightStatus flightStatus;
    private LocalDateTime scheduledDepartureTime;
    private LocalDateTime estimatedDepartureTime;
    private int delayMinutes;
    private String dataSource;
}
```

### 6.8 `Product` (도메인 엔티티)
```java
public class Product {
    private Long id;
    private String name;
    private ProductCategory category;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String description;
    private Boolean isVipExclusive;
}
```

### 6.9 `AppleWalletPassResponse` & `PushResponse`
```java
// Apple Wallet 메타데이터 응답
public class AppleWalletPassResponse {
    private String passTypeIdentifier;
    private String serialNumber;
    private String teamIdentifier;
    private String organizationName;
    private String description;
    private String logoText;
    private Map<String, Object> boardingPassDetails;
    private String pkpassDownloadUrl;
}

// FCM 푸시 응답
public class PushResponse {
    private boolean success;
    private String messageId;
    private String title;
    private String body;
    private String statusMessage;
}
```
