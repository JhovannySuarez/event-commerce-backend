ALTER TABLE tenants
ADD COLUMN country VARCHAR(100),
ADD COLUMN city VARCHAR(100);

CREATE INDEX idx_tenants_country_city ON tenants(country, city);