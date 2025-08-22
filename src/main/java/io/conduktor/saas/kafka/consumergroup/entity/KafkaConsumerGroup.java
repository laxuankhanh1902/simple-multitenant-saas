package io.conduktor.saas.kafka.consumergroup.entity;

import io.conduktor.saas.core.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "kafka_consumer_groups", indexes = {
    @Index(name = "idx_kafka_consumer_group_tenant_cluster", columnList = "tenantId, clusterId"),
    @Index(name = "idx_kafka_consumer_group_id", columnList = "groupId"),
    @Index(name = "idx_kafka_consumer_group_state", columnList = "state"),
    @Index(name = "idx_kafka_consumer_group_lag", columnList = "lagTotal")
})
public class KafkaConsumerGroup extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "cluster_id", nullable = false)
    private Long clusterId;

    @Column(name = "group_id", nullable = false, length = 200)
    private String groupId;

    @Column(name = "state", length = 20)
    private String state;

    @Column(name = "protocol", length = 50)
    private String protocol;

    @Column(name = "protocol_type", length = 50)
    private String protocolType;

    @Column(name = "member_count")
    private Integer memberCount = 0;

    @Column(name = "lag_total")
    private Long lagTotal = 0L;

    @Column(name = "assignment_strategy", length = 100)
    private String assignmentStrategy;

    @Column(name = "coordinator_id")
    private Integer coordinatorId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private Map<String, Object> metadata;

    public KafkaConsumerGroup() {}

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
}