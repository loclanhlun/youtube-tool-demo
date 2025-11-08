BEGIN;

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

COMMIT;