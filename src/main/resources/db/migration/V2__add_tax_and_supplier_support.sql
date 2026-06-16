-- =====================================================
-- TAX SUPPORT
-- =====================================================

CREATE TABLE taxes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rate NUMERIC(5,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_taxes_tenant_id ON taxes(tenant_id);

CREATE TABLE tenant_tax_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    tax_id UUID NOT NULL REFERENCES taxes(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, tax_id)
);

CREATE INDEX idx_tenant_tax_settings_tenant_id ON tenant_tax_settings(tenant_id);
CREATE INDEX idx_tenant_tax_settings_tax_id ON tenant_tax_settings(tax_id);

CREATE TABLE category_tax_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID NOT NULL REFERENCES product_categories(id),
    tax_id UUID NOT NULL REFERENCES taxes(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (category_id, tax_id)
);

CREATE INDEX idx_category_tax_settings_category_id ON category_tax_settings(category_id);
CREATE INDEX idx_category_tax_settings_tax_id ON category_tax_settings(tax_id);

ALTER TABLE tenant_configuration
ADD COLUMN prices_include_taxes BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE quotations
ADD COLUMN tax_total NUMERIC(14,2) NOT NULL DEFAULT 0;

ALTER TABLE quotation_items
ADD COLUMN subtotal NUMERIC(14,2) NOT NULL DEFAULT 0,
ADD COLUMN tax_total NUMERIC(14,2) NOT NULL DEFAULT 0;

CREATE TABLE quotation_item_taxes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quotation_item_id UUID NOT NULL REFERENCES quotation_items(id),
    tax_id UUID NOT NULL REFERENCES taxes(id),
    tax_name VARCHAR(100) NOT NULL,
    tax_rate NUMERIC(5,2) NOT NULL,
    tax_amount NUMERIC(14,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quotation_item_taxes_item_id ON quotation_item_taxes(quotation_item_id);
CREATE INDEX idx_quotation_item_taxes_tax_id ON quotation_item_taxes(tax_id);

ALTER TABLE taxes
ADD CONSTRAINT chk_taxes_rate
CHECK (rate >= 0);

ALTER TABLE quotation_item_taxes
ADD CONSTRAINT chk_quotation_item_taxes_amount
CHECK (tax_amount >= 0);

-- =====================================================
-- SUPPLIER SUPPORT
-- =====================================================

CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(150) NOT NULL,
    contact_name VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(50),
    address TEXT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_suppliers_tenant_id ON suppliers(tenant_id);

ALTER TABLE products
ADD COLUMN source_type VARCHAR(50) NOT NULL DEFAULT 'INTERNAL',
ADD COLUMN supplier_id UUID REFERENCES suppliers(id),
ADD COLUMN internal_notes TEXT;

CREATE INDEX idx_products_supplier_id ON products(supplier_id);

ALTER TABLE products
ADD CONSTRAINT chk_products_supplier
CHECK (
    (source_type = 'INTERNAL' AND supplier_id IS NULL)
    OR
    (source_type = 'EXTERNAL' AND supplier_id IS NOT NULL)
);