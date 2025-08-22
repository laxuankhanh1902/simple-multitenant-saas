-- Create Kafka metrics table
CREATE TABLE IF NOT EXISTS kafka_metrics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    cluster_id BIGINT,
    topic_name VARCHAR(200),
    consumer_group VARCHAR(200),
    metric_type VARCHAR(50) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(20,6) NOT NULL,
    metric_unit VARCHAR(20),
    labels JSONB,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cluster_id) REFERENCES kafka_clusters(id) ON DELETE CASCADE
);

-- Create indexes for performance and time-series queries
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_tenant_time ON kafka_metrics(tenant_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_cluster_time ON kafka_metrics(cluster_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_topic_time ON kafka_metrics(topic_name, timestamp);
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_type_name ON kafka_metrics(metric_type, metric_name);
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_timestamp ON kafka_metrics(timestamp);

-- Create a partial index for recent metrics (last 30 days) for better performance
CREATE INDEX IF NOT EXISTS idx_kafka_metrics_recent ON kafka_metrics(tenant_id, cluster_id, metric_type, timestamp) 
WHERE timestamp > CURRENT_TIMESTAMP - INTERVAL '30 days';