package io.conduktor.saas.kafka.cluster.service;

import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.core.service.BaseService;
import io.conduktor.saas.kafka.cluster.dto.CreateKafkaClusterRequest;
import io.conduktor.saas.kafka.cluster.dto.KafkaClusterDto;
import io.conduktor.saas.kafka.cluster.dto.UpdateKafkaClusterRequest;
import io.conduktor.saas.kafka.cluster.entity.KafkaCluster;
import io.conduktor.saas.kafka.cluster.repository.KafkaClusterRepository;
import io.conduktor.saas.security.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
// @Transactional
public class KafkaClusterService {

    private final KafkaClusterRepository kafkaClusterRepository;

    public KafkaClusterService(KafkaClusterRepository kafkaClusterRepository) {
        this.kafkaClusterRepository = kafkaClusterRepository;
    }

    public PageResponse<KafkaClusterDto> findClusters(String status, String healthStatus, String searchTerm,
                                                     int page, int size, String sort, String direction) {
        String tenantId = TenantContext.getCurrentTenant();
        
        Sort sortObj = Sort.by(
            "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC,
            sort
        );
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<KafkaCluster> clusters = kafkaClusterRepository.findByTenantIdWithFilters(
            tenantId, status, healthStatus, searchTerm, pageable
        );
        
        List<KafkaClusterDto> dtos = clusters.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return new PageResponse<>(
            dtos,
            clusters.getNumber(),
            clusters.getSize(),
            clusters.getTotalElements(),
            clusters.getTotalPages(),
            clusters.isFirst(),
            clusters.isLast()
        );
    }

    public List<KafkaClusterDto> findAllClusters() {
        String tenantId = TenantContext.getCurrentTenant();
        List<KafkaCluster> clusters = kafkaClusterRepository.findByTenantId(tenantId);
        return clusters.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public KafkaClusterDto findById(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        KafkaCluster cluster = kafkaClusterRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));
        
        return convertToDto(cluster);
    }

    public KafkaClusterDto createCluster(CreateKafkaClusterRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        
        // Check if cluster name already exists
        kafkaClusterRepository.findByTenantIdAndName(tenantId, request.getName())
            .ifPresent(cluster -> {
                throw new RuntimeException("Cluster with name '" + request.getName() + "' already exists");
            });
        
        KafkaCluster cluster = new KafkaCluster();
        cluster.setTenantId(tenantId);
        cluster.setName(request.getName());
        cluster.setDescription(request.getDescription());
        cluster.setBootstrapServers(request.getBootstrapServers());
        cluster.setConnectionType(request.getConnectionType());
        cluster.setSecurityConfig(request.getSecurityConfig());
        cluster.setConnectionConfig(request.getConnectionConfig());
        cluster.setMonitoringConfig(request.getMonitoringConfig());
        cluster.setAutoHealthCheck(request.getAutoHealthCheck());
        cluster.setHealthCheckIntervalMinutes(request.getHealthCheckIntervalMinutes());
        cluster.setStatus("ACTIVE");
        cluster.setHealthStatus("UNKNOWN");
        
        cluster = kafkaClusterRepository.save(cluster);
        
        // Perform initial health check
        performHealthCheck(cluster);
        
        return convertToDto(cluster);
    }

    public KafkaClusterDto updateCluster(Long id, UpdateKafkaClusterRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        
        KafkaCluster cluster = kafkaClusterRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));
        
        // Check if new name conflicts with existing cluster
        if (request.getName() != null && !request.getName().equals(cluster.getName())) {
            kafkaClusterRepository.findByTenantIdAndName(tenantId, request.getName())
                .ifPresent(existingCluster -> {
                    throw new RuntimeException("Cluster with name '" + request.getName() + "' already exists");
                });
        }
        
        // Update fields if provided
        if (request.getName() != null) cluster.setName(request.getName());
        if (request.getDescription() != null) cluster.setDescription(request.getDescription());
        if (request.getBootstrapServers() != null) cluster.setBootstrapServers(request.getBootstrapServers());
        if (request.getConnectionType() != null) cluster.setConnectionType(request.getConnectionType());
        if (request.getStatus() != null) cluster.setStatus(request.getStatus());
        if (request.getSecurityConfig() != null) cluster.setSecurityConfig(request.getSecurityConfig());
        if (request.getConnectionConfig() != null) cluster.setConnectionConfig(request.getConnectionConfig());
        if (request.getMonitoringConfig() != null) cluster.setMonitoringConfig(request.getMonitoringConfig());
        if (request.getAutoHealthCheck() != null) cluster.setAutoHealthCheck(request.getAutoHealthCheck());
        if (request.getHealthCheckIntervalMinutes() != null) cluster.setHealthCheckIntervalMinutes(request.getHealthCheckIntervalMinutes());
        
        cluster = kafkaClusterRepository.save(cluster);
        
        return convertToDto(cluster);
    }

    public void deleteCluster(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        
        KafkaCluster cluster = kafkaClusterRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));
        
        kafkaClusterRepository.delete(cluster);
    }

    public Map<String, Object> performHealthCheck(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        
        KafkaCluster cluster = kafkaClusterRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));
        
        return performHealthCheck(cluster);
    }

    public Map<String, Object> getClusterMetrics(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        
        KafkaCluster cluster = kafkaClusterRepository.findByTenantIdAndId(tenantId, id)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("brokerCount", cluster.getBrokerCount());
        metrics.put("topicCount", cluster.getTopicCount());
        metrics.put("partitionCount", cluster.getPartitionCount());
        metrics.put("consumerGroupCount", cluster.getConsumerGroupCount());
        metrics.put("healthStatus", cluster.getHealthStatus());
        metrics.put("lastHealthCheck", cluster.getLastHealthCheck());
        metrics.put("kafkaVersion", cluster.getKafkaVersion());
        
        // Add custom metrics if available
        if (cluster.getMetrics() != null) {
            metrics.putAll(cluster.getMetrics());
        }
        
        return metrics;
    }

    public Map<String, Object> getClusterStatistics() {
        String tenantId = TenantContext.getCurrentTenant();
        
        Long totalClusters = kafkaClusterRepository.countClustersByTenantId(tenantId);
        Long activeClusters = kafkaClusterRepository.countActiveByTenantId(tenantId);
        Long healthyClusters = kafkaClusterRepository.countHealthyByTenantId(tenantId);
        Long totalTopics = kafkaClusterRepository.getTotalTopicCountByTenantId(tenantId);
        Long totalPartitions = kafkaClusterRepository.getTotalPartitionCountByTenantId(tenantId);
        Long totalConsumerGroups = kafkaClusterRepository.getTotalConsumerGroupCountByTenantId(tenantId);
        
        List<Object[]> healthStats = kafkaClusterRepository.getHealthStatusStatsByTenantId(tenantId);
        List<String> connectionTypes = kafkaClusterRepository.findDistinctConnectionTypesByTenantId(tenantId);
        List<String> versions = kafkaClusterRepository.findDistinctVersionsByTenantId(tenantId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClusters", totalClusters != null ? totalClusters : 0L);
        stats.put("activeClusters", activeClusters != null ? activeClusters : 0L);
        stats.put("healthyClusters", healthyClusters != null ? healthyClusters : 0L);
        stats.put("totalTopics", totalTopics != null ? totalTopics : 0L);
        stats.put("totalPartitions", totalPartitions != null ? totalPartitions : 0L);
        stats.put("totalConsumerGroups", totalConsumerGroups != null ? totalConsumerGroups : 0L);
        stats.put("healthStats", healthStats);
        stats.put("connectionTypes", connectionTypes);
        stats.put("versions", versions);
        
        return stats;
    }

    private Map<String, Object> performHealthCheck(KafkaCluster cluster) {
        Map<String, Object> healthResult = new HashMap<>();
        
        try {
            // Simulate health check - in real implementation, this would connect to Kafka
            boolean isHealthy = simulateKafkaHealthCheck(cluster);
            
            cluster.setLastHealthCheck(LocalDateTime.now());
            cluster.setHealthStatus(isHealthy ? "HEALTHY" : "UNHEALTHY");
            cluster.setHealthCheckError(isHealthy ? null : "Connection timeout");
            
            // Simulate getting cluster info
            if (isHealthy) {
                cluster.setBrokerCount(3);
                cluster.setTopicCount(25);
                cluster.setPartitionCount(150);
                cluster.setConsumerGroupCount(8);
                cluster.setKafkaVersion("3.5.0");
            }
            
            kafkaClusterRepository.save(cluster);
            
            healthResult.put("status", cluster.getHealthStatus());
            healthResult.put("lastCheck", cluster.getLastHealthCheck());
            healthResult.put("brokerCount", cluster.getBrokerCount());
            healthResult.put("error", cluster.getHealthCheckError());
            
        } catch (Exception e) {
            cluster.setLastHealthCheck(LocalDateTime.now());
            cluster.setHealthStatus("ERROR");
            cluster.setHealthCheckError(e.getMessage());
            kafkaClusterRepository.save(cluster);
            
            healthResult.put("status", "ERROR");
            healthResult.put("error", e.getMessage());
        }
        
        return healthResult;
    }

    private boolean simulateKafkaHealthCheck(KafkaCluster cluster) {
        // Simulate health check - in real implementation, this would:
        // 1. Create Kafka admin client with cluster's bootstrap servers
        // 2. Check if brokers are reachable
        // 3. Get cluster metadata
        // 4. Return health status
        
        // For demo purposes, randomly return healthy status
        return Math.random() > 0.1; // 90% chance of being healthy
    }

    private KafkaClusterDto convertToDto(KafkaCluster cluster) {
        KafkaClusterDto dto = new KafkaClusterDto();
        BeanUtils.copyProperties(cluster, dto);
        return dto;
    }
}