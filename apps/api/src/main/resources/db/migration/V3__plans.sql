BEGIN;

INSERT INTO plans (code, name, description, price, currency, billing_period)
VALUES ('FREE', 'Free 1 Month', 'Free for one month', 0, 'USD', 'MONTHLY'),
       ('PRO_MONTHLY', 'Pro monthly', 'For pro monthly', 9.99, 'USD', 'MONTHLY'),
       ('PRO_YEARLY', 'Pro yearly', 'For pro yearly', 99.00, 'USD', 'YEARLY')
ON CONFLICT DO NOTHING;


INSERT INTO plan_features (plan_id, feature_id, included_units, overage_policy)
-- FREE PLAN
VALUES (1, 1, 600, 'CREDITS'),
       (1, 2, 50000, 'CREDITS'),
       (1, 3, 10, 'CREDITS'),
--PRO MONTHLY PLAN
       (2, 1, 10000, 'CREDITS'),
       (2, 2, 150000, 'CREDITS'),
       (2, 3, 30, 'CREDITS'),
-- PRO YEARLY PLAN
       (3, 1, 100000, 'CREDITS'),
       (3, 2, 500000, 'CREDITS'),
       (3, 3, 100, 'CREDITS')
ON CONFLICT DO NOTHING;
COMMIT;