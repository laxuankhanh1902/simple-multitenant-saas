-- Create Kafka topics table
CREATE TABLE IF NOT EXISTS kafka_topics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    cluster_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    partitions INTEGER NOT NULL,
    replication_factor SMALLINT NOT NULL,
    configuration JSONB,
    size_bytes BIGINT,
    message_count BIGINT,
    retention_ms BIGINT,
    cleanup_policy VARCHAR(50),
    segment_ms BIGINT,
    min_insync_replicas INTEGER,
    compression_type VARCHAR(20),
    partition_info JSONB,
    metrics JSONB,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    is_internal BOOLEAN DEFAULT false,
    is_compacted BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version BIGINT DEFAULT 1,
    UNIQUE(tenant_id, cluster_id, name),
    FOREIGN KEY (cluster_id) REFERENCES kafka_clusters(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_kafka_topic_tenant_cluster ON kafka_topics(tenant_id, cluster_id);
CREATE INDEX IF NOT EXISTS idx_kafka_topic_name ON kafka_topics(name);
CREATE INDEX IF NOT EXISTS idx_kafka_topic_cluster_name ON kafka_topics(cluster_id, name);
CREATE INDEX IF NOT EXISTS idx_kafka_topic_status ON kafka_topics(status);