# Stock Backend

본인 토스증권 계좌 전용 AIM 서버입니다. HolyLife의 Spring Boot 단일 애플리케이션, Java 계층형 패키지, MyBatis XML, 환경 프로파일 분리 구조를 참고합니다.

기술 구성은 Java 17, Spring Boot 4, MariaDB, MyBatis입니다. 패키지 책임은 common(공통 응답), config(설정), system(상태), dashboard(요약), investment(전략·종목·포트폴리오·실행), market/toss(토스 API), ai(AI 한도)로 나눕니다.

MariaDB에 stock 데이터베이스를 준비하고 DB_URL, DB_USERNAME, DB_PASSWORD 환경변수를 설정한 뒤 Maven으로 실행합니다. 핵심 스키마 초안은 dbscript/01_schema_core.sql에 있습니다. 기본 포트는 38081이며 /api/system/health에서 프로세스 상태를 확인합니다.

현재 토스 계좌·시세·AI·주문은 아직 연결하지 않았습니다. 기본 모드는 PAPER이고 실계좌 주문 코드는 없습니다. 비밀 키는 소스에 저장하지 않습니다.