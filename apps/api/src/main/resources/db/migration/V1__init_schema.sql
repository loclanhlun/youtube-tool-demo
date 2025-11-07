-- Flyway V1: initial schema for youtube_tool
-- Requires: PostgreSQL + pgcrypto (for gen_random_uuid)
-- Notes:
--  - standardized createdAt/updatedAt -> created_at/updated_at
--  - avoids hardcoded IDs on seed by selecting via code

BEGIN;

-- Extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- 1) Identity / RBAC
-- =========================

CREATE TABLE IF NOT EXISTS user_status
(
    id         SERIAL PRIMARY KEY,
    code       VARCHAR(50) UNIQUE NOT NULL,
    name       VARCHAR(100)       NOT NULL,
    created_at TIMESTAMPTZ        NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS roles
(
    id         SERIAL PRIMARY KEY,
    code       TEXT        NOT NULL UNIQUE,
    name       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS users
(
    id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    email         VARCHAR(155) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    status_id     INT                 NOT NULL,
    created_at    TIMESTAMPTZ         NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    CONSTRAINT fk_user_user_status FOREIGN KEY (status_id) REFERENCES user_status (id)
);

CREATE TABLE IF NOT EXISTS user_profiles
(
    id         SERIAL PRIMARY KEY,
    firstName  TEXT        NOT NULL,
    middleName TEXT,
    lastName   TEXT        NOT NULL,
    user_id    UUID        NOT NULL,
    address    TEXT,
    age        INT,
    logo       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_roles
(
    id      SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    role_id INT  NOT NULL,
    CONSTRAINT fk_user_roles_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT uq_user_roles UNIQUE (user_id, role_id)
);

-- =========================
-- 2) Feature & Pricing
-- =========================

CREATE TABLE IF NOT EXISTS feature_units
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS features
(
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(50) UNIQUE NOT NULL, -- TRANSCRIBE, REWRITE, IMAGE_GEN
    name            VARCHAR(155)       NOT NULL,
    feature_unit_id INT,
    CONSTRAINT fk_features_feature_units
        FOREIGN KEY (feature_unit_id) REFERENCES feature_units (id)
);

CREATE TABLE IF NOT EXISTS pricing_rules
(
    id                   SERIAL PRIMARY KEY,
    feature_id           INT            NOT NULL,
    unit_cost_in_credits NUMERIC(12, 4) NOT NULL,
    unit                 TEXT           NOT NULL,
    min_step             INT            NOT NULL DEFAULT 1,
    notes                TEXT,
    effective_from       TIMESTAMPTZ    NOT NULL, -- thời điểm áp dụng
    effective_to         TIMESTAMPTZ,             -- NULL = còn hiệu lực
    CONSTRAINT fk_pricing_rules_features
        FOREIGN KEY (feature_id) REFERENCES features (id)
);
CREATE INDEX IF NOT EXISTS idx_pricing_action_time
    ON pricing_rules (feature_id, effective_from DESC);

-- =========================
-- 3) Credits (wallet & ledger)
-- =========================

CREATE TABLE IF NOT EXISTS credit_wallets
(
    user_id         UUID PRIMARY KEY,
    balance_credits BIGINT      NOT NULL DEFAULT 0,
    free_credits    BIGINT      NOT NULL DEFAULT 0,
    effective_from  TIMESTAMPTZ NOT NULL DEFAULT now(),
    period          VARCHAR(50) NOT NULL DEFAULT 'MONTH',
    reset_day       INT         NOT NULL DEFAULT 30,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_credit_wallets_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS credit_ledger
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        UUID        NOT NULL,
    change_credits BIGINT      NOT NULL, -- + for top-up, - for spend
    reason         TEXT        NOT NULL,
    meta_json      JSONB,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_credit_ledger_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- =========================
-- 4) Catalog & Orders
-- =========================

CREATE TABLE IF NOT EXISTS order_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS product_sku_types
(
    id   SERIAL PRIMARY KEY,
    node VARCHAR(50)  NOT NULL,
    name VARCHAR(155) NOT NULL
);

CREATE TABLE IF NOT EXISTS product_skus
(
    id              UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    code            VARCHAR(150) UNIQUE NOT NULL,           -- ví dụ: CREDITS_1K, CREDITS_5K
    name            VARCHAR(155)        NOT NULL,           -- "Credit Pack 1,000"
    type_id         INT                 NOT NULL,
    credits_granted BIGINT              NOT NULL DEFAULT 0, -- gói credit tặng khi mua (nếu là credit_pack)
    is_active       BOOLEAN             NOT NULL DEFAULT TRUE,
    meta_json       JSONB,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT fk_product_skus_product_sku_types FOREIGN KEY (type_id) REFERENCES product_sku_types (id)
);

CREATE TABLE IF NOT EXISTS sku_prices
(
    id             BIGSERIAL PRIMARY KEY,
    sku_id         UUID           NOT NULL,
    currency       VARCHAR(20)    NOT NULL,            -- USD, VND
    unit_price     NUMERIC(12, 2) NOT NULL,            -- giá hiện hành
    effective_from TIMESTAMPTZ    NOT NULL,            -- thời điểm áp dụng
    effective_to   TIMESTAMPTZ,                        -- NULL = còn hiệu lực
    region         VARCHAR(10)    NOT NULL DEFAULT '', -- '', 'VN', 'US', 'EU'...
    CONSTRAINT fk_sku_prices_product_skus FOREIGN KEY (sku_id) REFERENCES product_skus (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_sku_prices_key
    ON sku_prices (sku_id, currency, region, effective_from);

CREATE TABLE IF NOT EXISTS orders
(
    id           UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    user_id      UUID           NOT NULL,
    status_id    INT            NOT NULL, -- pending, paid, failed, cancelled
    currency     VARCHAR(50)    NOT NULL DEFAULT 'USD',
    total_amount NUMERIC(12, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_orders_order_status FOREIGN KEY (status_id) REFERENCES order_status (id)
);

CREATE TABLE IF NOT EXISTS order_types
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
    id                       BIGSERIAL PRIMARY KEY,
    order_id                 UUID           NOT NULL,
    order_type_id            INT            NOT NULL, -- credit_pack
    sku_id                   UUID           NOT NULL,
    snapshot_name            VARCHAR(255),
    snapshot_unit_price      NUMERIC(12, 2) NOT NULL,
    snapshot_credits_granted BIGINT,
    qty                      INT            NOT NULL,
    CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_items_order_types FOREIGN KEY (order_type_id) REFERENCES order_types (id),
    CONSTRAINT fk_order_items_product_skus FOREIGN KEY (sku_id) REFERENCES product_skus (id)
);

-- =========================
-- 5) Payments
-- =========================

CREATE TABLE IF NOT EXISTS payment_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50)  NOT NULL,
    name VARCHAR(155) NOT NULL
);

CREATE TABLE IF NOT EXISTS payment_intents
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    order_id      UUID        NOT NULL,
    provider_id   INT,
    provider      VARCHAR(50) NOT NULL, -- stripe, vnpay, etc.
    provider_ref  VARCHAR(255),
    status_id     INT         NOT NULL, -- requires_action, succeeded, failed
    client_secret TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    CONSTRAINT fk_payment_intents_orders FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_payment_intents_payment_status FOREIGN KEY (status_id) REFERENCES payment_status (id)
);

-- =========================
-- 6) Jobs & Artifacts
-- =========================

CREATE TABLE IF NOT EXISTS job_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50)  NOT NULL UNIQUE,
    name VARCHAR(155) NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id       UUID        NOT NULL,
    status_id     INT         NOT NULL, -- queued, running, succeeded, failed, requires_payment
    error_message TEXT,
    input_ref     TEXT,
    output_ref    TEXT,
    feature_id    INT         NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at  TIMESTAMPTZ,
    CONSTRAINT fk_jobs_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_jobs_job_status FOREIGN KEY (status_id) REFERENCES job_status (id),
    CONSTRAINT fk_jobs_features FOREIGN KEY (feature_id) REFERENCES features (id)
);

CREATE TABLE IF NOT EXISTS tool_runs
(
    id                BIGSERIAL PRIMARY KEY,
    job_id            UUID        NOT NULL,
    provider          TEXT        NOT NULL,
    action_code       TEXT        NOT NULL,
    model             TEXT,
    cost_units_used   BIGINT      NOT NULL,
    input_stats_json  JSONB,
    output_stats_json JSONB,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_tool_runs_jobs FOREIGN KEY (job_id) REFERENCES jobs (id)
);

CREATE TABLE IF NOT EXISTS artifacts
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    job_id     UUID        NOT NULL,
    kind       TEXT        NOT NULL, -- audio, transcript, text, image
    uri        TEXT,
    bytes      BYTEA,
    mime       TEXT,
    meta_json  JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_artifacts_jobs FOREIGN KEY (job_id) REFERENCES jobs (id)
);

CREATE TABLE IF NOT EXISTS transcripts
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    job_id     UUID        NOT NULL,
    language   TEXT,
    text       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_transcripts_jobs FOREIGN KEY (job_id) REFERENCES jobs (id)
);

CREATE TABLE IF NOT EXISTS rewrites
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    job_id          UUID        NOT NULL,
    source_text_ref TEXT,
    target_style    TEXT,
    params_json     JSONB,
    output_text     TEXT,
    input_tokens    INT,
    output_tokens   INT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_rewrites_jobs FOREIGN KEY (job_id) REFERENCES jobs (id)
);

CREATE TABLE IF NOT EXISTS images
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    job_id          UUID        NOT NULL,
    prompt          TEXT,
    negative_prompt TEXT,
    params_json     JSONB,
    width           INT,
    height          INT,
    seed            BIGINT,
    output_uri      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_images_jobs FOREIGN KEY (job_id) REFERENCES jobs (id)
);

-- =========================
-- 7) Seed data
-- =========================

-- user_status
INSERT INTO user_status (code, name)
VALUES ('ACTIVE', 'Active'),
       ('INACTIVE', 'Inactive')
ON CONFLICT (code) DO NOTHING;

-- feature_units
INSERT INTO feature_units (code, name)
VALUES ('SECOND', 'Second'),
       ('TOKEN', 'Token')
ON CONFLICT (code) DO NOTHING;

-- features (link by unit code to avoid assuming IDs)
INSERT INTO features (code, name, feature_unit_id)
SELECT 'URL_TO_TRANSCRIPTION', 'URL to transcription', fu.id
FROM feature_units fu
WHERE fu.code = 'SECOND'
ON CONFLICT (code) DO NOTHING;

INSERT INTO features (code, name, feature_unit_id)
SELECT 'REWRITE_CONTENT', 'Rewrite the content', fu.id
FROM feature_units fu
WHERE fu.code = 'TOKEN'
ON CONFLICT (code) DO NOTHING;

INSERT INTO features (code, name, feature_unit_id)
SELECT 'TEXT_TO_IMAGE', 'Text to image', fu.id
FROM feature_units fu
WHERE fu.code = 'TOKEN'
ON CONFLICT (code) DO NOTHING;

-- job_status
INSERT INTO job_status (code, name)
VALUES ('QUEUED', 'Queued'),
       ('RUNNING', 'Running'),
       ('SUCCEEDED', 'Succeeded'),
       ('REQUIRES_PAYMENT', 'Requires payment')
ON CONFLICT (code) DO NOTHING;

-- pricing_rules by feature code
INSERT INTO pricing_rules (feature_id, unit_cost_in_credits, unit, min_step, notes, effective_from)
SELECT f.id,
       0.2000,
       'seconds',
       10,
       'Base transcription pricing, billed per 10s block',
       TIMESTAMPTZ '2025-01-01 00:00:00+00'
FROM features f
WHERE f.code = 'URL_TO_TRANSCRIPTION'
ON CONFLICT DO NOTHING;

INSERT INTO pricing_rules (feature_id, unit_cost_in_credits, unit, min_step, notes, effective_from)
SELECT f.id,
       0.0100,
       'tokens',
       100,
       'Standard rewrite pricing for GPT-4o-mini',
       TIMESTAMPTZ '2025-01-01 00:00:00+00'
FROM features f
WHERE f.code = 'REWRITE_CONTENT'
ON CONFLICT DO NOTHING;

INSERT INTO pricing_rules (feature_id, unit_cost_in_credits, unit, min_step, notes, effective_from)
SELECT f.id,
       15.0000,
       'images',
       1,
       'Standard image generation cost',
       TIMESTAMPTZ '2025-01-01 00:00:00+00'
FROM features f
WHERE f.code = 'TEXT_TO_IMAGE'
ON CONFLICT DO NOTHING;

INSERT INTO roles (code, name)
VALUES ('ROLE_USER', 'User'),
       ('ROLE_ADMIN', 'Admin')
ON CONFLICT (code) DO NOTHING;

COMMIT;
