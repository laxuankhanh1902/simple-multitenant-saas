package io.conduktor.saas.kafka.audit.entity;

import io.conduktor.saas.core.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "kafka_audit_logs", indexes = {
    @Index(name = "idx_kafka_audit_tenant_timestamp", columnList = "tenantId, timestamp"),
    @Index(name = "idx_kafka_audit_user_action", columnList = "userId, action"),
    @Index(name = "idx_kafka_audit_cluster_topic", columnList = "clusterId, topicName"),
    @Index(name = "idx_kafka_audit_timestamp", columnList = "timestamp")
})
public class KafkaAuditLog extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "resource_name", length = 200)
    private String resourceName;

    @Column(name = "cluster_id")
    private Long clusterId;

    @Column(name = "cluster_name", length = 100)
    private String clusterName;

    @Column(name = "topic_name", length = 200)
    private String topicName;

    @Column(name = "consumer_group", length = 200)
    private String consumerGroup;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "TEXT")
    private Map<String, Object> details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "status", length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private Map<String, Object> metadata;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "request_id", length = 255)
    private String requestId;

    public KafkaAuditLog() {
        this.timestamp = LocalDateTime.now();
        this.status = "SUCCESS";
    }

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}