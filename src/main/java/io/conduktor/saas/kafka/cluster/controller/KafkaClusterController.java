package io.conduktor.saas.kafka.cluster.controller;

import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.kafka.cluster.dto.CreateKafkaClusterRequest;
import io.conduktor.saas.kafka.cluster.dto.KafkaClusterDto;
import io.conduktor.saas.kafka.cluster.dto.UpdateKafkaClusterRequest;
import io.conduktor.saas.kafka.cluster.service.KafkaClusterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/kafka/clusters")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Kafka Clusters", description = "Kafka cluster management APIs")
public class KafkaClusterController {

    private final KafkaClusterService kafkaClusterService;

    public KafkaClusterController(KafkaClusterService kafkaClusterService) {
        this.kafkaClusterService = kafkaClusterService;
    }

    @GetMapping
    @Operation(summary = "Get all clusters with health status and pagination")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<KafkaClusterDto>>> getClusters(
            @Parameter(description = "Status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Health status filter") @RequestParam(required = false) String healthStatus,
            @Parameter(description = "Search term") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        PageResponse<KafkaClusterDto> clusters = kafkaClusterService.findClusters(
            status, healthStatus, searchTerm, page, size, sort, direction
        );
        return ResponseEntity.ok(ApiResponse.success(clusters));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all clusters without pagination")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<KafkaClusterDto>>> getAllClusters() {
        List<KafkaClusterDto> clusters = kafkaClusterService.findAllClusters();
        return ResponseEntity.ok(ApiResponse.success(clusters));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific cluster details")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<KafkaClusterDto>> getClusterById(
            @Parameter(description = "Cluster ID") @PathVariable Long id) {
        
        KafkaClusterDto cluster = kafkaClusterService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(cluster));
    }

    @PostMapping
    @Operation(summary = "Create new cluster connection")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<KafkaClusterDto>> createCluster(
            @Valid @RequestBody CreateKafkaClusterRequest request) {
        
        KafkaClusterDto cluster = kafkaClusterService.createCluster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cluster));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cluster configuration")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<KafkaClusterDto>> updateCluster(
            @Parameter(description = "Cluster ID") @PathVariable Long id,
            @Valid @RequestBody UpdateKafkaClusterRequest request) {
        
        KafkaClusterDto cluster = kafkaClusterService.updateCluster(id, request);
        return ResponseEntity.ok(ApiResponse.success(cluster));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove cluster connection")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCluster(
            @Parameter(description = "Cluster ID") @PathVariable Long id) {
        
        kafkaClusterService.deleteCluster(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/health-check")
    @Operation(summary = "Perform cluster health check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> performHealthCheck(
            @Parameter(description = "Cluster ID") @PathVariable Long id) {
        
        Map<String, Object> healthResult = kafkaClusterService.performHealthCheck(id);
        return ResponseEntity.ok(ApiResponse.success(healthResult));
    }

    @GetMapping("/{id}/metrics")
    @Operation(summary = "Get cluster metrics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClusterMetrics(
            @Parameter(description = "Cluster ID") @PathVariable Long id) {
        
        Map<String, Object> metrics = kafkaClusterService.getClusterMetrics(id);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get cluster statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClusterStatistics() {
        Map<String, Object> stats = kafkaClusterService.getClusterStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/export")
    @Operation(summary = "Export cluster data to CSV")
    @PreAuthorize("hasRole('USER')")
    public void exportClusters(
            @Parameter(description = "Status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Health status filter") @RequestParam(required = false) String healthStatus,
            HttpServletResponse response) throws IOException {

        PageResponse<KafkaClusterDto> clusters = kafkaClusterService.findClusters(
            status, healthStatus, null, 0, 10000, "name", "asc"
        );

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
            "attachment; filename=kafka-clusters-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".csv");

        try (PrintWriter writer = response.getWriter()) {
            // CSV Header
            writer.println("ID,Name,Description,Bootstrap Servers,Connection Type,Status,Health Status,Version,Broker Count,Topic Count,Partition Count,Consumer Group Count,Last Health Check,Created At");

            // CSV Data
            for (KafkaClusterDto cluster : clusters.getContent()) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    cluster.getId() != null ? cluster.getId().toString() : "",
                    cluster.getName() != null ? cluster.getName() : "",
                    cluster.getDescription() != null ? cluster.getDescription().replace(",", ";") : "",
                    cluster.getBootstrapServers() != null ? cluster.getBootstrapServers().replace(",", ";") : "",
                    cluster.getConnectionType() != null ? cluster.getConnectionType() : "",
                    cluster.getStatus() != null ? cluster.getStatus() : "",
                    cluster.getHealthStatus() != null ? cluster.getHealthStatus() : "",
                    cluster.getKafkaVersion() != null ? cluster.getKafkaVersion() : "",
                    cluster.getBrokerCount() != null ? cluster.getBrokerCount().toString() : "",
                    cluster.getTopicCount() != null ? cluster.getTopicCount().toString() : "",
                    cluster.getPartitionCount() != null ? cluster.getPartitionCount().toString() : "",
                    cluster.getConsumerGroupCount() != null ? cluster.getConsumerGroupCount().toString() : "",
                    cluster.getLastHealthCheck() != null ? cluster.getLastHealthCheck().toString() : "",
                    cluster.getCreatedAt() != null ? cluster.getCreatedAt().toString() : ""
                );
            }
        }
    }
}