ALTER TABLE product_media
ADD COLUMN usage_type VARCHAR(50) NOT NULL DEFAULT 'GALLERY',
ADD COLUMN alt_text VARCHAR(255),
ADD COLUMN thumbnail_url TEXT,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT NOW();

CREATE INDEX idx_product_media_usage_type
ON product_media(product_id, usage_type);

ALTER TABLE product_media
ADD CONSTRAINT chk_product_media_usage_type
CHECK (usage_type IN ('LISTING', 'GALLERY', 'DETAIL', 'THUMBNAIL'));

ALTER TABLE product_media
ADD CONSTRAINT chk_product_media_type
CHECK (media_type IN ('IMAGE', 'VIDEO'));