-- Create audit logs table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT,
    username VARCHAR(100),
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    session_id VARCHAR(255),
    request_id VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create system events table
CREATE TABLE system_events (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255),
    event_type VARCHAR(100) NOT NULL,
    event_category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    message TEXT NOT NULL,
    source VARCHAR(100),
    metadata JSONB,
    user_id BIGINT,
    ip_address INET,
    resolved BOOLEAN NOT NULL DEFAULT false,
    resolved_at TIMESTAMP,
    resolved_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_system_events_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_system_events_resolved_by FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Create user activity table
CREATE TABLE user_activity (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    activity_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(255),
    description TEXT,
    metadata JSONB,
    ip_address INET,
    user_agent TEXT,
    duration_ms INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create usage metrics table
CREATE TABLE usage_metrics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    metric_type VARCHAR(100) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    value DECIMAL(20, 6) NOT NULL,
    unit VARCHAR(20),
    tags JSONB,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for audit_logs
CREATE INDEX idx_audit_logs_tenant_id ON audit_logs(tenant_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_ip_address ON audit_logs(ip_address);

-- Create indexes for system_events
CREATE INDEX idx_system_events_tenant_id ON system_events(tenant_id);
CREATE INDEX idx_system_events_event_type ON system_events(event_type);
CREATE INDEX idx_system_events_event_category ON system_events(event_category);
CREATE INDEX idx_system_events_severity ON system_events(severity);
CREATE INDEX idx_system_events_user_id ON system_events(user_id);
CREATE INDEX idx_system_events_resolved ON system_events(resolved);
CREATE INDEX idx_system_events_created_at ON system_events(created_at);

-- Create indexes for user_activity
CREATE INDEX idx_user_activity_tenant_id ON user_activity(tenant_id);
CREATE INDEX idx_user_activity_user_id ON user_activity(user_id);
CREATE INDEX idx_user_activity_activity_type ON user_activity(activity_type);
CREATE INDEX idx_user_activity_resource_type ON user_activity(resource_type);
CREATE INDEX idx_user_activity_created_at ON user_activity(created_at);

-- Create indexes for usage_metrics
CREATE INDEX idx_usage_metrics_tenant_id ON usage_metrics(tenant_id);
CREATE INDEX idx_usage_metrics_metric_type ON usage_metrics(metric_type);
CREATE INDEX idx_usage_metrics_metric_name ON usage_metrics(metric_name);
CREATE INDEX idx_usage_metrics_recorded_at ON usage_metrics(recorded_at);
CREATE INDEX idx_usage_metrics_created_at ON usage_metrics(created_at);

-- Add constraints
ALTER TABLE system_events ADD CONSTRAINT chk_system_event_severity 
    CHECK (severity IN ('DEBUG', 'INFO', 'WARN', 'ERROR', 'CRITICAL'));

ALTER TABLE system_events ADD CONSTRAINT chk_system_event_category 
    CHECK (event_category IN ('SECURITY', 'SYSTEM', 'USER', 'BILLING', 'INTEGRATION', 'PERFORMANCE'));

-- Create function to automatically log user activities
CREATE OR REPLACE FUNCTION log_user_activity()
RETURNS TRIGGER AS $$
BEGIN
    -- This would be customized based on the table being audited
    INSERT INTO audit_logs (
        tenant_id, user_id, entity_type, entity_id, action, 
        old_values, new_values, created_at
    ) VALUES (
        COALESCE(NEW.tenant_id, OLD.tenant_id),
        COALESCE(NEW.updated_by, OLD.updated_by, NEW.created_by, OLD.created_by)::BIGINT,
        TG_TABLE_NAME,
        COALESCE(NEW.id, OLD.id)::TEXT,
        TG_OP,
        CASE WHEN TG_OP IN ('UPDATE', 'DELETE') THEN to_jsonb(OLD) ELSE NULL END,
        CASE WHEN TG_OP IN ('INSERT', 'UPDATE') THEN to_jsonb(NEW) ELSE NULL END,
        CURRENT_TIMESTAMP
    );
    
    RETURN COALESCE(NEW, OLD);
END;
$$ language 'plpgsql';