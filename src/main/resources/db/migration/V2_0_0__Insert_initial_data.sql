-- Insert initial subscription plans
INSERT INTO subscription_plans (name, description, price, currency, billing_period, max_users, max_projects, storage_limit_gb, api_rate_limit, features, is_active, sort_order) VALUES 
('Free Trial', '30-day free trial with basic features', 0.00, 'USD', 'MONTHLY', 5, 3, 10, 500, '["basic_analytics", "email_support"]', true, 1),
('Starter', 'Perfect for small teams getting started', 29.00, 'USD', 'MONTHLY', 10, 10, 100, 1000, '["basic_analytics", "email_support", "project_templates"]', true, 2),
('Professional', 'Advanced features for growing businesses', 79.00, 'USD', 'MONTHLY', 25, 50, 500, 5000, '["advanced_analytics", "priority_support", "custom_integrations", "project_templates", "api_access"]', true, 3),
('Enterprise', 'Full-featured plan for large organizations', 199.00, 'USD', 'MONTHLY', 100, 200, 2000, 20000, '["advanced_analytics", "priority_support", "custom_integrations", "project_templates", "api_access", "sso", "audit_logs", "custom_branding"]', true, 4);

-- Insert notification templates
INSERT INTO notification_templates (name, description, type, channel, subject_template, body_template, variables) VALUES 
('welcome_email', 'Welcome email for new users', 'WELCOME', 'EMAIL', 
 'Welcome to {{organization_name}}!', 
 'Hi {{first_name}},\n\nWelcome to {{organization_name}}! We''re excited to have you on board.\n\nYou can get started by logging in to your dashboard: {{dashboard_url}}\n\nIf you have any questions, don''t hesitate to reach out to our support team.\n\nBest regards,\nThe {{organization_name}} Team',
 '["first_name", "organization_name", "dashboard_url"]'),

('email_verification', 'Email verification template', 'VERIFICATION', 'EMAIL',
 'Verify your email address',
 'Hi {{first_name}},\n\nPlease verify your email address by clicking the link below:\n\n{{verification_url}}\n\nThis link will expire in 24 hours.\n\nIf you didn''t create an account, please ignore this email.\n\nBest regards,\nThe Team',
 '["first_name", "verification_url"]'),

('password_reset', 'Password reset template', 'PASSWORD_RESET', 'EMAIL',
 'Reset your password',
 'Hi {{first_name}},\n\nYou requested to reset your password. Click the link below to create a new password:\n\n{{reset_url}}\n\nThis link will expire in 1 hour.\n\nIf you didn''t request this reset, please ignore this email.\n\nBest regards,\nThe Team',
 '["first_name", "reset_url"]'),

('trial_expiring', 'Trial expiring notification', 'SYSTEM_ALERT', 'EMAIL',
 'Your trial is expiring soon',
 'Hi {{first_name}},\n\nYour trial for {{organization_name}} will expire in {{days_remaining}} days.\n\nTo continue using our service without interruption, please upgrade your plan:\n\n{{upgrade_url}}\n\nIf you have any questions, our team is here to help.\n\nBest regards,\nThe Team',
 '["first_name", "organization_name", "days_remaining", "upgrade_url"]'),

('billing_success', 'Successful billing notification', 'BILLING', 'EMAIL',
 'Payment confirmation - {{organization_name}}',
 'Hi {{first_name}},\n\nThank you! We''ve successfully processed your payment of {{amount}} {{currency}} for {{organization_name}}.\n\nInvoice: {{invoice_number}}\nBilling Period: {{billing_period}}\n\nYou can view your invoice and billing history in your dashboard: {{billing_url}}\n\nBest regards,\nThe Team',
 '["first_name", "organization_name", "amount", "currency", "invoice_number", "billing_period", "billing_url"]'),

('project_invitation', 'Project invitation template', 'PROJECT_UPDATE', 'EMAIL',
 'You''ve been invited to join {{project_name}}',
 'Hi {{first_name}},\n\n{{inviter_name}} has invited you to join the project "{{project_name}}" in {{organization_name}}.\n\nProject Description: {{project_description}}\nYour Role: {{role}}\n\nClick here to accept the invitation: {{invitation_url}}\n\nBest regards,\nThe {{organization_name}} Team',
 '["first_name", "inviter_name", "project_name", "organization_name", "project_description", "role", "invitation_url"]');

-- Insert default feature flags
INSERT INTO feature_flags (name, description, type, default_value, is_global, is_active) VALUES 
('enable_analytics', 'Enable advanced analytics features', 'BOOLEAN', 'false', false, true),
('enable_api_access', 'Enable API access for tenants', 'BOOLEAN', 'false', false, true),
('enable_sso', 'Enable Single Sign-On integration', 'BOOLEAN', 'false', false, true),
('enable_audit_logs', 'Enable detailed audit logging', 'BOOLEAN', 'false', false, true),
('enable_custom_branding', 'Enable custom branding options', 'BOOLEAN', 'false', false, true),
('max_file_upload_size', 'Maximum file upload size in MB', 'INTEGER', '10', false, true),
('session_timeout', 'Session timeout in minutes', 'INTEGER', '60', true, true),
('enable_maintenance_mode', 'Enable maintenance mode', 'BOOLEAN', 'false', true, true),
('api_rate_limit_window', 'API rate limit window in seconds', 'INTEGER', '60', false, true),
('enable_email_notifications', 'Enable email notifications', 'BOOLEAN', 'true', true, true);

-- Insert system admin user (for initial setup)
-- Note: This would typically be done through a separate setup process
-- Password: admin123 (hashed with BCrypt)
INSERT INTO tenants (tenant_id, name, subdomain, admin_email, admin_first_name, admin_last_name, status, max_users, storage_limit_gb, api_rate_limit) 
VALUES ('system', 'System Admin', 'system', 'admin@localhost', 'System', 'Admin', 'ACTIVE', 1000, 10000, 100000);

INSERT INTO users (tenant_id, username, email, password, first_name, last_name, status, enabled, email_verified) 
VALUES ('system', 'admin', 'admin@localhost', '$2a$10$Q1z7Y1Ej1kR5J3mM8G6k9.F8H9bB2xN7V1dD0fT5kL3jO9qZ8wW5U', 'System', 'Admin', 'ACTIVE', true, true);

-- Insert admin role for system admin
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');

-- Insert default notification preferences for common types
-- These will be used as templates for new users
INSERT INTO notification_preferences (tenant_id, user_id, notification_type, channel, enabled) VALUES 
('system', 1, 'WELCOME', 'EMAIL', true),
('system', 1, 'VERIFICATION', 'EMAIL', true),
('system', 1, 'PASSWORD_RESET', 'EMAIL', true),
('system', 1, 'BILLING', 'EMAIL', true),
('system', 1, 'SYSTEM_ALERT', 'EMAIL', true),
('system', 1, 'PROJECT_UPDATE', 'EMAIL', true);

-- Add some initial usage metrics for demonstration
INSERT INTO usage_metrics (tenant_id, metric_type, metric_name, value, unit, tags) VALUES 
('system', 'SYSTEM', 'users_created', 1, 'count', '{"source": "initial_setup"}'),
('system', 'SYSTEM', 'tenants_created', 1, 'count', '{"source": "initial_setup"}'),
('system', 'SYSTEM', 'storage_used', 0, 'bytes', '{"source": "initial_setup"}');

-- Insert initial system event
INSERT INTO system_events (tenant_id, event_type, event_category, severity, message, source) VALUES 
('system', 'SYSTEM_INITIALIZED', 'SYSTEM', 'INFO', 'System has been initialized with default data', 'database_migration');