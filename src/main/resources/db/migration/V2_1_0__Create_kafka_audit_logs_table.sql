-- Create Kafka audit logs table
CREATE TABLE IF NOT EXISTS kafka_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id BIGINT,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_name VARCHAR(200),
    cluster_id BIGINT,
    cluster_name VARCHAR(100),
    topic_name VARCHAR(200),
    consumer_group VARCHAR(200),
    details JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_ms INTEGER,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    metadata JSONB,
    session_id VARCHAR(255),
    request_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_kafka_audit_tenant_timestamp ON kafka_audit_logs(tenant_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_kafka_audit_user_action ON kafka_audit_logs(user_id, action);
CREATE INDEX IF NOT EXISTS idx_kafka_audit_cluster_topic ON kafka_audit_logs(cluster_id, topic_name);
CREATE INDEX IF NOT EXISTS idx_kafka_audit_timestamp ON kafka_audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_kafka_audit_action ON kafka_audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_kafka_audit_resource_type ON kafka_audit_logs(resource_type);