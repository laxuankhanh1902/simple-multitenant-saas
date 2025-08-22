package io.conduktor.saas.kafka.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Kafka audit log data transfer object")
public class KafkaAuditLogDto {

    @Schema(description = "Audit log ID", example = "1")
    private Long id;

    @Schema(description = "Tenant ID", example = "acme-corp")
    private String tenantId;

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "User email", example = "john.doe@acme-corp.com")
    private String userEmail;

    @Schema(description = "Action performed", example = "CREATE_TOPIC")
    private String action;

    @Schema(description = "Resource type", example = "TOPIC")
    private String resourceType;

    @Schema(description = "Resource name", example = "user-events")
    private String resourceName;

    @Schema(description = "Cluster ID", example = "1")
    private Long clusterId;

    @Schema(description = "Cluster name", example = "production-cluster")
    private String clusterName;

    @Schema(description = "Topic name", example = "user-events")
    private String topicName;

    @Schema(description = "Consumer group", example = "analytics-consumer-group")
    private String consumerGroup;

    @Schema(description = "Additional details")
    private Map<String, Object> details;

    @Schema(description = "IP address", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "User agent")
    private String userAgent;

    @Schema(description = "Timestamp", example = "2024-03-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Duration in milliseconds", example = "150")
    private Integer durationMs;

    @Schema(description = "Status", example = "SUCCESS")
    private String status;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

    @Schema(description = "Session ID")
    private String sessionId;

    @Schema(description = "Request ID")
    private String requestId;

    // Constructors
    public KafkaAuditLogDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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