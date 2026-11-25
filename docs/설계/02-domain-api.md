# 2단계 산출물: 전략·종목·실행·AI 사용량 API 설계

- 상태: 구현 초안
- DB 기준: dbscript/01_schema_core.sql
- 공통 응답: success, data, error
- 모든 경로는 기본적으로 /api prefix를 사용합니다.
- DB 스키마 적용과 HTTP/API 동작은 아직 실행 검증하지 않았습니다.

## 1. 구현 경계

2단계에서 전략 버전과 투자 종목은 조회·등록·활성 상태 변경 API까지 연결합니다. 실행은 과거 이력 조회만 제공합니다. 예약/수동 실행 생성과 주문 실행은 6단계의 실행 엔진에 둡니다. AI 사용량은 일자·제공자별 누적 및 기간 조회를 제공합니다. AI 호출 전 한도 차단은 4단계에서 연결합니다.

## 2. 계층 구조

- controller: HTTP 경로, 요청 바인딩, 공통 응답
- dto: 요청 입력값과 유효성 제약
- service: 버전 전환, 코드 검증, 조회 범위 제한, 트랜잭션
- domain: DB 결과 및 저장 모델
- mapper: SQL 인터페이스
- resources/mapper: MyBatis XML과 테이블 SQL

서비스는 거래 판단이나 토스 API 호출을 수행하지 않습니다. DB 입력은 SQL 파라미터 바인딩으로 처리합니다.

## 3. API 명세

### 전략 버전

| Method | 경로 | 기능 |
|---|---|---|
| GET | /api/strategies | 전체 전략 버전 목록 |
| GET | /api/strategies/{code}/versions | 전략 코드별 버전 목록 |
| POST | /api/strategies/versions | 새 PAPER 전략 버전 등록 |
| PUT | /api/strategies/{code}/versions/{version}/activate | 같은 전략 코드 내 PAPER 버전 활성화 |

등록 요청 예:
{
  "strategyCode": "CORE",
  "versionNo": 1,
  "strategyName": "기본 전략",
  "config": {
    "risk": {"maxPositionAmount": 100000, "minimumCashRatio": 0.2},
    "ai": {"enabled": false}
  }
}

중복된 코드/버전은 400으로 거부합니다. 신규 버전은 항상 enabled=false, executionMode=PAPER로 저장합니다. 활성화는 기존 같은 코드의 활성 버전을 비활성화한 뒤 지정 버전만 켭니다. LIVE 전략은 활성화하지 않습니다. 설정 JSON의 구조별 의미 검증은 다음 위험/전략 구현 단계에서 보강합니다.

### 종목 목록

| Method | 경로 | 기능 |
|---|---|---|
| GET | /api/instruments?marketCode=KR&watchState=ALLOW | 시장/상태 필터 목록 |
| GET | /api/instruments/{id} | 종목 상세 |
| POST | /api/instruments | 국내·미국 주식/ETF 등록 |
| PATCH | /api/instruments/{id}/state | WATCH/ALLOW/EXCLUDE 및 enabled 변경 |

등록 요청 예:
{
  "marketCode": "US",
  "assetType": "ETF",
  "symbol": "SPY",
  "displayName": "SPDR S&P 500 ETF",
  "watchState": "WATCH",
  "memo": "검토 후보"
}

심볼은 저장 전에 공백을 제거하고 대문자로 정규화합니다. (marketCode, symbol) 중복은 DB 유일 제약으로 막습니다. 종목 삭제 API는 제공하지 않습니다. 주문 이력이 생긴 종목은 EXCLUDE 또는 enabled=false로 보존합니다.

### 실행 이력

| Method | 경로 | 기능 |
|---|---|---|
| GET | /api/executions?limit=50 | 최근 실행 이력. limit은 1~200으로 제한 |
| GET | /api/executions/{id} | 실행 상세 |

현재 실행 이력을 등록·갱신하는 API는 없습니다. 실행 레코드 생성과 상태 전이는 예약/수동 실행 서비스를 구현할 6단계에서 연결합니다.

### AI 사용량

| Method | 경로 | 기능 |
|---|---|---|
| GET | /api/ai-usage?from=YYYY-MM-DD&to=YYYY-MM-DD | 날짜 범위별 제공자 집계 |
| POST | /api/ai-usage | 하루·제공자 사용량을 원자적으로 누적 |

등록 요청 예:
{
  "usageDate": "2026-11-25",
  "providerCode": "OPENAI",
  "callCount": 1,
  "inputTokens": 1200,
  "outputTokens": 300,
  "estimatedCost": 0.0042
}

누적은 (usage_date, provider_code) 복합 키 기준 upsert로 처리합니다. 기간은 최대 1년으로 제한합니다. 실제 공급자 호출과 한도 차단은 이 API만으로 자동 실행되지 않으며 4단계 및 AI 연동 단계에서 서비스 내부로 연결해야 합니다.

## 4. 오류 응답

- 입력 검증 실패: HTTP 400, INVALID_REQUEST
- 잘못된 값/중복 버전: HTTP 400 또는 DB 중복 HTTP 409
- 조회 대상 없음: HTTP 404, NOT_FOUND
- 예상하지 못한 서버 오류: HTTP 500, INTERNAL_ERROR

응답 메시지에는 SQL이나 내부 예외를 노출하지 않습니다.

## 5. 후속 보완사항

- 로그인·인증 및 로컬/사설 네트워크 보호
- 목록 페이지네이션, 정렬 기준, 검색 필터 확장
- 전략 JSON의 버전별 스키마 및 위험 범위 검증
- 전략 활성 버전의 동시 변경 잠금/DB 단일 활성 보장
- 주문 상태·체결 내역·부분 체결 도메인 추가
- 실행 레코드 생성/상태 전이 API는 예약·수동 실행 단계에서 구현
- AI 한도 확인을 공급자 호출 앞에 강제하고 누적을 자동 연결