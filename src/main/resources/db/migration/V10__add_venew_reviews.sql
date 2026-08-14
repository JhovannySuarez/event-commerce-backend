CREATE TABLE venue_reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    venue_id UUID NOT NULL REFERENCES venues(id),

    customer_id UUID,

    rating INTEGER NOT NULL,

    title VARCHAR(200),

    review TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_rating
    CHECK (rating BETWEEN 1 AND 5)
);

ALTER TABLE venues
ADD COLUMN average_rating NUMERIC(3,2) DEFAULT 0,
ADD COLUMN review_count INTEGER DEFAULT 0;


ALTER TABLE tenants
ADD COLUMN country_code VARCHAR(10);

ALTER TABLE tenants
ADD COLUMN currency_code VARCHAR(10);