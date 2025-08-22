package io.conduktor.saas.kafka.consumergroup.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Filter criteria for consumer groups")
public class ConsumerGroupFilterDto {

    @Schema(description = "Filter by cluster ID", example = "1")
    private Long clusterId;

    @Schema(description = "Filter by consumer group state", example = "STABLE")
    private String state;

    @Schema(description = "Filter by states", example = "[\"STABLE\", \"PREPARING_REBALANCE\"]")
    private List<String> states;

    @Schema(description = "Filter by group ID pattern (supports wildcards)", example = "user-*")
    private String groupIdPattern;

    @Schema(description = "Filter by minimum lag threshold", example = "1000")
    private Long minLag;

    @Schema(description = "Filter by maximum lag threshold", example = "10000")
    private Long maxLag;

    @Schema(description = "Filter by minimum member count", example = "1")
    private Integer minMemberCount;

    @Schema(description = "Filter by maximum member count", example = "10")
    private Integer maxMemberCount;

    @Schema(description = "Filter by protocol", example = "consumer")
    private String protocol;

    @Schema(description = "Filter by assignment strategy", example = "range")
    private String assignmentStrategy;

    @Schema(description = "Include only active consumer groups", example = "true")
    private Boolean activeOnly;

    @Schema(description = "Include only consumer groups with lag", example = "true")
    private Boolean withLagOnly;

    @Schema(description = "Sort field", example = "lagTotal", allowableValues = {"groupId", "state", "lagTotal", "memberCount", "createdAt", "updatedAt"})
    private String sortBy = "groupId";

    @Schema(description = "Sort direction", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "ASC";

    @Schema(description = "Page number (0-based)", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size", example = "20")
    private Integer size = 20;

    public ConsumerGroupFilterDto() {}

    // Getters and Setters
    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public String getGroupIdPattern() {
        return groupIdPattern;
    }

    public void setGroupIdPattern(String groupIdPattern) {
        this.groupIdPattern = groupIdPattern;
    }

    public Long getMinLag() {
        return minLag;
    }

    public void setMinLag(Long minLag) {
        this.minLag = minLag;
    }

    public Long getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(Long maxLag) {
        this.maxLag = maxLag;
    }

    public Integer getMinMemberCount() {
        return minMemberCount;
    }

    public void setMinMemberCount(Integer minMemberCount) {
        this.minMemberCount = minMemberCount;
    }

    public Integer getMaxMemberCount() {
        return maxMemberCount;
    }

    public void setMaxMemberCount(Integer maxMemberCount) {
        this.maxMemberCount = maxMemberCount;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAssignmentStrategy() {
        return assignmentStrategy;
    }

    public void setAssignmentStrategy(String assignmentStrategy) {
        this.assignmentStrategy = assignmentStrategy;
    }

    public Boolean getActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(Boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public Boolean getWithLagOnly() {
        return withLagOnly;
    }

    public void setWithLagOnly(Boolean withLagOnly) {
        this.withLagOnly = withLagOnly;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}