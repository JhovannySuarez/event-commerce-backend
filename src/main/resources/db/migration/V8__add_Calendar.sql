CREATE TABLE event_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    minimum_duration_hours INTEGER NOT NULL DEFAULT 24,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, name)
);

CREATE TABLE venue_calendar_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    venue_id UUID NOT NULL REFERENCES venues(id),
    event_type_id UUID REFERENCES event_types(id),
    quotation_id UUID REFERENCES quotations(id),
    title VARCHAR(150) NOT NULL,
    description TEXT,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TENTATIVE',
    source VARCHAR(50) NOT NULL DEFAULT 'MANUAL',
    customer_name VARCHAR(150),
    customer_email VARCHAR(150),
    customer_phone VARCHAR(50),
    internal_notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_venue_calendar_events_dates CHECK (end_at > start_at)
);

CREATE TABLE calendar_event_spaces (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    calendar_event_id UUID NOT NULL REFERENCES venue_calendar_events(id),
    event_space_id UUID NOT NULL REFERENCES event_spaces(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (calendar_event_id, event_space_id)
);

CREATE INDEX idx_event_types_tenant_id
ON event_types(tenant_id);

CREATE INDEX idx_calendar_events_tenant_id
ON venue_calendar_events(tenant_id);

CREATE INDEX idx_calendar_events_venue_id
ON venue_calendar_events(venue_id);

CREATE INDEX idx_calendar_events_dates
ON venue_calendar_events(start_at, end_at);

CREATE INDEX idx_calendar_event_spaces_event_id
ON calendar_event_spaces(calendar_event_id);

CREATE INDEX idx_calendar_event_spaces_space_id
ON calendar_event_spaces(event_space_id);

ALTER TABLE event_types
ADD CONSTRAINT chk_event_types_duration
CHECK (
    minimum_duration_hours > 0
);