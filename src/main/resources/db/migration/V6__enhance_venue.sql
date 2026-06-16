ALTER TABLE venues
ADD COLUMN phone VARCHAR(50),
ADD COLUMN email VARCHAR(150),
ADD COLUMN latitude DOUBLE PRECISION,
ADD COLUMN longitude DOUBLE PRECISION,
ADD COLUMN instagram_url TEXT,
ADD COLUMN facebook_url TEXT,
ADD COLUMN website_url TEXT,
ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0,
ADD COLUMN highlighted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_venues_city_country
ON venues(city, country);

CREATE INDEX idx_venues_display_order
ON venues(tenant_id, display_order);

CREATE TABLE venue_media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    venue_id UUID NOT NULL REFERENCES venues(id),
    media_url TEXT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_venue_media_venue_id
ON venue_media(venue_id);

CREATE INDEX idx_venue_media_display_order
ON venue_media(venue_id, display_order);