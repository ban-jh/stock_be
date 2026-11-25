-- Stock AIM 핵심 스키마 초안. 빈 stock DB에 최초 적용하는 생성 스크립트이며 DROP하지 않습니다.
SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE IF NOT EXISTS aim_strategy (
 strategy_id BIGINT NOT NULL AUTO_INCREMENT,
 strategy_code VARCHAR(64) NOT NULL,
 version_no INT NOT NULL,
 strategy_name VARCHAR(120) NOT NULL,
 enabled BOOLEAN NOT NULL DEFAULT FALSE,
 execution_mode VARCHAR(16) NOT NULL DEFAULT 'PAPER',
 config_json LONGTEXT NOT NULL,
 created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
 updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
 PRIMARY KEY(strategy_id),
 UNIQUE KEY uk_strategy_version(strategy_code,version_no),
 KEY ix_strategy_enabled(enabled,execution_mode),
 CONSTRAINT ck_strategy_mode CHECK(execution_mode IN ('PAPER','LIVE')),
 CONSTRAINT ck_strategy_json CHECK(JSON_VALID(config_json))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='전략 버전과 설정';

CREATE TABLE IF NOT EXISTS aim_instrument (
 instrument_id BIGINT NOT NULL AUTO_INCREMENT,
 market_code VARCHAR(8) NOT NULL,
 asset_type VARCHAR(12) NOT NULL,
 symbol VARCHAR(32) NOT NULL,
 display_name VARCHAR(160) NOT NULL,
 watch_state VARCHAR(12) NOT NULL DEFAULT 'WATCH',
 enabled BOOLEAN NOT NULL DEFAULT TRUE,
 memo VARCHAR(500) NULL,
 created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
 updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
 PRIMARY KEY(instrument_id),
 UNIQUE KEY uk_instrument_market_symbol(market_code,symbol),
 KEY ix_instrument_state(watch_state,enabled),
 CONSTRAINT ck_instrument_market CHECK(market_code IN ('KR','US')),
 CONSTRAINT ck_instrument_type CHECK(asset_type IN ('STOCK','ETF')),
 CONSTRAINT ck_instrument_state CHECK(watch_state IN ('WATCH','ALLOW','EXCLUDE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관심 및 허용 종목';

CREATE TABLE IF NOT EXISTS aim_execution (
 execution_id CHAR(36) NOT NULL,
 idempotency_key VARCHAR(100) NOT NULL,
 trigger_type VARCHAR(12) NOT NULL,
 execution_mode VARCHAR(16) NOT NULL DEFAULT 'PAPER',
 status VARCHAR(16) NOT NULL,
 strategy_code VARCHAR(64) NULL,
 strategy_version INT NULL,
 started_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
 finished_at TIMESTAMP(3) NULL,
 stop_reason VARCHAR(500) NULL,
 summary_json LONGTEXT NULL,
 PRIMARY KEY(execution_id),
 UNIQUE KEY uk_execution_idempotency(idempotency_key),
 KEY ix_execution_started(started_at),
 KEY ix_execution_status(status,started_at),
 CONSTRAINT ck_execution_trigger CHECK(trigger_type IN ('SCHEDULED','MANUAL')),
 CONSTRAINT ck_execution_mode CHECK(execution_mode IN ('PAPER','LIVE')),
 CONSTRAINT ck_execution_status CHECK(status IN ('QUEUED','RUNNING','COMPLETED','STOPPED','FAILED')),
 CONSTRAINT ck_execution_json CHECK(summary_json IS NULL OR JSON_VALID(summary_json))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='예약 및 수동 실행';

CREATE TABLE IF NOT EXISTS aim_order (
 order_id BIGINT NOT NULL AUTO_INCREMENT,
 execution_id CHAR(36) NOT NULL,
 instrument_id BIGINT NOT NULL,
 side VARCHAR(8) NOT NULL,
 order_type VARCHAR(12) NOT NULL,
 quantity DECIMAL(24,8) NOT NULL,
 limit_price DECIMAL(24,8) NULL,
 currency_code CHAR(3) NOT NULL,
 order_status VARCHAR(16) NOT NULL,
 broker_order_id VARCHAR(120) NULL,
 reason VARCHAR(1000) NULL,
 created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
 updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
 PRIMARY KEY(order_id),
 KEY ix_order_execution(execution_id),
 KEY ix_order_instrument_created(instrument_id,created_at),
 CONSTRAINT fk_order_execution FOREIGN KEY(execution_id) REFERENCES aim_execution(execution_id),
 CONSTRAINT fk_order_instrument FOREIGN KEY(instrument_id) REFERENCES aim_instrument(instrument_id),
 CONSTRAINT ck_order_side CHECK(side IN ('BUY','SELL')),
 CONSTRAINT ck_order_type CHECK(order_type IN ('MARKET','LIMIT')),
 CONSTRAINT ck_order_quantity CHECK(quantity > 0),
 CONSTRAINT ck_order_price CHECK(limit_price IS NULL OR limit_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PAPER 및 향후 주문';

CREATE TABLE IF NOT EXISTS aim_ai_usage_daily (
 usage_date DATE NOT NULL,
 provider_code VARCHAR(40) NOT NULL,
 call_count INT UNSIGNED NOT NULL DEFAULT 0,
 input_tokens BIGINT UNSIGNED NOT NULL DEFAULT 0,
 output_tokens BIGINT UNSIGNED NOT NULL DEFAULT 0,
 estimated_cost DECIMAL(18,6) NOT NULL DEFAULT 0,
 updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
 PRIMARY KEY(usage_date,provider_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 사용량 및 비용';

CREATE TABLE IF NOT EXISTS aim_system_setting (
 setting_key VARCHAR(100) NOT NULL,
 setting_value VARCHAR(1000) NOT NULL,
 description VARCHAR(500) NULL,
 updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
 PRIMARY KEY(setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='개인 시스템 설정';

INSERT INTO aim_system_setting(setting_key,setting_value,description) VALUES
 ('execution.mode','PAPER','초기 모드는 가상매매'),
 ('execution.enabled','false','사용자 명시 활성화 전에는 실행 차단'),
 ('ai.monthly_call_limit','0','0은 AI 비활성')
ON DUPLICATE KEY UPDATE setting_key=VALUES(setting_key);