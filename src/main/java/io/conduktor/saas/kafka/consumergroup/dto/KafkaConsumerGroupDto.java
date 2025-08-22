package io.conduktor.saas.kafka.consumergroup.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Kafka Consumer Group information")
public class KafkaConsumerGroupDto {

    @Schema(description = "Consumer group ID", example = "1")
    private Long id;

    @Schema(description = "Tenant ID", example = "acme-corp")
    private String tenantId;

    @Schema(description = "Kafka cluster ID", example = "1")
    private Long clusterId;

    @Schema(description = "Consumer group name", example = "my-consumer-group")
    private String groupId;

    @Schema(description = "Consumer group state", example = "STABLE", allowableValues = {"PREPARING_REBALANCE", "COMPLETING_REBALANCE", "STABLE", "DEAD", "EMPTY"})
    private String state;

    @Schema(description = "Consumer group protocol", example = "range")
    private String protocol;

    @Schema(description = "Protocol type", example = "consumer")
    private String protocolType;

    @Schema(description = "Number of active members", example = "3")
    private Integer memberCount;

    @Schema(description = "Total lag across all partitions", example = "1500")
    private Long lagTotal;

    @Schema(description = "Assignment strategy", example = "org.apache.kafka.clients.consumer.RangeAssignor")
    private String assignmentStrategy;

    @Schema(description = "Coordinator broker ID", example = "1")
    private Integer coordinatorId;

    @Schema(description = "Description of the consumer group")
    private String description;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Created by user")
    private String createdBy;

    @Schema(description = "Updated by user")
    private String updatedBy;

    @Schema(description = "Version number")
    private Long version;

    public KafkaConsumerGroupDto() {}

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

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Long getLagTotal() {
        return lagTotal;
    }

    public void setLagTotal(Long lagTotal) {
        this.lagTotal = lagTotal;
    }

    public String getAssignmentStrategy() {
        return assignmentStrategy;
    }

    public void setAssignmentStrategy(String assignmentStrategy) {
        this.assignmentStrategy = assignmentStrategy;
    }

    public Integer getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Integer coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}