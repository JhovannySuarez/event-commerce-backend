-- ============================================================
-- V18__seed_event_spaces.sql
-- Seed event spaces and event type relationships
-- ============================================================


-- ============================================================
-- Medellín - Aguas Claras
-- ============================================================

INSERT INTO event_spaces (
    id,
    venue_id,
    name,
    description,
    capacity_min,
    capacity_max,
    base_price,
    display_order,
    highlighted,
    active,
    created_at,
    updated_at
)
VALUES
(
    '10000000-0000-0000-0000-000000000001',
    '33373db8-94e8-4620-9715-f6e9aa192a9f',
    'Salón Principal',
    'Espacio amplio para celebraciones sociales y eventos corporativos.',
    50,
    150,
    2500000,
    1,
    TRUE,
    TRUE,
    NOW(),
    NOW()
),
(
    '10000000-0000-0000-0000-000000000002',
    '33373db8-94e8-4620-9715-f6e9aa192a9f',
    'Auditorio',
    'Espacio de gran capacidad para conferencias, eventos corporativos y grandes eventos.',
    50,
    300,
    5000000,
    2,
    TRUE,
    TRUE,
    NOW(),
    NOW()
);


-- ============================================================
-- Bogotá - Aguas Claras Bogotá
-- ============================================================

INSERT INTO event_spaces (
    id,
    venue_id,
    name,
    description,
    capacity_min,
    capacity_max,
    base_price,
    display_order,
    highlighted,
    active,
    created_at,
    updated_at
)
VALUES
(
    '10000000-0000-0000-0000-000000000003',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    'Salón Social',
    'Espacio diseñado para bodas, celebraciones y otros eventos sociales.',
    20,
    100,
    1800000,
    1,
    TRUE,
    TRUE,
    NOW(),
    NOW()
),
(
    '10000000-0000-0000-0000-000000000004',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    'Salón Corporativo',
    'Espacio para reuniones y eventos empresariales.',
    10,
    80,
    1200000,
    2,
    FALSE,
    TRUE,
    NOW(),
    NOW()
);


-- ============================================================
-- Event type relationships
-- ============================================================

-- ============================================================
-- Medellín - Salón Principal
-- ============================================================

INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000001',
    et.id,
    'FULL_DAY',
    NULL,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Evento Social';


INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000001',
    et.id,
    'HOURLY',
    4,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Evento Corporativo';


INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000001',
    et.id,
    'FULL_DAY',
    NULL,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Grandes Eventos';


-- ============================================================
-- Medellín - Auditorio
-- ============================================================

INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000002',
    et.id,
    'HOURLY',
    2,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Evento Corporativo';


INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000002',
    et.id,
    'FULL_DAY',
    NULL,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Grandes Eventos';


-- ============================================================
-- Bogotá - Salón Social
-- ============================================================

INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000003',
    et.id,
    'FULL_DAY',
    NULL,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Evento Social';


-- ============================================================
-- Bogotá - Salón Corporativo
-- ============================================================

INSERT INTO event_space_event_types (
    id,
    event_space_id,
    event_type_id,
    booking_mode,
    minimum_hours,
    active,
    created_at
)
SELECT
    gen_random_uuid(),
    '10000000-0000-0000-0000-000000000004',
    et.id,
    'HOURLY',
    4,
    TRUE,
    NOW()
FROM event_types et
WHERE et.tenant_id = 'f2f21f19-0a8c-48bf-acda-517e2ed139f1'
  AND et.name = 'Evento Corporativo';