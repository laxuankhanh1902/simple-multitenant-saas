-- Create subscription plans table
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    billing_period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    max_users INTEGER NOT NULL DEFAULT 10,
    max_projects INTEGER NOT NULL DEFAULT 5,
    storage_limit_gb INTEGER NOT NULL DEFAULT 100,
    api_rate_limit INTEGER NOT NULL DEFAULT 1000,
    features JSONB, -- JSON array of features
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE,
    trial_end_date DATE,
    auto_renew BOOLEAN NOT NULL DEFAULT true,
    payment_method VARCHAR(50),
    external_subscription_id VARCHAR(255), -- For payment processor reference
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_subscriptions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id),
    CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
);

-- Create billing records table
CREATE TABLE billing_records (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    subscription_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE,
    payment_method VARCHAR(50),
    external_invoice_id VARCHAR(255), -- For payment processor reference
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_billing_records_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id),
    CONSTRAINT fk_billing_records_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

-- Create indexes
CREATE INDEX idx_subscription_plans_name ON subscription_plans(name);
CREATE INDEX idx_subscription_plans_active ON subscription_plans(is_active);
CREATE INDEX idx_subscription_plans_price ON subscription_plans(price);

CREATE INDEX idx_subscriptions_tenant_id ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_plan_id ON subscriptions(plan_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_start_date ON subscriptions(start_date);
CREATE INDEX idx_subscriptions_end_date ON subscriptions(end_date);

CREATE INDEX idx_billing_records_tenant_id ON billing_records(tenant_id);
CREATE INDEX idx_billing_records_subscription_id ON billing_records(subscription_id);
CREATE INDEX idx_billing_records_status ON billing_records(status);
CREATE INDEX idx_billing_records_due_date ON billing_records(due_date);
CREATE INDEX idx_billing_records_invoice_number ON billing_records(invoice_number);

-- Add constraints
ALTER TABLE subscription_plans ADD CONSTRAINT chk_subscription_plan_billing_period 
    CHECK (billing_period IN ('MONTHLY', 'YEARLY', 'QUARTERLY'));

ALTER TABLE subscriptions ADD CONSTRAINT chk_subscription_status 
    CHECK (status IN ('ACTIVE', 'CANCELED', 'EXPIRED', 'SUSPENDED', 'TRIAL'));

ALTER TABLE billing_records ADD CONSTRAINT chk_billing_record_status 
    CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'CANCELED', 'REFUNDED'));

-- Add triggers
CREATE TRIGGER update_subscription_plans_updated_at 
    BEFORE UPDATE ON subscription_plans 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_subscriptions_updated_at 
    BEFORE UPDATE ON subscriptions 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_billing_records_updated_at 
    BEFORE UPDATE ON billing_records 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();