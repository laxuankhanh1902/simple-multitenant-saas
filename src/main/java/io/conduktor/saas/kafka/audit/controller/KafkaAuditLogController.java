package io.conduktor.saas.kafka.audit.controller;

import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.kafka.audit.dto.KafkaAuditLogDto;
import io.conduktor.saas.kafka.audit.dto.KafkaAuditLogFilterDto;
import io.conduktor.saas.kafka.audit.service.KafkaAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka/audit-logs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Kafka Audit Logs", description = "Kafka audit log management APIs")
public class KafkaAuditLogController {

    private final KafkaAuditLogService kafkaAuditLogService;

    public KafkaAuditLogController(KafkaAuditLogService kafkaAuditLogService) {
        this.kafkaAuditLogService = kafkaAuditLogService;
    }

    @GetMapping
    @Operation(summary = "Get paginated audit logs with advanced filtering")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<KafkaAuditLogDto>>> getAuditLogs(
            @Parameter(description = "User email filter") @RequestParam(required = false) String userEmail,
            @Parameter(description = "Action filter") @RequestParam(required = false) String action,
            @Parameter(description = "Multiple actions filter") @RequestParam(required = false) List<String> actions,
            @Parameter(description = "Resource type filter") @RequestParam(required = false) String resourceType,
            @Parameter(description = "Resource name filter") @RequestParam(required = false) String resourceName,
            @Parameter(description = "Cluster ID filter") @RequestParam(required = false) Long clusterId,
            @Parameter(description = "Cluster name filter") @RequestParam(required = false) String clusterName,
            @Parameter(description = "Topic name filter") @RequestParam(required = false) String topicName,
            @Parameter(description = "Consumer group filter") @RequestParam(required = false) String consumerGroup,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status,
            @Parameter(description = "IP address filter") @RequestParam(required = false) String ipAddress,
            @Parameter(description = "Date from filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @Parameter(description = "Date to filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @Parameter(description = "Search term for full-text search") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "timestamp") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {

        KafkaAuditLogFilterDto filter = new KafkaAuditLogFilterDto();
        filter.setUserEmail(userEmail);
        filter.setAction(action);
        filter.setActions(actions);
        filter.setResourceType(resourceType);
        filter.setResourceName(resourceName);
        filter.setClusterId(clusterId);
        filter.setClusterName(clusterName);
        filter.setTopicName(topicName);
        filter.setConsumerGroup(consumerGroup);
        filter.setStatus(status);
        filter.setIpAddress(ipAddress);
        filter.setDateFrom(dateFrom);
        filter.setDateTo(dateTo);
        filter.setSearchTerm(searchTerm);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSort(sort);
        filter.setDirection(direction);

        PageResponse<KafkaAuditLogDto> auditLogs = kafkaAuditLogService.findAuditLogs(filter);
        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific audit log by ID")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<KafkaAuditLogDto>> getAuditLogById(
            @Parameter(description = "Audit log ID") @PathVariable Long id) {
        
        KafkaAuditLogDto auditLog = kafkaAuditLogService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(auditLog));
    }

    @GetMapping("/export")
    @Operation(summary = "Export audit logs to CSV")
    @PreAuthorize("hasRole('USER')")
    public void exportAuditLogs(
            @Parameter(description = "User email filter") @RequestParam(required = false) String userEmail,
            @Parameter(description = "Action filter") @RequestParam(required = false) String action,
            @Parameter(description = "Resource type filter") @RequestParam(required = false) String resourceType,
            @Parameter(description = "Date from filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @Parameter(description = "Date to filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            HttpServletResponse response) throws IOException {

        KafkaAuditLogFilterDto filter = new KafkaAuditLogFilterDto();
        filter.setUserEmail(userEmail);
        filter.setAction(action);
        filter.setResourceType(resourceType);
        filter.setDateFrom(dateFrom);
        filter.setDateTo(dateTo);
        filter.setSize(10000); // Large size for export

        PageResponse<KafkaAuditLogDto> auditLogs = kafkaAuditLogService.findAuditLogs(filter);

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
            "attachment; filename=kafka-audit-logs-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".csv");

        try (PrintWriter writer = response.getWriter()) {
            // CSV Header
            writer.println("Timestamp,User Email,Action,Resource Type,Resource Name,Cluster Name,Topic Name,Consumer Group,Status,IP Address,Duration (ms)");

            // CSV Data
            for (KafkaAuditLogDto auditLog : auditLogs.getContent()) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    auditLog.getTimestamp() != null ? auditLog.getTimestamp().toString() : "",
                    auditLog.getUserEmail() != null ? auditLog.getUserEmail() : "",
                    auditLog.getAction() != null ? auditLog.getAction() : "",
                    auditLog.getResourceType() != null ? auditLog.getResourceType() : "",
                    auditLog.getResourceName() != null ? auditLog.getResourceName() : "",
                    auditLog.getClusterName() != null ? auditLog.getClusterName() : "",
                    auditLog.getTopicName() != null ? auditLog.getTopicName() : "",
                    auditLog.getConsumerGroup() != null ? auditLog.getConsumerGroup() : "",
                    auditLog.getStatus() != null ? auditLog.getStatus() : "",
                    auditLog.getIpAddress() != null ? auditLog.getIpAddress() : "",
                    auditLog.getDurationMs() != null ? auditLog.getDurationMs().toString() : ""
                );
            }
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get audit log statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuditStatistics(
            @Parameter(description = "Date from filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @Parameter(description = "Date to filter") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {

        Map<String, Object> stats = kafkaAuditLogService.getAuditStatistics(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/actions")
    @Operation(summary = "Get available actions for filtering")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableActions() {
        List<String> actions = kafkaAuditLogService.getAvailableActions();
        return ResponseEntity.ok(ApiResponse.success(actions));
    }

    @GetMapping("/users")
    @Operation(summary = "Get available users for filtering")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableUsers() {
        List<String> users = kafkaAuditLogService.getAvailableUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/clusters")
    @Operation(summary = "Get available clusters for filtering")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableClusters() {
        List<String> clusters = kafkaAuditLogService.getAvailableClusters();
        return ResponseEntity.ok(ApiResponse.success(clusters));
    }

    @GetMapping("/topics")
    @Operation(summary = "Get available topics for filtering")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableTopics() {
        List<String> topics = kafkaAuditLogService.getAvailableTopics();
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
}