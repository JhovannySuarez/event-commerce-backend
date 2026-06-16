CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tenant_branding (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    logo_url TEXT,
    favicon_url TEXT,
    primary_color VARCHAR(20),
    secondary_color VARCHAR(20),
    accent_color VARCHAR(20),
    font_family VARCHAR(100),
    custom_domain VARCHAR(150),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id)
);

CREATE TABLE tenant_configuration (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    default_language VARCHAR(10) NOT NULL DEFAULT 'es',
    supported_languages TEXT[] NOT NULL DEFAULT ARRAY['es'],
    default_currency VARCHAR(10) NOT NULL DEFAULT 'COP',
    timezone VARCHAR(100) NOT NULL DEFAULT 'America/Bogota',
    show_prices_publicly BOOLEAN NOT NULL DEFAULT TRUE,
    require_login_to_quote BOOLEAN NOT NULL DEFAULT FALSE,
    allow_external_vendors BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID REFERENCES tenants(id),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password_hash TEXT,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE venues (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, slug)
);

CREATE TABLE venue_spaces (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    venue_id UUID NOT NULL REFERENCES venues(id),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    capacity_min INTEGER,
    capacity_max INTEGER,
    base_price NUMERIC(14,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE product_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, slug)
);

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    category_id UUID NOT NULL REFERENCES product_categories(id),
    name VARCHAR(150) NOT NULL,
    short_description TEXT,
    long_description TEXT,
    price NUMERIC(14,2),
    pricing_type VARCHAR(50) NOT NULL,
    included_items TEXT,
    excluded_items TEXT,
    requirements TEXT,
    restrictions TEXT,
    duration_minutes INTEGER,
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    highlighted BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE product_translations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id),
    language_code VARCHAR(10) NOT NULL,
    name VARCHAR(150) NOT NULL,
    short_description TEXT,
    long_description TEXT,
    included_items TEXT,
    excluded_items TEXT,
    requirements TEXT,
    restrictions TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, language_code)
);

CREATE TABLE product_media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id),
    media_url TEXT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE venue_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    venue_id UUID NOT NULL REFERENCES venues(id),
    rule_type VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    value TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quotations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    customer_id UUID REFERENCES users(id),
    venue_id UUID NOT NULL REFERENCES venues(id),
    event_space_id UUID REFERENCES venue_spaces(id),
    event_type VARCHAR(50),
    event_date DATE,
    guest_count INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    subtotal NUMERIC(14,2) NOT NULL DEFAULT 0,
    discount_total NUMERIC(14,2) NOT NULL DEFAULT 0,
    total NUMERIC(14,2) NOT NULL DEFAULT 0,
    paid_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
    balance_due NUMERIC(14,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quotation_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quotation_id UUID NOT NULL REFERENCES quotations(id),
    product_id UUID NOT NULL REFERENCES products(id),
    category_id UUID NOT NULL REFERENCES product_categories(id),
    product_name VARCHAR(150) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(14,2) NOT NULL,
    total NUMERIC(14,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quotation_payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    quotation_id UUID NOT NULL REFERENCES quotations(id),
    amount NUMERIC(14,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date DATE NOT NULL,
    reference VARCHAR(150),
    notes TEXT,
    receipt_url TEXT,
    registered_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_tenant_id ON users(tenant_id);

CREATE INDEX idx_venues_tenant_id ON venues(tenant_id);

CREATE INDEX idx_venue_spaces_venue_id ON venue_spaces(venue_id);

CREATE INDEX idx_product_categories_tenant_id ON product_categories(tenant_id);

CREATE INDEX idx_products_tenant_id ON products(tenant_id);

CREATE INDEX idx_products_category_id ON products(category_id);

CREATE INDEX idx_product_translations_product_id ON product_translations(product_id);

CREATE INDEX idx_product_media_product_id ON product_media(product_id);

CREATE INDEX idx_venue_rules_tenant_id ON venue_rules(tenant_id);

CREATE INDEX idx_venue_rules_venue_id ON venue_rules(venue_id);

CREATE INDEX idx_quotations_tenant_id ON quotations(tenant_id);

CREATE INDEX idx_quotations_customer_id ON quotations(customer_id);

CREATE INDEX idx_quotation_items_quotation_id ON quotation_items(quotation_id);

CREATE INDEX idx_quotation_payments_quotation_id ON quotation_payments(quotation_id);

ALTER TABLE venue_spaces
ADD CONSTRAINT chk_venue_spaces_capacity
CHECK (
    capacity_min IS NULL
    OR capacity_max IS NULL
    OR capacity_min <= capacity_max
);

ALTER TABLE quotations
ADD CONSTRAINT chk_quotations_guest_count
CHECK (guest_count IS NULL OR guest_count > 0);

ALTER TABLE quotation_items
ADD CONSTRAINT chk_quotation_items_quantity
CHECK (quantity > 0);

ALTER TABLE quotation_items
ADD CONSTRAINT chk_quotation_items_total
CHECK (total >= 0);

ALTER TABLE quotation_payments
ADD CONSTRAINT chk_quotation_payments_amount
CHECK (amount > 0);