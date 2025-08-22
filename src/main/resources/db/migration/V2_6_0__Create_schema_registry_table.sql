-- Create Schema Registry table
CREATE TABLE IF NOT EXISTS schema_registry (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    cluster_id BIGINT,
    subject VARCHAR(200) NOT NULL,
    version INTEGER NOT NULL,
    schema_id INTEGER,
    schema_content TEXT NOT NULL,
    schema_type VARCHAR(20) DEFAULT 'AVRO',
    compatibility_level VARCHAR(20) DEFAULT 'BACKWARD',
    is_deleted BOOLEAN DEFAULT false,
    references JSONB,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    version_num BIGINT DEFAULT 1,
    UNIQUE(tenant_id, subject, version),
    FOREIGN KEY (cluster_id) REFERENCES kafka_clusters(id) ON DELETE SET NULL
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_schema_registry_tenant_subject ON schema_registry(tenant_id, subject);
CREATE INDEX IF NOT EXISTS idx_schema_registry_cluster ON schema_registry(cluster_id);
CREATE INDEX IF NOT EXISTS idx_schema_registry_subject_version ON schema_registry(subject, version);
CREATE INDEX IF NOT EXISTS idx_schema_registry_schema_id ON schema_registry(schema_id);
CREATE INDEX IF NOT EXISTS idx_schema_registry_type ON schema_registry(schema_type);