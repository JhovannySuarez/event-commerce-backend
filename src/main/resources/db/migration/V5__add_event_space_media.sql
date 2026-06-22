CREATE TABLE event_spaces (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    venue_id UUID NOT NULL
        REFERENCES venues(id),

    name VARCHAR(150) NOT NULL,

    description TEXT,

    capacity_min INTEGER,

    capacity_max INTEGER,

    base_price NUMERIC(14,2),

    display_order INTEGER NOT NULL DEFAULT 0,

    highlighted BOOLEAN NOT NULL DEFAULT FALSE,

    event_space_type VARCHAR(50),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_event_spaces_capacity
        CHECK (
            capacity_min IS NULL
            OR capacity_max IS NULL
            OR capacity_min <= capacity_max
        ),

    CONSTRAINT chk_event_spaces_base_price
        CHECK (base_price IS NULL OR base_price >= 0)
);

CREATE INDEX idx_event_spaces_venue_id
ON event_spaces(venue_id);

CREATE INDEX idx_event_spaces_display_order
ON event_spaces(venue_id, display_order);

CREATE INDEX idx_event_spaces_active
ON event_spaces(venue_id, active);

ALTER TABLE event_spaces
ADD CONSTRAINT chk_event_spaces_type
CHECK (
    event_space_type IS NULL OR
    event_space_type IN (
        'INDOOR',
        'OUTDOOR',
        'TERRACE',
        'GARDEN',
        'BEACH',
        'ROOFTOP',
        'OTHER'
    )
);


CREATE TABLE event_space_media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    event_space_id UUID NOT NULL
        REFERENCES event_spaces(id),

    media_url TEXT NOT NULL,

    media_type VARCHAR(20) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_event_space_media_event_space_id
ON event_space_media(event_space_id);