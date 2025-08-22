package io.conduktor.saas.kafka.topic.entity;

import io.conduktor.saas.core.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "kafka_topics", indexes = {
    @Index(name = "idx_kafka_topic_tenant_cluster", columnList = "tenantId, clusterId"),
    @Index(name = "idx_kafka_topic_name", columnList = "name"),
    @Index(name = "idx_kafka_topic_cluster_name", columnList = "clusterId, name")
})
public class KafkaTopic extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "cluster_id", nullable = false)
    private Long clusterId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "partitions", nullable = false)
    private Integer partitions;

    @Column(name = "replication_factor", nullable = false)
    private Short replicationFactor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration", columnDefinition = "TEXT")
    private Map<String, Object> configuration;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "message_count")
    private Long messageCount;

    @Column(name = "retention_ms")
    private Long retentionMs;

    @Column(name = "cleanup_policy", length = 50)
    private String cleanupPolicy;

    @Column(name = "segment_ms")
    private Long segmentMs;

    @Column(name = "min_insync_replicas")
    private Integer minInsyncReplicas;

    @Column(name = "compression_type", length = 20)
    private String compressionType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "partition_info", columnDefinition = "TEXT")
    private Map<String, Object> partitionInfo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics", columnDefinition = "TEXT")
    private Map<String, Object> metrics;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "is_compacted")
    private Boolean isCompacted = false;

    public KafkaTopic() {}

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }

    public Short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(Short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public Long getRetentionMs() {
        return retentionMs;
    }

    public void setRetentionMs(Long retentionMs) {
        this.retentionMs = retentionMs;
    }

    public String getCleanupPolicy() {
        return cleanupPolicy;
    }

    public void setCleanupPolicy(String cleanupPolicy) {
        this.cleanupPolicy = cleanupPolicy;
    }

    public Long getSegmentMs() {
        return segmentMs;
    }

    public void setSegmentMs(Long segmentMs) {
        this.segmentMs = segmentMs;
    }

    public Integer getMinInsyncReplicas() {
        return minInsyncReplicas;
    }

    public void setMinInsyncReplicas(Integer minInsyncReplicas) {
        this.minInsyncReplicas = minInsyncReplicas;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public Map<String, Object> getPartitionInfo() {
        return partitionInfo;
    }

    public void setPartitionInfo(Map<String, Object> partitionInfo) {
        this.partitionInfo = partitionInfo;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(Boolean isInternal) {
        this.isInternal = isInternal;
    }

    public Boolean getIsCompacted() {
        return isCompacted;
    }

    public void setIsCompacted(Boolean isCompacted) {
        this.isCompacted = isCompacted;
    }
}