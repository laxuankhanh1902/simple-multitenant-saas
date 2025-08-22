package io.conduktor.saas.kafka.consumergroup.controller;

import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.kafka.consumergroup.dto.ConsumerGroupFilterDto;
import io.conduktor.saas.kafka.consumergroup.dto.ConsumerGroupLagMonitoringDto;
import io.conduktor.saas.kafka.consumergroup.dto.KafkaConsumerGroupDto;
import io.conduktor.saas.kafka.consumergroup.service.KafkaConsumerGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kafka/consumer-groups")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Kafka Consumer Groups", description = "Kafka Consumer Group management and monitoring API")
public class KafkaConsumerGroupController {

    @Autowired
    private KafkaConsumerGroupService consumerGroupService;

    @GetMapping
    @Operation(summary = "Get consumer groups", description = "Retrieve consumer groups with filtering and pagination")
    public ResponseEntity<ApiResponse<Page<KafkaConsumerGroupDto>>> getConsumerGroups(
            @ModelAttribute ConsumerGroupFilterDto filter) {
        
        Page<KafkaConsumerGroupDto> consumerGroups = consumerGroupService.getConsumerGroups(filter);
        return ResponseEntity.ok(ApiResponse.success(consumerGroups));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get consumer group by ID", description = "Retrieve a specific consumer group by its ID")
    public ResponseEntity<ApiResponse<KafkaConsumerGroupDto>> getConsumerGroupById(
            @Parameter(description = "Consumer group ID") @PathVariable Long id) {
        
        KafkaConsumerGroupDto consumerGroup = consumerGroupService.getConsumerGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(consumerGroup));
    }

    @GetMapping("/cluster/{clusterId}")
    @Operation(summary = "Get consumer groups by cluster", description = "Retrieve all consumer groups for a specific cluster")
    public ResponseEntity<ApiResponse<List<KafkaConsumerGroupDto>>> getConsumerGroupsByCluster(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId) {
        
        List<KafkaConsumerGroupDto> consumerGroups = consumerGroupService.getConsumerGroupsByCluster(clusterId);
        return ResponseEntity.ok(ApiResponse.success(consumerGroups));
    }

    @GetMapping("/cluster/{clusterId}/group/{groupId}")
    @Operation(summary = "Get specific consumer group", description = "Retrieve a specific consumer group by cluster and group ID")
    public ResponseEntity<ApiResponse<KafkaConsumerGroupDto>> getConsumerGroup(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId,
            @Parameter(description = "Consumer group ID") @PathVariable String groupId) {
        
        KafkaConsumerGroupDto consumerGroup = consumerGroupService.getConsumerGroup(clusterId, groupId);
        return ResponseEntity.ok(ApiResponse.success(consumerGroup));
    }

    @GetMapping("/cluster/{clusterId}/group/{groupId}/lag-monitoring")
    @Operation(summary = "Get consumer group lag monitoring", description = "Retrieve detailed lag monitoring information for a consumer group")
    public ResponseEntity<ApiResponse<ConsumerGroupLagMonitoringDto>> getConsumerGroupLagMonitoring(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId,
            @Parameter(description = "Consumer group ID") @PathVariable String groupId) {
        
        ConsumerGroupLagMonitoringDto lagMonitoring = consumerGroupService.getConsumerGroupLagMonitoring(clusterId, groupId);
        return ResponseEntity.ok(ApiResponse.success(lagMonitoring));
    }

    @GetMapping("/cluster/{clusterId}/high-lag")
    @Operation(summary = "Get consumer groups with high lag", description = "Retrieve consumer groups that exceed the lag threshold")
    public ResponseEntity<ApiResponse<List<KafkaConsumerGroupDto>>> getConsumerGroupsWithHighLag(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId,
            @Parameter(description = "Lag threshold (optional, default: 1000)") @RequestParam(required = false) Long lagThreshold) {
        
        List<KafkaConsumerGroupDto> consumerGroups = consumerGroupService.getConsumerGroupsWithHighLag(clusterId, lagThreshold);
        return ResponseEntity.ok(ApiResponse.success(consumerGroups));
    }

    @GetMapping("/cluster/{clusterId}/statistics")
    @Operation(summary = "Get consumer group statistics", description = "Retrieve statistical information about consumer groups in a cluster")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConsumerGroupStatistics(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId) {
        
        Map<String, Object> statistics = consumerGroupService.getConsumerGroupStatistics(clusterId);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @PostMapping("/cluster/{clusterId}/group/{groupId}/refresh-lag")
    @Operation(summary = "Refresh consumer group lag", description = "Refresh lag information for a specific consumer group")
    public ResponseEntity<ApiResponse<KafkaConsumerGroupDto>> refreshConsumerGroupLag(
            @Parameter(description = "Cluster ID") @PathVariable Long clusterId,
            @Parameter(description = "Consumer group ID") @PathVariable String groupId) {
        
        KafkaConsumerGroupDto consumerGroup = consumerGroupService.refreshConsumerGroupLag(clusterId, groupId);
        return ResponseEntity.ok(ApiResponse.success(consumerGroup));
    }
}