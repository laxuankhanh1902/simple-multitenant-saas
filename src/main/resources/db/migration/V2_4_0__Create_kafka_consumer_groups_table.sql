-- Create Kafka consumer groups table
CREATE TABLE IF NOT EXISTS kafka_consumer_groups (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    cluster_id BIGINT NOT NULL,
    group_id VARCHAR(200) NOT NULL,
    state VARCHAR(20),
    protocol VARCHAR(50),
    protocol_type VARCHAR(50),
    member_count INTEGER DEFAULT 0,
    lag_total BIGINT DEFAULT 0,
    assignment_strategy VARCHAR(100),
    coordinator_id INTEGER,
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1,
    UNIQUE(tenant_id, cluster_id, group_id),
    FOREIGN KEY (cluster_id) REFERENCES kafka_clusters(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_kafka_consumer_group_tenant_cluster ON kafka_consumer_groups(tenant_id, cluster_id);
CREATE INDEX IF NOT EXISTS idx_kafka_consumer_group_id ON kafka_consumer_groups(group_id);
CREATE INDEX IF NOT EXISTS idx_kafka_consumer_group_state ON kafka_consumer_groups(state);
CREATE INDEX IF NOT EXISTS idx_kafka_consumer_group_lag ON kafka_consumer_groups(lag_total);