-- Sample data for Multi-Tenant SaaS Framework
-- This script creates realistic sample data for testing

-- First, let's create the tables (these will be managed by Flyway in production)
-- Note: This is temporary for setup - Flyway migrations should handle this

-- Tenants table
CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    subdomain VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    admin_email VARCHAR(255) NOT NULL,
    admin_first_name VARCHAR(50),
    admin_last_name VARCHAR(50),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    country VARCHAR(50),
    postal_code VARCHAR(10),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    trial_end_date TIMESTAMP,
    max_users INTEGER DEFAULT 10,
    storage_limit_gb INTEGER DEFAULT 100,
    api_rate_limit INTEGER DEFAULT 1000,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'en_US',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    enabled BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    last_login TIMESTAMP,
    login_count BIGINT DEFAULT 0,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1,
    UNIQUE(tenant_id, username),
    UNIQUE(tenant_id, email)
);

-- User roles table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Projects table
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL REFERENCES users(id),
    start_date DATE,
    end_date DATE,
    budget DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'USD',
    priority VARCHAR(10) DEFAULT 'MEDIUM',
    completion_percentage INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1,
    UNIQUE(tenant_id, name)
);

-- Project members table
CREATE TABLE IF NOT EXISTS project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id)
);

-- Subscription plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    billing_period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    max_users INTEGER,
    storage_limit_gb INTEGER,
    api_rate_limit INTEGER,
    features TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tenant subscriptions table
CREATE TABLE IF NOT EXISTS tenant_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT true,
    billing_email VARCHAR(255),
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    subscription_id BIGINT NOT NULL REFERENCES tenant_subscriptions(id),
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    issue_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    paid_date TIMESTAMP,
    payment_method VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100),
    action VARCHAR(20) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    session_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User activities table
CREATE TABLE IF NOT EXISTS user_activities (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    activity_type VARCHAR(50) NOT NULL,
    description TEXT,
    metadata JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- System events table
CREATE TABLE IF NOT EXISTS system_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    severity VARCHAR(10) NOT NULL DEFAULT 'INFO',
    message TEXT NOT NULL,
    details JSONB,
    tenant_id VARCHAR(255),
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    message TEXT,
    type VARCHAR(20) NOT NULL DEFAULT 'INFO',
    priority VARCHAR(10) DEFAULT 'NORMAL',
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- API usage table
CREATE TABLE IF NOT EXISTS api_usage (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    endpoint VARCHAR(200) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INTEGER NOT NULL,
    response_time_ms INTEGER,
    request_size_bytes BIGINT,
    response_size_bytes BIGINT,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Usage metrics table
CREATE TABLE IF NOT EXISTS usage_metrics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    metric_name VARCHAR(50) NOT NULL,
    metric_value DECIMAL(15,2) NOT NULL,
    metric_unit VARCHAR(20),
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, metric_name, period_start)
);

-- File uploads table
CREATE TABLE IF NOT EXISTS file_uploads (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100),
    checksum VARCHAR(64),
    is_public BOOLEAN NOT NULL DEFAULT false,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tenant settings table
CREATE TABLE IF NOT EXISTS tenant_settings (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT,
    setting_type VARCHAR(20) DEFAULT 'STRING',
    is_encrypted BOOLEAN NOT NULL DEFAULT false,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    UNIQUE(tenant_id, setting_key)
);

-- Insert sample tenants
INSERT INTO tenants (tenant_id, name, subdomain, description, admin_email, admin_first_name, admin_last_name, phone, city, country, status, trial_end_date, max_users, storage_limit_gb) VALUES
('acme-corp', 'ACME Corporation', 'acme', 'Global manufacturing and logistics company', 'admin@acme-corp.com', 'John', 'Smith', '+1-555-0101', 'New York', 'USA', 'ACTIVE', NULL, 50, 500),
('techstart-inc', 'TechStart Inc', 'techstart', 'Innovative technology startup', 'founder@techstart.com', 'Sarah', 'Johnson', '+1-555-0102', 'San Francisco', 'USA', 'TRIAL', CURRENT_TIMESTAMP + INTERVAL '14 days', 25, 250),
('global-retail', 'Global Retail Solutions', 'retail', 'International retail and e-commerce platform', 'it@globalretail.com', 'Michael', 'Brown', '+44-20-7946-0958', 'London', 'UK', 'ACTIVE', NULL, 100, 1000),
('healthcare-plus', 'HealthCare Plus', 'healthcare', 'Digital healthcare solutions provider', 'admin@healthcareplus.com', 'Dr. Emily', 'Davis', '+1-555-0104', 'Boston', 'USA', 'ACTIVE', NULL, 75, 750),
('edu-platform', 'EduPlatform', 'eduplatform', 'Online education and learning management', 'admin@eduplatform.com', 'Robert', 'Wilson', '+1-555-0105', 'Austin', 'USA', 'TRIAL', CURRENT_TIMESTAMP + INTERVAL '7 days', 30, 300);

-- Insert sample users with hashed passwords (password is 'password123' for all users)
-- Note: In production, these would be properly hashed using BCrypt
INSERT INTO users (tenant_id, username, email, password, first_name, last_name, phone, status, enabled, email_verified, login_count, last_login) VALUES
-- ACME Corporation users
('acme-corp', 'john.smith', 'john.smith@acme-corp.com', 'Abc123@', 'John', 'Smith', '+1-555-0201', 'ACTIVE', true, true, 45, CURRENT_TIMESTAMP - INTERVAL '2 hours'),
('acme-corp', 'jane.doe', 'jane.doe@acme-corp.com', 'Abc123@', 'Jane', 'Doe', '+1-555-0202', 'ACTIVE', true, true, 23, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('acme-corp', 'bob.manager', 'bob.manager@acme-corp.com', 'Abc123@', 'Bob', 'Manager', '+1-555-0203', 'ACTIVE', true, true, 67, CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
('acme-corp', 'alice.analyst', 'alice.analyst@acme-corp.com', 'Abc123@', 'Alice', 'Analyst', '+1-555-0204', 'ACTIVE', true, true, 12, CURRENT_TIMESTAMP - INTERVAL '3 hours'),

-- TechStart Inc users
('techstart-inc', 'sarah.johnson', 'sarah.johnson@techstart.com', 'Abc123@', 'Sarah', 'Johnson', '+1-555-0301', 'ACTIVE', true, true, 89, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('techstart-inc', 'dev.engineer', 'dev@techstart.com', 'Abc123@', 'Dev', 'Engineer', '+1-555-0302', 'ACTIVE', true, true, 156, CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
('techstart-inc', 'product.owner', 'product@techstart.com', 'Abc123@', 'Product', 'Owner', '+1-555-0303', 'ACTIVE', true, true, 34, CURRENT_TIMESTAMP - INTERVAL '2 days'),

-- Global Retail users
('global-retail', 'michael.brown', 'michael.brown@globalretail.com', 'Abc123@', 'Michael', 'Brown', '+44-20-7946-0958', 'ACTIVE', true, true, 78, CURRENT_TIMESTAMP - INTERVAL '4 hours'),
('global-retail', 'emma.sales', 'emma.sales@globalretail.com', 'Abc123@', 'Emma', 'Sales', '+44-20-7946-0959', 'ACTIVE', true, true, 92, CURRENT_TIMESTAMP - INTERVAL '6 hours'),
('global-retail', 'david.ops', 'david.ops@globalretail.com', 'Abc123@', 'David', 'Ops', '+44-20-7946-0960', 'ACTIVE', true, true, 134, CURRENT_TIMESTAMP - INTERVAL '8 hours'),

-- HealthCare Plus users
('healthcare-plus', 'emily.davis', 'emily.davis@healthcareplus.com', 'Abc123@', 'Emily', 'Davis', '+1-555-0401', 'ACTIVE', true, true, 56, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('healthcare-plus', 'nurse.practitioner', 'nurse@healthcareplus.com', 'Abc123@', 'Nancy', 'Practitioner', '+1-555-0402', 'ACTIVE', true, true, 201, CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
('healthcare-plus', 'admin.staff', 'admin@healthcareplus.com', 'Abc123@', 'Admin', 'Staff', '+1-555-0403', 'ACTIVE', true, true, 87, CURRENT_TIMESTAMP - INTERVAL '2 hours'),

-- EduPlatform users
('eduplatform', 'robert.wilson', 'robert.wilson@eduplatform.com', 'Abc123@', 'Robert', 'Wilson', '+1-555-0501', 'ACTIVE', true, true, 43, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('eduplatform', 'teacher.mary', 'mary@eduplatform.com', 'Abc123@', 'Mary', 'Teacher', '+1-555-0502', 'ACTIVE', true, true, 167, CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
('eduplatform', 'student.support', 'support@eduplatform.com', 'Abc123@', 'Support', 'Staff', '+1-555-0503', 'ACTIVE', true, true, 29, CURRENT_TIMESTAMP - INTERVAL '5 hours');

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
-- ACME Corp roles
(1, 'TENANT_ADMIN'), (1, 'USER'),
(2, 'USER'),
(3, 'USER'),
(4, 'USER'),

-- TechStart roles
(5, 'TENANT_ADMIN'), (5, 'USER'),
(6, 'USER'),
(7, 'USER'),

-- Global Retail roles
(8, 'TENANT_ADMIN'), (8, 'USER'),
(9, 'USER'),
(10, 'USER'),

-- HealthCare Plus roles
(11, 'TENANT_ADMIN'), (11, 'USER'),
(12, 'USER'),
(13, 'USER'),

-- EduPlatform roles
(14, 'TENANT_ADMIN'), (14, 'USER'),
(15, 'USER'),
(16, 'USER');

-- Create a system admin user that can access all tenants
INSERT INTO users (tenant_id, username, email, password, first_name, last_name, status, enabled, email_verified, login_count) VALUES
('system', 'admin', 'admin@system.com', 'Abc123@', 'System', 'Administrator', 'ACTIVE', true, true, 5);

INSERT INTO user_roles (user_id, role) VALUES
(17, 'ADMIN'), (17, 'TENANT_ADMIN'), (17, 'USER');

-- Insert subscription plans
INSERT INTO subscription_plans (name, description, price, billing_period, max_users, storage_limit_gb, api_rate_limit, features) VALUES
('Starter', 'Perfect for small teams getting started', 29.99, 'MONTHLY', 5, 10, 1000, ARRAY['Basic Support', 'Core Features', 'API Access']),
('Professional', 'Ideal for growing businesses', 99.99, 'MONTHLY', 25, 100, 5000, ARRAY['Priority Support', 'Advanced Features', 'API Access', 'Analytics', 'Custom Integrations']),
('Enterprise', 'For large organizations with advanced needs', 299.99, 'MONTHLY', 100, 1000, 25000, ARRAY['24/7 Support', 'All Features', 'Unlimited API', 'Advanced Analytics', 'Custom Integrations', 'SSO', 'Compliance']);

-- Insert tenant subscriptions
INSERT INTO tenant_subscriptions (tenant_id, plan_id, status, billing_email, payment_method) VALUES
('acme-corp', 3, 'ACTIVE', 'billing@acme-corp.com', 'CREDIT_CARD'),
('techstart-inc', 1, 'TRIAL', 'billing@techstart.com', 'CREDIT_CARD'),
('global-retail', 3, 'ACTIVE', 'finance@globalretail.com', 'BANK_TRANSFER'),
('healthcare-plus', 2, 'ACTIVE', 'billing@healthcareplus.com', 'CREDIT_CARD'),
('edu-platform', 2, 'TRIAL', 'admin@eduplatform.com', 'CREDIT_CARD');

-- Insert sample projects
INSERT INTO projects (tenant_id, name, description, owner_id, start_date, end_date, budget, priority, completion_percentage) VALUES
('acme-corp', 'Digital Transformation', 'Company-wide digital transformation initiative', 1, '2024-01-15', '2024-12-31', 500000.00, 'HIGH', 65),
('acme-corp', 'Supply Chain Optimization', 'Optimize global supply chain operations', 3, '2024-03-01', '2024-08-30', 250000.00, 'MEDIUM', 40),
('techstart-inc', 'Product Launch v2.0', 'Next generation product development', 5, '2024-02-01', '2024-06-30', 150000.00, 'HIGH', 80),
('global-retail', 'E-commerce Platform Upgrade', 'Modernize online shopping experience', 8, '2024-01-01', '2024-09-30', 750000.00, 'HIGH', 55),
('healthcare-plus', 'Patient Portal Enhancement', 'Improve patient engagement platform', 11, '2024-02-15', '2024-07-15', 200000.00, 'MEDIUM', 70),
('edu-platform', 'Mobile Learning App', 'Develop mobile application for students', 14, '2024-03-01', '2024-10-31', 180000.00, 'HIGH', 45);

-- Insert project members
INSERT INTO project_members (project_id, user_id, role) VALUES
(1, 1, 'OWNER'), (1, 2, 'MEMBER'), (1, 3, 'MANAGER'), (1, 4, 'MEMBER'),
(2, 3, 'OWNER'), (2, 1, 'MEMBER'), (2, 2, 'MEMBER'),
(3, 5, 'OWNER'), (3, 6, 'MEMBER'), (3, 7, 'MANAGER'),
(4, 8, 'OWNER'), (4, 9, 'MEMBER'), (4, 10, 'MEMBER'),
(5, 11, 'OWNER'), (5, 12, 'MEMBER'), (5, 13, 'MEMBER'),
(6, 14, 'OWNER'), (6, 15, 'MEMBER'), (6, 16, 'MEMBER');

-- Insert sample invoices
INSERT INTO invoices (tenant_id, subscription_id, invoice_number, amount, status, issue_date, due_date, paid_date, payment_method) VALUES
('acme-corp', 1, 'INV-2024-001', 299.99, 'PAID', '2024-01-01', '2024-01-31', '2024-01-15 10:30:00', 'CREDIT_CARD'),
('acme-corp', 1, 'INV-2024-002', 299.99, 'PAID', '2024-02-01', '2024-02-29', '2024-02-10 14:20:00', 'CREDIT_CARD'),
('global-retail', 3, 'INV-2024-003', 299.99, 'PAID', '2024-01-01', '2024-01-31', '2024-01-25 09:15:00', 'BANK_TRANSFER'),
('healthcare-plus', 4, 'INV-2024-004', 99.99, 'PAID', '2024-01-01', '2024-01-31', '2024-01-20 16:45:00', 'CREDIT_CARD'),
('techstart-inc', 2, 'INV-2024-005', 29.99, 'PENDING', '2024-03-01', '2024-03-31', NULL, 'CREDIT_CARD');

-- Insert sample notifications
INSERT INTO notifications (tenant_id, user_id, title, message, type, priority) VALUES
('acme-corp', 1, 'Project Update', 'Digital Transformation project has reached 65% completion', 'PROJECT', 'NORMAL'),
('acme-corp', 2, 'Welcome!', 'Welcome to the ACME Corporation platform', 'SYSTEM', 'LOW'),
('techstart-inc', 5, 'Trial Reminder', 'Your trial period expires in 7 days', 'BILLING', 'HIGH'),
('global-retail', 8, 'Invoice Generated', 'Your monthly invoice is ready for review', 'BILLING', 'NORMAL'),
('healthcare-plus', 11, 'Security Alert', 'New login detected from unusual location', 'SECURITY', 'HIGH'),
('edu-platform', 14, 'Project Milestone', 'Mobile Learning App project reached important milestone', 'PROJECT', 'NORMAL');

-- Insert sample audit logs
INSERT INTO audit_logs (tenant_id, user_id, entity_type, entity_id, action, old_values, new_values, ip_address) VALUES
('acme-corp', 1, 'USER', '2', 'UPDATE', '{"status": "INACTIVE"}', '{"status": "ACTIVE"}', '192.168.1.100'),
('acme-corp', 3, 'PROJECT', '1', 'UPDATE', '{"completion_percentage": 60}', '{"completion_percentage": 65}', '192.168.1.101'),
('techstart-inc', 5, 'USER', '6', 'CREATE', '{}', '{"username": "dev.engineer", "email": "dev@techstart.com"}', '10.0.0.50'),
('global-retail', 8, 'TENANT', 'global-retail', 'UPDATE', '{"max_users": 90}', '{"max_users": 100}', '172.16.0.10'),
('healthcare-plus', 11, 'PROJECT', '5', 'CREATE', '{}', '{"name": "Patient Portal Enhancement", "status": "ACTIVE"}', '192.168.2.200');

-- Insert sample user activities
INSERT INTO user_activities (tenant_id, user_id, activity_type, description, ip_address) VALUES
('acme-corp', 1, 'LOGIN', 'User logged in successfully', '192.168.1.100'),
('acme-corp', 2, 'VIEW_PROJECT', 'Viewed project: Digital Transformation', '192.168.1.102'),
('techstart-inc', 5, 'CREATE_PROJECT', 'Created new project: Product Launch v2.0', '10.0.0.50'),
('global-retail', 8, 'DOWNLOAD_REPORT', 'Downloaded monthly usage report', '172.16.0.10'),
('healthcare-plus', 11, 'UPDATE_PROFILE', 'Updated user profile information', '192.168.2.200'),
('edu-platform', 14, 'INVITE_USER', 'Invited new team member to project', '203.0.113.15');

-- Insert sample system events
INSERT INTO system_events (event_type, severity, message, tenant_id, user_id) VALUES
('USER_LOGIN', 'INFO', 'Successful user authentication', 'acme-corp', 1),
('PAYMENT_PROCESSED', 'INFO', 'Monthly subscription payment processed', 'acme-corp', NULL),
('SECURITY_ALERT', 'WARN', 'Multiple failed login attempts detected', 'techstart-inc', 5),
('SYSTEM_MAINTENANCE', 'INFO', 'Scheduled system maintenance completed', NULL, NULL),
('API_RATE_LIMIT', 'WARN', 'API rate limit threshold reached', 'global-retail', 9);

-- Insert sample API usage
INSERT INTO api_usage (tenant_id, user_id, endpoint, method, status_code, response_time_ms, ip_address) VALUES
('acme-corp', 1, '/api/users', 'GET', 200, 145, '192.168.1.100'),
('acme-corp', 2, '/api/projects', 'GET', 200, 89, '192.168.1.102'),
('techstart-inc', 5, '/api/projects', 'POST', 201, 234, '10.0.0.50'),
('global-retail', 8, '/api/reports/usage', 'GET', 200, 567, '172.16.0.10'),
('healthcare-plus', 11, '/api/users/profile', 'PUT', 200, 123, '192.168.2.200'),
('edu-platform', 14, '/api/projects/6/members', 'POST', 201, 189, '203.0.113.15');

-- Insert sample usage metrics
INSERT INTO usage_metrics (tenant_id, metric_name, metric_value, metric_unit, period_start, period_end) VALUES
('acme-corp', 'API_CALLS', 15420.00, 'COUNT', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
('acme-corp', 'STORAGE_USED', 234.50, 'GB', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
('techstart-inc', 'API_CALLS', 3240.00, 'COUNT', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
('global-retail', 'API_CALLS', 45600.00, 'COUNT', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
('healthcare-plus', 'STORAGE_USED', 67.80, 'GB', '2024-03-01 00:00:00', '2024-03-31 23:59:59'),
('edu-platform', 'ACTIVE_USERS', 28.00, 'COUNT', '2024-03-01 00:00:00', '2024-03-31 23:59:59');

-- Insert sample file uploads
INSERT INTO file_uploads (tenant_id, user_id, original_filename, stored_filename, file_path, file_size, content_type) VALUES
('acme-corp', 1, 'project-requirements.pdf', 'acme_proj_req_2024_001.pdf', '/uploads/acme-corp/documents/', 2547830, 'application/pdf'),
('acme-corp', 3, 'budget-analysis.xlsx', 'acme_budget_2024_002.xlsx', '/uploads/acme-corp/spreadsheets/', 1045672, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'),
('techstart-inc', 5, 'product-mockup.png', 'techstart_mockup_2024_001.png', '/uploads/techstart-inc/images/', 3247891, 'image/png'),
('global-retail', 8, 'sales-report.pdf', 'retail_sales_2024_001.pdf', '/uploads/global-retail/reports/', 1876543, 'application/pdf'),
('healthcare-plus', 11, 'patient-flow-diagram.svg', 'health_flow_2024_001.svg', '/uploads/healthcare-plus/diagrams/', 156789, 'image/svg+xml');

-- Insert sample tenant settings
INSERT INTO tenant_settings (tenant_id, setting_key, setting_value, setting_type, description) VALUES
('acme-corp', 'THEME_COLOR', '#1a365d', 'STRING', 'Primary theme color for the interface'),
('acme-corp', 'MAX_FILE_SIZE_MB', '50', 'INTEGER', 'Maximum file upload size in megabytes'),
('acme-corp', 'ENABLE_2FA', 'true', 'BOOLEAN', 'Enable two-factor authentication'),
('techstart-inc', 'THEME_COLOR', '#2d3748', 'STRING', 'Primary theme color for the interface'),
('techstart-inc', 'NOTIFICATION_EMAIL', 'notifications@techstart.com', 'STRING', 'Email for system notifications'),
('global-retail', 'CURRENCY_DEFAULT', 'GBP', 'STRING', 'Default currency for financial operations'),
('healthcare-plus', 'ENABLE_AUDIT_LOG', 'true', 'BOOLEAN', 'Enable comprehensive audit logging'),
('edu-platform', 'SESSION_TIMEOUT_MINUTES', '60', 'INTEGER', 'User session timeout in minutes');
