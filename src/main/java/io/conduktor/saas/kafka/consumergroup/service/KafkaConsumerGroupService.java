package io.conduktor.saas.kafka.consumergroup.service;

import io.conduktor.saas.core.exception.ResourceNotFoundException;
import io.conduktor.saas.security.TenantContext;
import io.conduktor.saas.kafka.consumergroup.dto.ConsumerGroupFilterDto;
import io.conduktor.saas.kafka.consumergroup.dto.ConsumerGroupLagMonitoringDto;
import io.conduktor.saas.kafka.consumergroup.dto.KafkaConsumerGroupDto;
import io.conduktor.saas.kafka.consumergroup.entity.KafkaConsumerGroup;
import io.conduktor.saas.kafka.consumergroup.repository.KafkaConsumerGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerGroupService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGroupService.class);
    private static final Long DEFAULT_LAG_THRESHOLD = 1000L;

    @Autowired
    private KafkaConsumerGroupRepository consumerGroupRepository;

    private String getCurrentTenantId() {
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("No tenant context available");
        }
        return tenantId;
    }

    public Page<KafkaConsumerGroupDto> getConsumerGroups(ConsumerGroupFilterDto filter) {
        String tenantId = getCurrentTenantId();
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        Page<KafkaConsumerGroup> consumerGroups;
        
        if (filter.getClusterId() != null) {
            consumerGroups = consumerGroupRepository.findByTenantIdAndClusterId(
                tenantId, filter.getClusterId(), pageable);
        } else if (filter.getGroupIdPattern() != null) {
            consumerGroups = consumerGroupRepository.findByTenantIdAndGroupIdContaining(
                tenantId, filter.getGroupIdPattern(), pageable);
        } else {
            consumerGroups = consumerGroupRepository.findAll(pageable);
        }
        
        return consumerGroups.map(this::convertToDto);
    }

    public KafkaConsumerGroupDto getConsumerGroupById(Long id) {
        String tenantId = getCurrentTenantId();
        KafkaConsumerGroup consumerGroup = consumerGroupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consumer group not found with id: " + id));
        
        if (!tenantId.equals(consumerGroup.getTenantId())) {
            throw new ResourceNotFoundException("Consumer group not found with id: " + id);
        }
        
        return convertToDto(consumerGroup);
    }

    public KafkaConsumerGroupDto getConsumerGroup(Long clusterId, String groupId) {
        String tenantId = getCurrentTenantId();
        KafkaConsumerGroup consumerGroup = consumerGroupRepository
            .findByTenantIdAndClusterIdAndGroupId(tenantId, clusterId, groupId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Consumer group not found: " + groupId + " in cluster: " + clusterId));
        
        return convertToDto(consumerGroup);
    }

    public List<KafkaConsumerGroupDto> getConsumerGroupsByCluster(Long clusterId) {
        String tenantId = getCurrentTenantId();
        List<KafkaConsumerGroup> consumerGroups = consumerGroupRepository
            .findByTenantIdAndClusterId(tenantId, clusterId);
        
        return consumerGroups.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public ConsumerGroupLagMonitoringDto getConsumerGroupLagMonitoring(Long clusterId, String groupId) {
        String tenantId = getCurrentTenantId();
        KafkaConsumerGroup consumerGroup = consumerGroupRepository
            .findByTenantIdAndClusterIdAndGroupId(tenantId, clusterId, groupId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Consumer group not found: " + groupId + " in cluster: " + clusterId));

        return createLagMonitoringDto(consumerGroup);
    }

    public List<KafkaConsumerGroupDto> getConsumerGroupsWithHighLag(Long clusterId, Long lagThreshold) {
        String tenantId = getCurrentTenantId();
        Long threshold = lagThreshold != null ? lagThreshold : DEFAULT_LAG_THRESHOLD;
        
        List<KafkaConsumerGroup> consumerGroups = consumerGroupRepository
            .findConsumerGroupsWithHighLag(tenantId, clusterId, threshold);
        
        return consumerGroups.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Map<String, Object> getConsumerGroupStatistics(Long clusterId) {
        String tenantId = getCurrentTenantId();
        
        Map<String, Object> stats = new HashMap<>();
        
        List<KafkaConsumerGroup> allGroups = consumerGroupRepository
            .findByTenantIdAndClusterId(tenantId, clusterId);
        
        Long totalLag = consumerGroupRepository.getTotalLagByCluster(tenantId, clusterId);
        Long activeGroups = consumerGroupRepository.countActiveConsumerGroups(tenantId, clusterId);
        
        stats.put("totalConsumerGroups", allGroups.size());
        stats.put("activeConsumerGroups", activeGroups);
        stats.put("totalLag", totalLag != null ? totalLag : 0L);
        
        // Calculate state distribution
        Map<String, Long> stateDistribution = allGroups.stream()
            .collect(Collectors.groupingBy(
                cg -> cg.getState() != null ? cg.getState() : "UNKNOWN",
                Collectors.counting()
            ));
        stats.put("stateDistribution", stateDistribution);
        
        // Calculate lag distribution
        Long groupsWithLag = allGroups.stream()
            .mapToLong(cg -> cg.getLagTotal() > 0 ? 1 : 0)
            .sum();
        stats.put("consumerGroupsWithLag", groupsWithLag);
        
        // Calculate high lag groups
        Long highLagGroups = allGroups.stream()
            .mapToLong(cg -> cg.getLagTotal() > DEFAULT_LAG_THRESHOLD ? 1 : 0)
            .sum();
        stats.put("highLagGroups", highLagGroups);
        
        return stats;
    }

    public KafkaConsumerGroupDto refreshConsumerGroupLag(Long clusterId, String groupId) {
        String tenantId = getCurrentTenantId();
        
        // In a real implementation, this would connect to Kafka and fetch real-time lag data
        // For now, we'll simulate refreshing the data
        KafkaConsumerGroup consumerGroup = consumerGroupRepository
            .findByTenantIdAndClusterIdAndGroupId(tenantId, clusterId, groupId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Consumer group not found: " + groupId + " in cluster: " + clusterId));
        
        // Simulate fetching updated lag data
        simulateKafkaLagUpdate(consumerGroup);
        
        KafkaConsumerGroup updated = consumerGroupRepository.save(consumerGroup);
        
        logger.info("Refreshed lag data for consumer group: {} in cluster: {} for tenant: {}", 
                   groupId, clusterId, tenantId);
        
        return convertToDto(updated);
    }

    private void simulateKafkaLagUpdate(KafkaConsumerGroup consumerGroup) {
        // In a real implementation, this would use Kafka AdminClient to fetch real data
        // For demonstration, we'll simulate some realistic lag data
        long currentLag = consumerGroup.getLagTotal() != null ? consumerGroup.getLagTotal() : 0L;
        
        // Simulate lag fluctuation (±10%)
        double variation = (Math.random() - 0.5) * 0.2;
        long newLag = Math.max(0, (long) (currentLag * (1 + variation)));
        
        consumerGroup.setLagTotal(newLag);
        consumerGroup.setUpdatedAt(LocalDateTime.now());
        consumerGroup.setUpdatedBy("system");
        
        // Update metadata with last refresh time
        if (consumerGroup.getMetadata() == null) {
            consumerGroup.setMetadata(new HashMap<>());
        }
        consumerGroup.getMetadata().put("lastLagRefresh", LocalDateTime.now().toString());
        consumerGroup.getMetadata().put("lagTrend", newLag > currentLag ? "increasing" : "decreasing");
    }

    private ConsumerGroupLagMonitoringDto createLagMonitoringDto(KafkaConsumerGroup consumerGroup) {
        ConsumerGroupLagMonitoringDto dto = new ConsumerGroupLagMonitoringDto();
        
        dto.setGroupId(consumerGroup.getGroupId());
        dto.setState(consumerGroup.getState());
        dto.setTotalLag(consumerGroup.getLagTotal());
        dto.setLastMeasured(consumerGroup.getUpdatedAt());
        dto.setLagThreshold(DEFAULT_LAG_THRESHOLD);
        dto.setLagThresholdExceeded(consumerGroup.getLagTotal() > DEFAULT_LAG_THRESHOLD);
        
        // Simulate partition lag details
        List<ConsumerGroupLagMonitoringDto.PartitionLagDto> partitionLags = new ArrayList<>();
        
        // In a real implementation, this would come from Kafka
        for (int i = 0; i < 3; i++) {
            ConsumerGroupLagMonitoringDto.PartitionLagDto partitionLag = 
                new ConsumerGroupLagMonitoringDto.PartitionLagDto();
            partitionLag.setTopic("user-events");
            partitionLag.setPartition(i);
            partitionLag.setCurrentOffset(10000L + i * 1000);
            partitionLag.setLogEndOffset(partitionLag.getCurrentOffset() + (consumerGroup.getLagTotal() / 3));
            partitionLag.setLag(partitionLag.getLogEndOffset() - partitionLag.getCurrentOffset());
            partitionLag.setMemberId("consumer-" + i);
            partitionLag.setClientId("client-" + i);
            partitionLag.setHost("host-" + i);
            
            partitionLags.add(partitionLag);
        }
        
        dto.setPartitionLags(partitionLags);
        dto.setPartitionsWithLag((int) partitionLags.stream().mapToLong(p -> p.getLag() > 0 ? 1 : 0).sum());
        dto.setMaxLag(partitionLags.stream().mapToLong(ConsumerGroupLagMonitoringDto.PartitionLagDto::getLag).max().orElse(0L));
        dto.setAverageLag(partitionLags.stream().mapToLong(ConsumerGroupLagMonitoringDto.PartitionLagDto::getLag).average().orElse(0.0));
        
        return dto;
    }

    private KafkaConsumerGroupDto convertToDto(KafkaConsumerGroup consumerGroup) {
        KafkaConsumerGroupDto dto = new KafkaConsumerGroupDto();
        
        dto.setId(consumerGroup.getId());
        dto.setTenantId(consumerGroup.getTenantId());
        dto.setClusterId(consumerGroup.getClusterId());
        dto.setGroupId(consumerGroup.getGroupId());
        dto.setState(consumerGroup.getState());
        dto.setProtocol(consumerGroup.getProtocol());
        dto.setProtocolType(consumerGroup.getProtocolType());
        dto.setMemberCount(consumerGroup.getMemberCount());
        dto.setLagTotal(consumerGroup.getLagTotal());
        dto.setAssignmentStrategy(consumerGroup.getAssignmentStrategy());
        dto.setCoordinatorId(consumerGroup.getCoordinatorId());
        dto.setDescription(consumerGroup.getDescription());
        dto.setMetadata(consumerGroup.getMetadata());
        dto.setCreatedAt(consumerGroup.getCreatedAt());
        dto.setUpdatedAt(consumerGroup.getUpdatedAt());
        dto.setCreatedBy(consumerGroup.getCreatedBy());
        dto.setUpdatedBy(consumerGroup.getUpdatedBy());
        dto.setVersion(consumerGroup.getVersion());
        
        return dto;
    }
}