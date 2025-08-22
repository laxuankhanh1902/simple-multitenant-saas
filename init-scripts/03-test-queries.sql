-- Test queries to verify data insertion

-- Check tenants
SELECT 
    name, 
    subdomain, 
    status, 
    admin_email, 
    max_users,
    CASE 
        WHEN trial_end_date IS NOT NULL THEN 'Trial until ' || trial_end_date::date 
        ELSE 'Full Account' 
    END as account_type
FROM tenants 
ORDER BY created_at;

-- Check users per tenant
SELECT 
    t.name as tenant_name,
    COUNT(u.id) as user_count,
    COUNT(CASE WHEN u.enabled = true THEN 1 END) as active_users
FROM tenants t
LEFT JOIN users u ON t.tenant_id = u.tenant_id
GROUP BY t.name, t.tenant_id
ORDER BY t.name;

-- Check user roles
SELECT 
    u.username,
    u.email,
    t.name as tenant_name,
    STRING_AGG(ur.role, ', ') as roles
FROM users u
JOIN tenants t ON u.tenant_id = t.tenant_id
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.tenant_id != 'system'
GROUP BY u.username, u.email, t.name
ORDER BY t.name, u.username;

-- System admin
SELECT 
    username,
    email,
    STRING_AGG(ur.role, ', ') as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.tenant_id = 'system'
GROUP BY username, email;

-- Check subscription plans and tenant subscriptions
SELECT 
    sp.name as plan_name,
    sp.price,
    sp.billing_period,
    COUNT(ts.id) as active_subscriptions
FROM subscription_plans sp
LEFT JOIN tenant_subscriptions ts ON sp.id = ts.plan_id AND ts.status = 'ACTIVE'
GROUP BY sp.id, sp.name, sp.price, sp.billing_period
ORDER BY sp.price;

-- Check projects by tenant
SELECT 
    t.name as tenant_name,
    p.name as project_name,
    p.status,
    p.completion_percentage,
    u.username as owner,
    COUNT(pm.user_id) as team_members
FROM tenants t
JOIN projects p ON t.tenant_id = p.tenant_id
JOIN users u ON p.owner_id = u.id
LEFT JOIN project_members pm ON p.id = pm.project_id
GROUP BY t.name, p.name, p.status, p.completion_percentage, u.username
ORDER BY t.name, p.name;

-- Check billing status
SELECT 
    t.name as tenant_name,
    sp.name as plan_name,
    ts.status as subscription_status,
    COUNT(i.id) as total_invoices,
    COUNT(CASE WHEN i.status = 'PAID' THEN 1 END) as paid_invoices,
    COUNT(CASE WHEN i.status = 'PENDING' THEN 1 END) as pending_invoices
FROM tenants t
JOIN tenant_subscriptions ts ON t.tenant_id = ts.tenant_id
JOIN subscription_plans sp ON ts.plan_id = sp.id
LEFT JOIN invoices i ON ts.id = i.subscription_id
GROUP BY t.name, sp.name, ts.status
ORDER BY t.name;

-- Check recent audit activity
SELECT 
    t.name as tenant_name,
    u.username,
    al.entity_type,
    al.action,
    al.created_at
FROM audit_logs al
JOIN tenants t ON al.tenant_id = t.tenant_id
LEFT JOIN users u ON al.user_id = u.id
ORDER BY al.created_at DESC
LIMIT 10;

-- Check notification summary
SELECT 
    t.name as tenant_name,
    COUNT(n.id) as total_notifications,
    COUNT(CASE WHEN n.is_read = false THEN 1 END) as unread_notifications,
    COUNT(CASE WHEN n.priority = 'HIGH' THEN 1 END) as high_priority
FROM tenants t
LEFT JOIN notifications n ON t.tenant_id = n.tenant_id
GROUP BY t.name
ORDER BY t.name;

-- Check API usage statistics
SELECT 
    t.name as tenant_name,
    COUNT(au.id) as total_api_calls,
    AVG(au.response_time_ms) as avg_response_time,
    COUNT(CASE WHEN au.status_code >= 400 THEN 1 END) as error_calls
FROM tenants t
LEFT JOIN api_usage au ON t.tenant_id = au.tenant_id
GROUP BY t.name
ORDER BY total_api_calls DESC;

-- Check usage metrics summary
SELECT 
    t.name as tenant_name,
    um.metric_name,
    um.metric_value,
    um.metric_unit
FROM tenants t
JOIN usage_metrics um ON t.tenant_id = um.tenant_id
ORDER BY t.name, um.metric_name;

-- Check file upload summary
SELECT 
    t.name as tenant_name,
    COUNT(fu.id) as total_files,
    ROUND(SUM(fu.file_size)::numeric / (1024*1024), 2) as total_size_mb,
    COUNT(CASE WHEN fu.is_public = true THEN 1 END) as public_files
FROM tenants t
LEFT JOIN file_uploads fu ON t.tenant_id = fu.tenant_id
GROUP BY t.name
ORDER BY total_size_mb DESC;

-- Check tenant settings
SELECT 
    t.name as tenant_name,
    COUNT(ts.id) as settings_count,
    COUNT(CASE WHEN ts.is_encrypted = true THEN 1 END) as encrypted_settings
FROM tenants t
LEFT JOIN tenant_settings ts ON t.tenant_id = ts.tenant_id
GROUP BY t.name
ORDER BY t.name;

-- Table counts summary
SELECT 'tenants' as table_name, COUNT(*) as record_count FROM tenants
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'projects', COUNT(*) FROM projects
UNION ALL
SELECT 'subscription_plans', COUNT(*) FROM subscription_plans
UNION ALL
SELECT 'tenant_subscriptions', COUNT(*) FROM tenant_subscriptions
UNION ALL
SELECT 'invoices', COUNT(*) FROM invoices
UNION ALL
SELECT 'audit_logs', COUNT(*) FROM audit_logs
UNION ALL
SELECT 'notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'api_usage', COUNT(*) FROM api_usage
UNION ALL
SELECT 'usage_metrics', COUNT(*) FROM usage_metrics
UNION ALL
SELECT 'file_uploads', COUNT(*) FROM file_uploads
UNION ALL
SELECT 'tenant_settings', COUNT(*) FROM tenant_settings
ORDER BY record_count DESC;
