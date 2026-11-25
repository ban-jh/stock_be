# 변경 이력

| 날짜 | 변경 | 영향 범위 | 검증 |
|---|---|---|---|
| 2026-11-25 | stock-be 초기 골격과 MariaDB/MyBatis 기반 설정 문서화 | Spring Boot 시작점, 상태 API, 공통 응답, 도메인 패키지 경계 | 빌드/테스트 미실행 |
| 2026-11-25 | 개발 절차서, 백엔드 설계서, 문서 갱신 규칙 작성 | docs/README.md, 개발 절차, 아키텍처 및 변경 이력 | 문서 파일 확인 |
| 2026-11-25 | 개발 로드맵 1단계: MariaDB 연결정보 환경변수화 및 핵심 스키마 초안 추가 | application.yml, dbscript/01_schema_core.sql, README.md | DB 미적용, 빌드/테스트 미실행 |

| 2026-11-25 | 1단계 DB 상세 설계: 6개 테이블 명세, Mermaid ERD, 관계·제약·인덱스 및 적용 절차 문서화 | docs/설계/01-database-schema.md 및 문서 인덱스/로드맵 | DDL은 미적용 상태로 명시 |

| 2026-11-25 | 2단계 도메인 접근 구현 초안: 전략·종목 CRUD 일부, 실행 이력 조회, AI 사용량 집계 API/Mapper | investment, ai, common/exception, mapper XML 및 설계/02-domain-api.md | DB 미적용, 빌드/테스트 미실행 |

| 2026-11-25 | 문서 폴더명을 화면 디자인과 혼동되지 않도록 한글 설계로 변경하고 링크 갱신 | docs/설계 및 문서 참조 | 문서 경로 확인 |
| 2026-11-25 | 3단계 토스증권 API 연결 사전 준비 절차와 착수 기준 문서화 | docs/설계/03-toss-api-prerequisites.md, README, 로드맵 | 공식 신청/인증/시세/약관 문서 링크 확인; API 연동 코드는 미착수 |
| 2026-11-25 | 토스 OAuth·시세·계좌 읽기 어댑터와 명시 실행형 연결 점검기 구현, 문서에서 인증 비밀값 제거 및 환경변수화; Spring Boot 4 JSON 의존성 정리 | market/toss, application.yml, pom.xml, JSON 사용 코드 및 설계 문서 | `mvn -q -DskipTests compile` 성공; 테스트와 실제 토스 호출은 미실행 |
| 2026-11-25 | Spring 자동 구성에 없는 `RestClient.Builder` 주입을 제거하고 프레임워크 기본 빌더로 직접 생성 | `TossApiConfiguration`, 설계/03-toss-api.md | `mvn -q -DskipTests compile` 성공; 토스 API 및 smoke check를 끈 상태로 Spring Boot 서버 기동 성공; 테스트 미실행 |
| 2026-11-25 | 읽기 전용 점검의 성공 조건을 국내·미국 현재가 2건과 계좌 1건 이상으로 강화하고 시크릿키 입력 절차 문서화 | `TossApiSmokeCheckRunner`, 설계/03-toss-api-prerequisites.md, 설계/03-toss-api.md | `mvn -q -DskipTests compile` 성공; 실제 API 호출은 사용자 로컬 설정 후 수행 |
| 2026-11-25 | 로컬 프로파일에 `.env.properties` 선택적 import 적용, Git 제외 파일과 안전한 예제 및 사용법 추가 | `application-local.yml`, `.env.example`, `.env.properties`, 설계 문서 | `mvn -q -DskipTests compile` 성공; 키와 smoke check를 끈 기본 서버 기동 성공; 실제 토스 호출은 미실행 |
| 2026-11-25 | `.env.properties` 인증값을 Windows DPAPI로 암호화하고 복호화·실행 스크립트를 추가 | `protect-local-secrets.ps1`, `run-local.ps1`, Toss 설정 검증, 설계 문서 | 기존 키를 출력 없이 DPAPI 암호화; 컴파일·기본 기동 확인 후 DPAPI 복호화로 읽기 전용 토스 점검 성공 (`priceRows=2`, `accountRows=1`); 주문 요청 없음 |
| 2026-11-25 | 토스 읽기 전용 API에 개인 Bearer 인증, 프로세스 요청 간격 및 429/Retry-After 전달 추가 | `market/toss` 컨트롤러·인증 인터셉터·요청 페이서, 공통 오류 응답, DPAPI 스크립트, 토스 설계/로드맵 | `mvn -q -DskipTests compile` 성공, PowerShell 스크립트 구문 확인 성공; 개인 토큰 미설정으로 브라우저 호출은 미확인 |
| 2026-11-25 | 토스 현재가·계좌 목록 응답을 내부 DTO로 변환하고 계좌번호 원문 마스킹 | `TossPriceQuote`, `TossAccountSummary`, 시세/계좌 서비스, 컨트롤러, smoke checker 및 설계 문서 | 컴파일 성공; 실제 조회로 시세 2건·계좌 1건·보유 조회 성공, 계좌번호 마스킹 확인; 계좌에 보유 항목이 없어 항목 DTO는 미구현 |
| 2026-11-25 | 로컬 DPAPI 토큰을 이용한 API 인증·읽기 경로 점검 후 3단계 상태 확정 | 토스 연동 설계/로드맵/아키텍처 문서 | 토큰 없는 요청 401, 인증 조회 `success=true`; 시세 실제 값·계좌번호·잔액을 출력하지 않음; 주문 호출 없음 |
