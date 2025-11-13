BEGIN;

-- =============================================
-- Extensions
-- =============================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =============================================
-- 1) Identity / RBAC
-- =============================================

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
    id          SERIAL PRIMARY KEY,
    first_name  TEXT        NOT NULL,
    middle_name TEXT,
    last_name   TEXT        NOT NULL,
    user_id     UUID        NOT NULL,
    address     TEXT,
    age         INT,
    logo        TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
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

CREATE INDEX IF NOT EXISTS idx_users_status ON users (status_id);


-- =============================================
-- 2) Features & Units & Pricing (credits per usage)
-- =============================================

CREATE TABLE IF NOT EXISTS feature_units
(
    id         SERIAL PRIMARY KEY,
    code       VARCHAR(50) UNIQUE NOT NULL, -- SECOND, TOKEN, IMAGE, etc.
    name       VARCHAR(155)       NOT NULL,
    created_at TIMESTAMPTZ        NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS features
(
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(50) UNIQUE NOT NULL, -- URL_TO_TRANSCRIPTION, REWRITE_CONTENT...
    name            VARCHAR(155)       NOT NULL,
    feature_unit_id INT,
    created_at      TIMESTAMPTZ        NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT fk_features_feature_units
        FOREIGN KEY (feature_unit_id) REFERENCES feature_units (id)
);

CREATE TABLE IF NOT EXISTS pricing_rules
(
    id                   SERIAL PRIMARY KEY,
    feature_id           INT            NOT NULL,
    unit_cost_in_credits NUMERIC(12, 4) NOT NULL, -- cost (credits) per unit
    unit                 TEXT           NOT NULL, -- seconds/tokens/images...
    min_step             INT            NOT NULL DEFAULT 1,
    notes                TEXT,
    effective_from       TIMESTAMPTZ    NOT NULL,
    effective_to         TIMESTAMPTZ,
    CONSTRAINT fk_pricing_rules_features
        FOREIGN KEY (feature_id) REFERENCES features (id)
);

CREATE INDEX IF NOT EXISTS idx_pricing_rules_feature_time
    ON pricing_rules (feature_id, effective_from DESC);


-- =============================================
-- 3) Subscription Plans & Limits
-- =============================================

-- Gói subscription: định nghĩa billing + giá + trạng thái
CREATE TABLE IF NOT EXISTS plans
(
    id             SERIAL PRIMARY KEY,
    code           VARCHAR(50) UNIQUE NOT NULL,
    name           VARCHAR(155)       NOT NULL,
    description    TEXT,
    price          NUMERIC(12, 2)     NOT NULL CHECK (price >= 0),
    currency       CHAR(3)            NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    billing_period VARCHAR(20)        NOT NULL
        CHECK (billing_period IN ('MONTHLY', 'YEARLY', 'WEEKLY', 'DAILY', 'LIFETIME')),
    active         BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ        NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ        NOT NULL DEFAULT now()
);

-- Limit per feature cho từng plan
-- included_units: số lượng "unit" của feature_unit (seconds/tokens/images) được dùng trong mỗi billing_period.
-- Khi vượt included_units -> dùng credit_wallet.
CREATE TABLE IF NOT EXISTS plan_features
(
    id             SERIAL PRIMARY KEY,
    plan_id        BIGINT      NOT NULL,
    feature_id     INT         NOT NULL,
    included_units BIGINT      NOT NULL DEFAULT 0,
    overage_policy VARCHAR(20) NOT NULL DEFAULT 'CREDITS'
        CHECK (overage_policy IN ('CREDITS', 'BLOCK')),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_plan_features_plan FOREIGN KEY (plan_id) REFERENCES plans (id) ON DELETE CASCADE,
    CONSTRAINT fk_plan_features_feature FOREIGN KEY (feature_id) REFERENCES features (id),
    CONSTRAINT uq_plan_features UNIQUE (plan_id, feature_id)
);

CREATE INDEX IF NOT EXISTS idx_plan_features_plan ON plan_features (plan_id);
CREATE INDEX IF NOT EXISTS idx_plan_features_feature ON plan_features (feature_id);


-- =============================================
-- 4) Subscriptions & Usage
-- =============================================

-- User đang dùng plan nào, trong khoảng nào
CREATE TABLE IF NOT EXISTS subscriptions
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id       UUID        NOT NULL,
    plan_id       BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL
        CHECK (status IN ('TRIALING', 'ACTIVE', 'EXPIRED', 'CANCELED', 'PENDING')),
    auto_renew    BOOLEAN     NOT NULL DEFAULT TRUE,
    start_at      TIMESTAMPTZ NOT NULL,
    end_at        TIMESTAMPTZ,
    canceled_at   TIMESTAMPTZ,
    cancel_reason TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES plans (id)
);

-- Đảm bảo mỗi user chỉ có 1 subscription ACTIVE/TRIALING tại 1 thời điểm (logic soft):
CREATE UNIQUE INDEX IF NOT EXISTS uq_subscriptions_user_active
    ON subscriptions (user_id)
    WHERE status IN ('TRIALING', 'ACTIVE');

CREATE INDEX IF NOT EXISTS idx_subscriptions_plan ON subscriptions (plan_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions (status);


-- Track usage theo subscription + feature + chu kỳ
-- Dùng để check nhanh limit thay vì scan jobs/tool_runs mỗi lần.
CREATE TABLE IF NOT EXISTS subscription_feature_usage
(
    id              SERIAL PRIMARY KEY,
    subscription_id UUID        NOT NULL,
    feature_id      INT         NOT NULL,
    period_start    TIMESTAMPTZ NOT NULL,
    period_end      TIMESTAMPTZ NOT NULL,
    used_units      BIGINT      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_sub_usage_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_usage_feature FOREIGN KEY (feature_id) REFERENCES features (id),
    CONSTRAINT uq_sub_usage UNIQUE (subscription_id, feature_id, period_start, period_end)
);

CREATE INDEX IF NOT EXISTS idx_sub_usage_sub_feature ON subscription_feature_usage (subscription_id, feature_id);


-- =============================================
-- 5) Credits (Wallet & Ledger)
-- =============================================

-- Ví credit: dùng khi vượt plan limit hoặc user chỉ xài credit
CREATE TABLE IF NOT EXISTS credit_wallets
(
    user_id         UUID PRIMARY KEY,
    balance_credits BIGINT      NOT NULL DEFAULT 0,
    free_credits    BIGINT      NOT NULL DEFAULT 0,
    effective_from  TIMESTAMPTZ NOT NULL DEFAULT now(),
    period          VARCHAR(50) NOT NULL DEFAULT 'MONTH',
    reset_day       INT         NOT NULL DEFAULT 1,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_credit_wallets_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Lịch sử +/- credits (top up, tiêu thụ, bonus...)
CREATE TABLE IF NOT EXISTS credit_ledger
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        UUID        NOT NULL,
    change_credits BIGINT      NOT NULL, -- + topup, - spend
    reason         TEXT        NOT NULL,
    meta_json      JSONB,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_credit_ledger_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_credit_ledger_user ON credit_ledger (user_id);


-- =============================================
-- 6) Catalog (SKU) & Orders (Topup / Subscription)
-- =============================================

CREATE TABLE IF NOT EXISTS order_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- PENDING, PAID, FAILED, CANCELED
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS order_types
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- SUBSCRIPTION, CREDIT_PACK
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS product_sku_types
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- CREDIT_PACK, SUBSCRIPTION_PLAN
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS product_skus
(
    id              UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    code            VARCHAR(150) UNIQUE NOT NULL,
    name            VARCHAR(155)        NOT NULL,
    type_id         INT                 NOT NULL,
    plan_id         BIGINT, -- nếu SKU này là subscription plan
    credits_granted BIGINT              NOT NULL DEFAULT 0,
    is_active       BOOLEAN             NOT NULL DEFAULT TRUE,
    meta_json       JSONB,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT fk_product_skus_type FOREIGN KEY (type_id) REFERENCES product_sku_types (id),
    CONSTRAINT fk_product_skus_plan FOREIGN KEY (plan_id) REFERENCES plans (id)
);

CREATE TABLE IF NOT EXISTS sku_prices
(
    id             BIGSERIAL PRIMARY KEY,
    sku_id         UUID           NOT NULL,
    currency       CHAR(3)        NOT NULL,
    unit_price     NUMERIC(12, 2) NOT NULL,
    effective_from TIMESTAMPTZ    NOT NULL,
    effective_to   TIMESTAMPTZ,
    region         VARCHAR(10)    NOT NULL DEFAULT '',
    CONSTRAINT fk_sku_prices_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id),
    CONSTRAINT uq_sku_prices UNIQUE (sku_id, currency, region, effective_from)
);

CREATE TABLE IF NOT EXISTS orders
(
    id           UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    user_id      UUID           NOT NULL,
    status_id    INT            NOT NULL,
    currency     CHAR(3)        NOT NULL DEFAULT 'USD',
    total_amount NUMERIC(12, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_orders_status FOREIGN KEY (status_id) REFERENCES order_status (id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    id                       BIGSERIAL PRIMARY KEY,
    order_id                 UUID           NOT NULL,
    order_type_id            INT            NOT NULL,
    sku_id                   UUID           NOT NULL,
    snapshot_name            VARCHAR(255),
    snapshot_unit_price      NUMERIC(12, 2) NOT NULL,
    snapshot_credits_granted BIGINT,
    qty                      INT            NOT NULL,
    created_at               TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_items_type FOREIGN KEY (order_type_id) REFERENCES order_types (id),
    CONSTRAINT fk_order_items_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id)
);

CREATE INDEX IF NOT EXISTS idx_orders_user ON orders (user_id);


-- =============================================
-- 7) Payments (Intent-based)
-- =============================================

CREATE TABLE IF NOT EXISTS payment_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- REQUIRES_ACTION, SUCCEEDED, FAILED, CANCELED
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS payment_intents
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    order_id      UUID        NOT NULL,
    provider      VARCHAR(50) NOT NULL, -- stripe, vnpay, paypal...
    provider_ref  VARCHAR(255),
    status_id     INT         NOT NULL,
    client_secret TEXT,
    raw_payload   JSONB,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    CONSTRAINT fk_payment_intents_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_payment_intents_status FOREIGN KEY (status_id) REFERENCES payment_status (id)
);

CREATE INDEX IF NOT EXISTS idx_payment_intents_order ON payment_intents (order_id);


-- =============================================
-- 8) Jobs & Usage Tracking to Features
-- =============================================

CREATE TABLE IF NOT EXISTS job_status
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL, -- QUEUED, RUNNING, SUCCEEDED, FAILED, REQUIRES_PAYMENT
    name VARCHAR(155)       NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL,
    subscription_id UUID, -- job chạy trong ngữ cảnh subscription nào (nếu có)
    feature_id      INT         NOT NULL,
    status_id       INT         NOT NULL,
    error_message   TEXT,
    input_ref       TEXT,
    output_ref      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at    TIMESTAMPTZ,
    CONSTRAINT fk_jobs_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_jobs_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions (id),
    CONSTRAINT fk_jobs_feature FOREIGN KEY (feature_id) REFERENCES features (id),
    CONSTRAINT fk_jobs_status FOREIGN KEY (status_id) REFERENCES job_status (id)
);

CREATE TABLE IF NOT EXISTS tool_runs
(
    id                BIGSERIAL PRIMARY KEY,
    job_id            UUID        NOT NULL,
    provider          TEXT        NOT NULL,
    action_code       TEXT        NOT NULL,
    model             TEXT,
    -- cost_units_used: số "unit" thực tế đã dùng (seconds/tokens/images...)
    cost_units_used   BIGINT      NOT NULL,
    input_stats_json  JSONB,
    output_stats_json JSONB,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_tool_runs_job FOREIGN KEY (job_id) REFERENCES jobs (id)
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
    CONSTRAINT fk_artifacts_job FOREIGN KEY (job_id) REFERENCES jobs (id)
);

CREATE TABLE IF NOT EXISTS transcripts
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    job_id     UUID        NOT NULL,
    language   TEXT,
    text       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_transcripts_job FOREIGN KEY (job_id) REFERENCES jobs (id)
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
    CONSTRAINT fk_rewrites_job FOREIGN KEY (job_id) REFERENCES jobs (id)
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
    CONSTRAINT fk_images_job FOREIGN KEY (job_id) REFERENCES jobs (id)
);


-- =============================================
-- 9) Triggers for updated_at
-- =============================================

CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Áp dụng cho các bảng có cột updated_at
DO
$$
    DECLARE
        r RECORD;
    BEGIN
        FOR r IN
            SELECT table_name
            FROM information_schema.columns
            WHERE column_name = 'updated_at'
              AND table_schema = 'public'
            LOOP
                EXECUTE format($f$
            DO $do$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_trigger
                    WHERE tgname = 'trg_%1$s_updated_at'
                      AND tgrelid = '%1$s'::regclass
                ) THEN
                    CREATE TRIGGER trg_%1$s_updated_at
                    BEFORE UPDATE ON %1$s
                    FOR EACH ROW
                    EXECUTE FUNCTION set_updated_at();
                END IF;
            END;
            $do$;
        $f$, r.table_name);
            END LOOP;
    END;
$$;


-- =============================================
-- 10) Seed data cơ bản
-- =============================================

INSERT INTO user_status (code, name)
VALUES ('ACTIVE', 'Active'),
       ('INACTIVE', 'Inactive')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (code, name)
VALUES ('ROLE_USER', 'User'),
       ('ROLE_ADMIN', 'Admin')
ON CONFLICT (code) DO NOTHING;

INSERT INTO feature_units (code, name)
VALUES ('SECOND', 'Second'),
       ('TOKEN', 'Token'),
       ('IMAGE', 'Image')
ON CONFLICT (code) DO NOTHING;

INSERT INTO features (code, name, feature_unit_id)
SELECT 'URL_TO_TRANSCRIPTION', 'URL to transcription', fu.id
FROM feature_units fu
WHERE fu.code = 'SECOND'
ON CONFLICT (code) DO NOTHING;

INSERT INTO features (code, name, feature_unit_id)
SELECT 'REWRITE_CONTENT', 'Rewrite content', fu.id
FROM feature_units fu
WHERE fu.code = 'TOKEN'
ON CONFLICT (code) DO NOTHING;

INSERT INTO features (code, name, feature_unit_id)
SELECT 'TEXT_TO_IMAGE', 'Text to image', fu.id
FROM feature_units fu
WHERE fu.code = 'IMAGE'
ON CONFLICT (code) DO NOTHING;

INSERT INTO job_status (code, name)
VALUES ('QUEUED', 'Queued'),
       ('RUNNING', 'Running'),
       ('SUCCEEDED', 'Succeeded'),
       ('FAILED', 'Failed'),
       ('REQUIRES_PAYMENT', 'Requires payment')
ON CONFLICT (code) DO NOTHING;

INSERT INTO order_status (code, name)
VALUES ('PENDING', 'Pending'),
       ('PAID', 'Paid'),
       ('FAILED', 'Failed'),
       ('CANCELED', 'Canceled')
ON CONFLICT (code) DO NOTHING;

INSERT INTO order_types (code, name)
VALUES ('SUBSCRIPTION', 'Subscription purchase'),
       ('CREDIT_PACK', 'Credit pack purchase')
ON CONFLICT (code) DO NOTHING;

INSERT INTO product_sku_types (code, name)
VALUES ('SUBSCRIPTION_PLAN', 'Subscription plan SKU'),
       ('CREDIT_PACK', 'Credit pack SKU')
ON CONFLICT (code) DO NOTHING;

INSERT INTO payment_status (code, name)
VALUES ('REQUIRES_ACTION', 'Requires action'),
       ('SUCCEEDED', 'Succeeded'),
       ('FAILED', 'Failed'),
       ('CANCELED', 'Canceled')
ON CONFLICT (code) DO NOTHING;

COMMIT;
