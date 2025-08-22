package io.conduktor.saas.kafka.consumergroup.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Consumer Group Lag Monitoring information")
public class ConsumerGroupLagMonitoringDto {

    @Schema(description = "Consumer group ID", example = "my-consumer-group")
    private String groupId;

    @Schema(description = "Consumer group state", example = "STABLE")
    private String state;

    @Schema(description = "Total lag across all partitions", example = "1500")
    private Long totalLag;

    @Schema(description = "Number of partitions with lag", example = "5")
    private Integer partitionsWithLag;

    @Schema(description = "Maximum lag across all partitions", example = "500")
    private Long maxLag;

    @Schema(description = "Average lag per partition", example = "300")
    private Double averageLag;

    @Schema(description = "Lag threshold exceeded flag", example = "true")
    private Boolean lagThresholdExceeded;

    @Schema(description = "Configured lag threshold", example = "1000")
    private Long lagThreshold;

    @Schema(description = "Last lag measurement timestamp")
    private LocalDateTime lastMeasured;

    @Schema(description = "Per-partition lag details")
    private List<PartitionLagDto> partitionLags;

    @Schema(description = "Lag trend over time (last hour)")
    private List<LagTrendDataPoint> lagTrend;

    @Schema(description = "Additional monitoring metadata")
    private Map<String, Object> monitoringMetadata;

    public ConsumerGroupLagMonitoringDto() {}

    // Inner classes for nested data
    @Schema(description = "Partition lag details")
    public static class PartitionLagDto {
        @Schema(description = "Topic name", example = "user-events")
        private String topic;

        @Schema(description = "Partition number", example = "0")
        private Integer partition;

        @Schema(description = "Current offset", example = "12345")
        private Long currentOffset;

        @Schema(description = "Log end offset", example = "12845")
        private Long logEndOffset;

        @Schema(description = "Lag in messages", example = "500")
        private Long lag;

        @Schema(description = "Consumer member ID")
        private String memberId;

        @Schema(description = "Client ID")
        private String clientId;

        @Schema(description = "Host info")
        private String host;

        // Getters and Setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public Integer getPartition() { return partition; }
        public void setPartition(Integer partition) { this.partition = partition; }
        public Long getCurrentOffset() { return currentOffset; }
        public void setCurrentOffset(Long currentOffset) { this.currentOffset = currentOffset; }
        public Long getLogEndOffset() { return logEndOffset; }
        public void setLogEndOffset(Long logEndOffset) { this.logEndOffset = logEndOffset; }
        public Long getLag() { return lag; }
        public void setLag(Long lag) { this.lag = lag; }
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
    }

    @Schema(description = "Lag trend data point")
    public static class LagTrendDataPoint {
        @Schema(description = "Timestamp of measurement")
        private LocalDateTime timestamp;

        @Schema(description = "Total lag at this time", example = "1200")
        private Long totalLag;

        @Schema(description = "Number of active members", example = "3")
        private Integer memberCount;

        // Getters and Setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public Long getTotalLag() { return totalLag; }
        public void setTotalLag(Long totalLag) { this.totalLag = totalLag; }
        public Integer getMemberCount() { return memberCount; }
        public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    }

    // Main class getters and setters
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

    public Long getTotalLag() {
        return totalLag;
    }

    public void setTotalLag(Long totalLag) {
        this.totalLag = totalLag;
    }

    public Integer getPartitionsWithLag() {
        return partitionsWithLag;
    }

    public void setPartitionsWithLag(Integer partitionsWithLag) {
        this.partitionsWithLag = partitionsWithLag;
    }

    public Long getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(Long maxLag) {
        this.maxLag = maxLag;
    }

    public Double getAverageLag() {
        return averageLag;
    }

    public void setAverageLag(Double averageLag) {
        this.averageLag = averageLag;
    }

    public Boolean getLagThresholdExceeded() {
        return lagThresholdExceeded;
    }

    public void setLagThresholdExceeded(Boolean lagThresholdExceeded) {
        this.lagThresholdExceeded = lagThresholdExceeded;
    }

    public Long getLagThreshold() {
        return lagThreshold;
    }

    public void setLagThreshold(Long lagThreshold) {
        this.lagThreshold = lagThreshold;
    }

    public LocalDateTime getLastMeasured() {
        return lastMeasured;
    }

    public void setLastMeasured(LocalDateTime lastMeasured) {
        this.lastMeasured = lastMeasured;
    }

    public List<PartitionLagDto> getPartitionLags() {
        return partitionLags;
    }

    public void setPartitionLags(List<PartitionLagDto> partitionLags) {
        this.partitionLags = partitionLags;
    }

    public List<LagTrendDataPoint> getLagTrend() {
        return lagTrend;
    }

    public void setLagTrend(List<LagTrendDataPoint> lagTrend) {
        this.lagTrend = lagTrend;
    }

    public Map<String, Object> getMonitoringMetadata() {
        return monitoringMetadata;
    }

    public void setMonitoringMetadata(Map<String, Object> monitoringMetadata) {
        this.monitoringMetadata = monitoringMetadata;
    }
}