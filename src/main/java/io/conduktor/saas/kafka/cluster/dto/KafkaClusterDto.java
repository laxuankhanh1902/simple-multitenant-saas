package io.conduktor.saas.kafka.cluster.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Kafka cluster data transfer object")
public class KafkaClusterDto {

    @Schema(description = "Cluster ID", example = "1")
    private Long id;

    @Schema(description = "Tenant ID", example = "acme-corp")
    private String tenantId;

    @Schema(description = "Cluster name", example = "production-cluster")
    private String name;

    @Schema(description = "Cluster description", example = "Production Kafka cluster for event streaming")
    private String description;

    @Schema(description = "Bootstrap servers", example = "kafka1:9092,kafka2:9092,kafka3:9092")
    private String bootstrapServers;

    @Schema(description = "Connection type", example = "SASL_SSL")
    private String connectionType;

    @Schema(description = "Cluster status", example = "ACTIVE")
    private String status;

    @Schema(description = "Health status", example = "HEALTHY")
    private String healthStatus;

    @Schema(description = "Kafka version", example = "3.5.0")
    private String kafkaVersion;

    @Schema(description = "Security configuration")
    private Map<String, Object> securityConfig;

    @Schema(description = "Connection configuration")
    private Map<String, Object> connectionConfig;

    @Schema(description = "Last health check timestamp", example = "2024-03-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastHealthCheck;

    @Schema(description = "Health check error message")
    private String healthCheckError;

    @Schema(description = "Number of brokers", example = "3")
    private Integer brokerCount;

    @Schema(description = "Number of topics", example = "25")
    private Integer topicCount;

    @Schema(description = "Total number of partitions", example = "150")
    private Integer partitionCount;

    @Schema(description = "Number of consumer groups", example = "8")
    private Integer consumerGroupCount;

    @Schema(description = "Cluster metrics")
    private Map<String, Object> metrics;

    @Schema(description = "Monitoring configuration")
    private Map<String, Object> monitoringConfig;

    @Schema(description = "Auto health check enabled", example = "true")
    private Boolean autoHealthCheck;

    @Schema(description = "Health check interval in minutes", example = "5")
    private Integer healthCheckIntervalMinutes;

    @Schema(description = "Created timestamp", example = "2024-03-01T09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Updated timestamp", example = "2024-03-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public KafkaClusterDto() {}

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

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(String kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public Map<String, Object> getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(Map<String, Object> securityConfig) {
        this.securityConfig = securityConfig;
    }

    public Map<String, Object> getConnectionConfig() {
        return connectionConfig;
    }

    public void setConnectionConfig(Map<String, Object> connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public LocalDateTime getLastHealthCheck() {
        return lastHealthCheck;
    }

    public void setLastHealthCheck(LocalDateTime lastHealthCheck) {
        this.lastHealthCheck = lastHealthCheck;
    }

    public String getHealthCheckError() {
        return healthCheckError;
    }

    public void setHealthCheckError(String healthCheckError) {
        this.healthCheckError = healthCheckError;
    }

    public Integer getBrokerCount() {
        return brokerCount;
    }

    public void setBrokerCount(Integer brokerCount) {
        this.brokerCount = brokerCount;
    }

    public Integer getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Integer getConsumerGroupCount() {
        return consumerGroupCount;
    }

    public void setConsumerGroupCount(Integer consumerGroupCount) {
        this.consumerGroupCount = consumerGroupCount;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getMonitoringConfig() {
        return monitoringConfig;
    }

    public void setMonitoringConfig(Map<String, Object> monitoringConfig) {
        this.monitoringConfig = monitoringConfig;
    }

    public Boolean getAutoHealthCheck() {
        return autoHealthCheck;
    }

    public void setAutoHealthCheck(Boolean autoHealthCheck) {
        this.autoHealthCheck = autoHealthCheck;
    }

    public Integer getHealthCheckIntervalMinutes() {
        return healthCheckIntervalMinutes;
    }

    public void setHealthCheckIntervalMinutes(Integer healthCheckIntervalMinutes) {
        this.healthCheckIntervalMinutes = healthCheckIntervalMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}