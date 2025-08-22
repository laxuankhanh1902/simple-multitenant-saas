-- Create Kafka clusters table
CREATE TABLE IF NOT EXISTS kafka_clusters (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    bootstrap_servers TEXT NOT NULL,
    connection_type VARCHAR(20) DEFAULT 'PLAINTEXT',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    health_status VARCHAR(20) DEFAULT 'UNKNOWN',
    kafka_version VARCHAR(50),
    security_config JSONB,
    connection_config JSONB,
    last_health_check TIMESTAMP,
    health_check_error TEXT,
    broker_count INTEGER,
    topic_count INTEGER,
    partition_count INTEGER,
    consumer_group_count INTEGER,
    metrics JSONB,
    monitoring_config JSONB,
    auto_health_check BOOLEAN DEFAULT true,
    health_check_interval_minutes INTEGER DEFAULT 5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version_num BIGINT DEFAULT 1,
    UNIQUE(tenant_id, name)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_kafka_cluster_tenant_name ON kafka_clusters(tenant_id, name);
CREATE INDEX IF NOT EXISTS idx_kafka_cluster_status ON kafka_clusters(status);
CREATE INDEX IF NOT EXISTS idx_kafka_cluster_health_status ON kafka_clusters(health_status);
CREATE INDEX IF NOT EXISTS idx_kafka_cluster_tenant ON kafka_clusters(tenant_id);