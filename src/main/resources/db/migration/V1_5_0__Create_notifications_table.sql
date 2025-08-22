-- Create notification templates table
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject_template TEXT,
    body_template TEXT NOT NULL,
    variables JSONB, -- Expected variables for the template
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    recipient_id BIGINT NOT NULL,
    recipient_email VARCHAR(255),
    template_id BIGINT,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    subject VARCHAR(500),
    body TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    failed_at TIMESTAMP,
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    error_message TEXT,
    metadata JSONB,
    external_id VARCHAR(255), -- For external service reference
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_template FOREIGN KEY (template_id) REFERENCES notification_templates(id)
);

-- Create notification preferences table
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_notification_preferences UNIQUE (user_id, notification_type, channel),
    CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create feature flags table
CREATE TABLE feature_flags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(20) NOT NULL DEFAULT 'BOOLEAN',
    default_value TEXT NOT NULL,
    is_global BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

-- Create tenant feature flags table
CREATE TABLE tenant_feature_flags (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    feature_flag_id BIGINT NOT NULL,
    value TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_tenant_feature_flags UNIQUE (tenant_id, feature_flag_id),
    CONSTRAINT fk_tenant_feature_flags_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_feature_flags_flag FOREIGN KEY (feature_flag_id) REFERENCES feature_flags(id) ON DELETE CASCADE
);

-- Create indexes for notification_templates
CREATE INDEX idx_notification_templates_name ON notification_templates(name);
CREATE INDEX idx_notification_templates_type ON notification_templates(type);
CREATE INDEX idx_notification_templates_channel ON notification_templates(channel);
CREATE INDEX idx_notification_templates_active ON notification_templates(is_active);

-- Create indexes for notifications
CREATE INDEX idx_notifications_tenant_id ON notifications(tenant_id);
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_channel ON notifications(channel);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_priority ON notifications(priority);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);

-- Create indexes for notification_preferences
CREATE INDEX idx_notification_preferences_tenant_id ON notification_preferences(tenant_id);
CREATE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);
CREATE INDEX idx_notification_preferences_type ON notification_preferences(notification_type);

-- Create indexes for feature_flags
CREATE INDEX idx_feature_flags_name ON feature_flags(name);
CREATE INDEX idx_feature_flags_active ON feature_flags(is_active);
CREATE INDEX idx_feature_flags_global ON feature_flags(is_global);

-- Create indexes for tenant_feature_flags
CREATE INDEX idx_tenant_feature_flags_tenant_id ON tenant_feature_flags(tenant_id);
CREATE INDEX idx_tenant_feature_flags_flag_id ON tenant_feature_flags(feature_flag_id);
CREATE INDEX idx_tenant_feature_flags_active ON tenant_feature_flags(is_active);

-- Add constraints
ALTER TABLE notification_templates ADD CONSTRAINT chk_notification_template_channel 
    CHECK (channel IN ('EMAIL', 'SMS', 'PUSH', 'IN_APP', 'WEBHOOK'));

ALTER TABLE notification_templates ADD CONSTRAINT chk_notification_template_type 
    CHECK (type IN ('WELCOME', 'VERIFICATION', 'PASSWORD_RESET', 'BILLING', 'SYSTEM_ALERT', 'PROJECT_UPDATE', 'CUSTOM'));

ALTER TABLE notifications ADD CONSTRAINT chk_notification_channel 
    CHECK (channel IN ('EMAIL', 'SMS', 'PUSH', 'IN_APP', 'WEBHOOK'));

ALTER TABLE notifications ADD CONSTRAINT chk_notification_priority 
    CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'));

ALTER TABLE notifications ADD CONSTRAINT chk_notification_status 
    CHECK (status IN ('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED', 'CANCELED'));

ALTER TABLE notification_preferences ADD CONSTRAINT chk_notification_pref_channel 
    CHECK (channel IN ('EMAIL', 'SMS', 'PUSH', 'IN_APP'));

ALTER TABLE feature_flags ADD CONSTRAINT chk_feature_flag_type 
    CHECK (type IN ('BOOLEAN', 'STRING', 'INTEGER', 'DECIMAL', 'JSON'));

-- Add triggers
CREATE TRIGGER update_notification_templates_updated_at 
    BEFORE UPDATE ON notification_templates 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notifications_updated_at 
    BEFORE UPDATE ON notifications 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notification_preferences_updated_at 
    BEFORE UPDATE ON notification_preferences 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_feature_flags_updated_at 
    BEFORE UPDATE ON feature_flags 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tenant_feature_flags_updated_at 
    BEFORE UPDATE ON tenant_feature_flags 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();