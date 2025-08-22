-- Create tenants table
CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    subdomain VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    admin_email VARCHAR(255) NOT NULL,
    admin_first_name VARCHAR(50),
    admin_last_name VARCHAR(50),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    trial_end_date TIMESTAMP,
    max_users INTEGER NOT NULL DEFAULT 10,
    storage_limit_gb INTEGER NOT NULL DEFAULT 100,
    api_rate_limit INTEGER NOT NULL DEFAULT 1000,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for better query performance
CREATE INDEX idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_trial_end_date ON tenants(trial_end_date);
CREATE INDEX idx_tenants_created_at ON tenants(created_at);

-- Add constraints
ALTER TABLE tenants ADD CONSTRAINT chk_tenant_status 
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TRIAL', 'CANCELED'));

-- Add trigger to update updated_at automatically
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tenants_updated_at 
    BEFORE UPDATE ON tenants 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();