package io.conduktor.saas.kafka.cluster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Request to create a new Kafka cluster connection")
public class CreateKafkaClusterRequest {

    @NotBlank(message = "Cluster name is required")
    @Schema(description = "Cluster name", example = "production-cluster", required = true)
    private String name;

    @Schema(description = "Cluster description", example = "Production Kafka cluster for event streaming")
    private String description;

    @NotBlank(message = "Bootstrap servers are required")
    @Schema(description = "Bootstrap servers", example = "kafka1:9092,kafka2:9092,kafka3:9092", required = true)
    private String bootstrapServers;

    @Schema(description = "Connection type", example = "SASL_SSL", defaultValue = "PLAINTEXT")
    private String connectionType = "PLAINTEXT";

    @Schema(description = "Security configuration")
    private Map<String, Object> securityConfig;

    @Schema(description = "Connection configuration")
    private Map<String, Object> connectionConfig;

    @Schema(description = "Monitoring configuration")
    private Map<String, Object> monitoringConfig;

    @Schema(description = "Auto health check enabled", example = "true", defaultValue = "true")
    private Boolean autoHealthCheck = true;

    @Schema(description = "Health check interval in minutes", example = "5", defaultValue = "5")
    private Integer healthCheckIntervalMinutes = 5;

    // Constructors
    public CreateKafkaClusterRequest() {}

    // Getters and Setters
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