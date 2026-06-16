CREATE TABLE venue_product_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    venue_id UUID NOT NULL REFERENCES venues(id),
    category_id UUID NOT NULL REFERENCES product_categories(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (venue_id, category_id)
);

CREATE INDEX idx_venue_product_categories_venue_id
ON venue_product_categories(venue_id);

CREATE INDEX idx_venue_product_categories_category_id
ON venue_product_categories(category_id);