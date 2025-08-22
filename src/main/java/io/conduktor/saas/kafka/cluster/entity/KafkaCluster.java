package io.conduktor.saas.kafka.cluster.entity;

import io.conduktor.saas.core.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "kafka_clusters", indexes = {
    @Index(name = "idx_kafka_cluster_tenant_name", columnList = "tenantId, name"),
    @Index(name = "idx_kafka_cluster_status", columnList = "status"),
    @Index(name = "idx_kafka_cluster_health_status", columnList = "healthStatus")
})
public class KafkaCluster extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "bootstrap_servers", nullable = false, columnDefinition = "TEXT")
    private String bootstrapServers;

    @Column(name = "connection_type", length = 20)
    private String connectionType = "PLAINTEXT";

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "health_status", length = 20)
    private String healthStatus = "UNKNOWN";

    @Column(name = "kafka_version", length = 50)
    private String kafkaVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "security_config", columnDefinition = "TEXT")
    private Map<String, Object> securityConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "connection_config", columnDefinition = "TEXT")
    private Map<String, Object> connectionConfig;

    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;

    @Column(name = "health_check_error", columnDefinition = "TEXT")
    private String healthCheckError;

    @Column(name = "broker_count")
    private Integer brokerCount;

    @Column(name = "topic_count")
    private Integer topicCount;

    @Column(name = "partition_count")
    private Integer partitionCount;

    @Column(name = "consumer_group_count")
    private Integer consumerGroupCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics", columnDefinition = "TEXT")
    private Map<String, Object> metrics;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "monitoring_config", columnDefinition = "TEXT")
    private Map<String, Object> monitoringConfig;

    @Column(name = "auto_health_check")
    private Boolean autoHealthCheck = true;

    @Column(name = "health_check_interval_minutes")
    private Integer healthCheckIntervalMinutes = 5;

    public KafkaCluster() {}

    // Getters and Setters
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
}