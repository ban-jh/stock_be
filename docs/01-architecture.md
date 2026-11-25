# 백엔드 설계

## 1. 목적과 경계

애플리케이션은 본인 계좌 관리용 단일 Spring Boot 서비스입니다. 외부 데이터 제공자는 토스증권 Open API이며, 저장소는 MariaDB입니다. 프론트엔드는 별도 Vue 앱으로 실행하고 JSON REST API를 호출합니다.

현재는 초기 골격만 구현되어 있습니다. 상태 확인과 연결 상태 요약 외에는 투자 도메인 기능이 준비되지 않았습니다. 문서에서 계획 기능과 구현 완료 기능을 혼동하지 않습니다.

## 2. 기술 구성

- Java 17 / Spring Boot 4.0.0
- Spring MVC, Validation, JDBC
- Spring Boot Jackson JSON starter (Jackson 3 데이터 바인딩)
- MyBatis 4.0.1 및 XML Mapper
- MariaDB JDBC Driver
- 애플리케이션 포트 38081
- 기본 실행 모드 PAPER, 주문 실행 비활성

HolyLife의 단일 애플리케이션 및 계층형 패키지 배치 방식을 참고합니다. HolyLife가 사용하는 MariaDB, MyBatis XML, application 프로파일 분리도 적용합니다.

## 3. 현재 패키지 지도

기본 패키지는 org.shaloman.pj입니다.

- StockApplication: 애플리케이션 시작 및 스케줄 기능 기반
- common.api: ApiResponse 공통 응답
- system.controller: 서버 프로세스 상태 확인
- dashboard.controller: 미연결 상태 요약
- investment.strategy: 전략 조건과 버전의 향후 위치
- investment.instrument: 종목 허용 목록의 향후 위치
- investment.portfolio: 계좌·보유·손익 모델의 향후 위치
- investment.execution: 예약/수동 실행과 PAPER 처리의 향후 위치
- market.toss: 토스 API 인증·시세·계좌 조회 어댑터
- ai: AI 분석과 사용 한도의 향후 위치
- config: 향후 Spring 구성 클래스
- resources/mapper: 향후 MyBatis XML

향후 도메인 구현은 controller/service/dto/mapper/domain 하위 계층으로 구성합니다. 빈 패키지 설명 파일은 모듈 경계만 선언하며 실제 기능 구현을 의미하지 않습니다.

## 4. 현재 API

### GET /api/system/health

서버 응답 여부, PAPER 모드, 브로커·AI 미연결 여부, 확인 시각을 반환합니다. status=UP은 웹 프로세스가 응답한다는 뜻이며 DB 또는 외부 서비스의 상태를 보증하지 않습니다.

### GET /api/dashboard/summary

현재 실행 모드와 브로커/시세/AI/포트폴리오 연결 상태를 반환합니다. 토스 연동 전 상태는 NOT_CONNECTED, DISABLED, NOT_AVAILABLE입니다. 자산 잔고와 수익률은 반환하지 않습니다.

응답은 success, data, error 필드를 갖는 ApiResponse로 감쌉니다.

## 5. 목표 모듈 및 책임

### market.toss

OAuth 토큰과 허용 IP 등 인증·연결 설정을 서버에서 관리합니다. 가격, 호가, 체결, 캔들, 계좌 잔고 응답을 내부 심볼·시장·통화 모델로 변환합니다. 요청 한도, 429, 지연/오래된 데이터, 외부 장애를 명시적으로 처리합니다. 토큰과 계좌정보를 로그에 기록하지 않습니다.

### investment.instrument

KR/US 시장, 주식/ETF 유형, 토스 심볼, 표시명, 관심·허용·제외 상태를 저장합니다. 허용 종목만 전략 평가 대상으로 전달합니다.

### investment.strategy

설정 변경은 기존 버전을 덮어쓰지 않고 새 버전으로 저장합니다. 각 실행과 보유 포지션에는 판단 당시 전략 버전을 연결합니다. 전략 파라미터는 DTO 입력 검증과 서버 측 범위 검증을 거칩니다.

### investment.portfolio

토스 잔고와 내부 실행 기록을 구분합니다. 현금, 보유 수량, 평균 매입가, 현재가, 통화, 환율 기준 시각을 추적합니다. 수익률 계산은 수수료·세금과 환율 반영 여부를 명시합니다.

### investment.execution

자동 예약과 수동 요청을 같은 유스케이스로 연결하되 실행 원인을 기록합니다. PAPER 주문/체결과 나중에 추가될 실계좌 주문 어댑터를 분리합니다. 멱등 키와 실행 잠금으로 스케줄 중복이나 버튼 중복 제출에 의한 중복 주문을 막습니다.

### ai

호출 전에 잔여 월간 한도와 비용 한도를 검증합니다. 초과 시 AI 분석 및 신규 투자 실행을 중단하고 사유를 남깁니다. AI의 제안은 사용자 승인과 백테스트/위험 제한을 거치기 전까지 활성 전략을 변경하지 않습니다.

## 6. 목표 실행 순서

예약 실행 또는 수동 실행 → 전체 중지 검사 → AI 한도 검사 → 시장 시간 및 시세 신선도 검사 → 활성 전략 버전과 허용 종목 조회 → 정량 판단과 AI 보조 분석 → 투자 한도 및 중복 주문 검사 → PAPER 주문 기록 → 실행 결과와 근거 저장.

AI 한도 초과, API 오류, 시세 오래됨, 시장 휴장, 위험 한도 위반은 신규 매매를 중단시키는 조건입니다. 실패 사유는 화면과 실행 이력에서 확인할 수 있게 저장합니다.

## 7. 데이터 및 설정 원칙

MariaDB 연결은 application.yml의 공통값과 local/dev/prod 프로파일을 사용합니다. 현재 기본 로컬값은 localhost:3306/stock, 사용자 stock입니다. 실제 비밀번호는 운영 환경에서 외부 설정으로 전달해야 합니다.

Mapper XML은 classpath:mapper/**/*.xml에서 탐색하며 underscore 컬럼명을 camelCase 필드로 자동 매핑합니다. SQL은 바인딩 파라미터를 사용하고 문자열 연결로 사용자 입력을 쿼리에 삽입하지 않습니다.

토스 API 키, AI 키, 계좌 식별 민감정보는 Git 추적 파일·프론트엔드 번들·일반 로그에 두지 않습니다. 개인 전용 시스템이어도 관리 화면 접근과 서버 포트 노출을 최소화합니다.

## 8. 운영상 미구현 항목

- MariaDB 스키마와 초기 데이터 마이그레이션
- 토스 API 조회 REST 경로의 프론트 연결, 계좌정보 노출 제어 및 운영 환경 시크릿 주입
- AI 공급자 호출과 사용량 집계
- 예약/수동 실행 서비스와 실행 이력
- PAPER 주문 체결 시뮬레이션
- 손익/백테스트 계산
- 알림 및 운영 장애 처리
- 실계좌 주문은 위 항목 검증 이후 별도 설계
## 데이터베이스 상세 설계
핵심 테이블 컬럼·제약·인덱스와 ERD는 docs/설계/01-database-schema.md를 기준으로 합니다. DDL은 초안이며 DB 미적용 상태입니다.

## 2단계 도메인 API 구현
전략 버전, 투자 종목, 실행 이력, AI 사용량의 API와 MyBatis 접근 초안이 구현되었습니다. 경로·요청/응답·오류 정책·구현 경계는 docs/설계/02-domain-api.md를 기준으로 합니다. 토스 연동, 실행 생성, AI 한도 강제는 아직 구현되지 않았습니다.

## 3단계 토스 읽기 전용 조회 API

- `GET /api/toss/prices?symbols=005930,AAPL`
- `GET /api/toss/accounts`
- `GET /api/toss/accounts/{accountSeq}/holdings`

모든 경로는 `Authorization: Bearer <STOCK_API_ACCESS_TOKEN>` 개인 인증을 요구합니다. 토큰이 비어 있으면 503, 불일치하면 401이며, 계좌 관련 응답은 인증 통과 후에만 반환됩니다. 토스 호출은 프로세스 내 기본 500ms 간격을 지키며, 토스가 429를 반환하면 재호출하지 않고 429와 `Retry-After`를 전달합니다. API 응답은 공통 `ApiResponse`로 감쌉니다.

현재가 응답은 `TossPriceQuote(symbol, timestamp, lastPrice, currency)`로, 계좌 목록은 `TossAccountSummary(accountSeq, accountType, maskedAccountNo)`로 변환합니다. 계좌번호 원문은 반환하지 않습니다. 보유 종목은 연결 계좌에 현재 항목이 없어 공급자 응답 구조를 보존하고, 실제 보유 행이 생기면 필드 계약을 확인해 타입을 추가합니다. 시장 전체 시세는 DB에 저장하지 않고 PAPER 실행에서 사용한 선택 시세만 실행 근거로 남깁니다.
