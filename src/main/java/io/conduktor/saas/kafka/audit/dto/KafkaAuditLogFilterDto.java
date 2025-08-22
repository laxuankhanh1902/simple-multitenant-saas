package io.conduktor.saas.kafka.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Kafka audit log filter criteria")
public class KafkaAuditLogFilterDto {

    @Schema(description = "User email filter", example = "john.doe@acme-corp.com")
    private String userEmail;

    @Schema(description = "Action filter", example = "CREATE_TOPIC")
    private String action;

    @Schema(description = "List of actions to filter by")
    private List<String> actions;

    @Schema(description = "Resource type filter", example = "TOPIC")
    private String resourceType;

    @Schema(description = "Resource name filter", example = "user-events")
    private String resourceName;

    @Schema(description = "Cluster ID filter", example = "1")
    private Long clusterId;

    @Schema(description = "Cluster name filter", example = "production-cluster")
    private String clusterName;

    @Schema(description = "Topic name filter", example = "user-events")
    private String topicName;

    @Schema(description = "Consumer group filter", example = "analytics-consumer-group")
    private String consumerGroup;

    @Schema(description = "Status filter", example = "SUCCESS")
    private String status;

    @Schema(description = "IP address filter", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "Date from filter", example = "2024-03-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;

    @Schema(description = "Date to filter", example = "2024-03-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;

    @Schema(description = "Search term for full-text search")
    private String searchTerm;

    @Schema(description = "Page number", example = "0", defaultValue = "0")
    private int page = 0;

    @Schema(description = "Page size", example = "20", defaultValue = "20")
    private int size = 20;

    @Schema(description = "Sort field", example = "timestamp", defaultValue = "timestamp")
    private String sort = "timestamp";

    @Schema(description = "Sort direction", example = "desc", defaultValue = "desc")
    private String direction = "desc";

    // Constructors
    public KafkaAuditLogFilterDto() {}

    // Getters and Setters
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

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}