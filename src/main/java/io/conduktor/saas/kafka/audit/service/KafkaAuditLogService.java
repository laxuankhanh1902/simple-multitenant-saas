package io.conduktor.saas.kafka.audit.service;

import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.core.service.BaseService;
import io.conduktor.saas.kafka.audit.dto.KafkaAuditLogDto;
import io.conduktor.saas.kafka.audit.dto.KafkaAuditLogFilterDto;
import io.conduktor.saas.kafka.audit.entity.KafkaAuditLog;
import io.conduktor.saas.kafka.audit.repository.KafkaAuditLogRepository;
import io.conduktor.saas.security.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
// @Transactional
public class KafkaAuditLogService {

    private final KafkaAuditLogRepository kafkaAuditLogRepository;

    public KafkaAuditLogService(KafkaAuditLogRepository kafkaAuditLogRepository) {
        this.kafkaAuditLogRepository = kafkaAuditLogRepository;
    }

    public PageResponse<KafkaAuditLogDto> findAuditLogs(KafkaAuditLogFilterDto filter) {
        String tenantId = TenantContext.getCurrentTenant();
        
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(filter.getDirection()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSort()
        );
        
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        
        Page<KafkaAuditLog> auditLogs;
        
        if (filter.getActions() != null && !filter.getActions().isEmpty()) {
            auditLogs = kafkaAuditLogRepository.findByTenantIdAndActionIn(
                tenantId, filter.getActions(), pageable
            );
        } else {
            auditLogs = kafkaAuditLogRepository.findByTenantIdWithFilters(
                tenantId,
                filter.getUserEmail(),
                filter.getAction(),
                filter.getResourceType(),
                filter.getResourceName(),
                filter.getClusterId(),
                filter.getClusterName(),
                filter.getTopicName(),
                filter.getConsumerGroup(),
                filter.getStatus(),
                filter.getIpAddress(),
                filter.getDateFrom(),
                filter.getDateTo(),
                filter.getSearchTerm(),
                pageable
            );
        }
        
        List<KafkaAuditLogDto> dtos = auditLogs.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return new PageResponse<>(
            dtos,
            auditLogs.getNumber(),
            auditLogs.getSize(),
            auditLogs.getTotalElements(),
            auditLogs.getTotalPages(),
            auditLogs.isFirst(),
            auditLogs.isLast()
        );
    }

    public KafkaAuditLogDto findById(Long id) {
        String tenantId = TenantContext.getCurrentTenant();
        KafkaAuditLog auditLog = kafkaAuditLogRepository.findById(id)
            .filter(log -> tenantId.equals(log.getTenantId()))
            .orElseThrow(() -> new RuntimeException("Audit log not found"));
        
        return convertToDto(auditLog);
    }

    public List<String> getAvailableActions() {
        String tenantId = TenantContext.getCurrentTenant();
        return kafkaAuditLogRepository.findDistinctActionsByTenantId(tenantId);
    }

    public List<String> getAvailableUsers() {
        String tenantId = TenantContext.getCurrentTenant();
        return kafkaAuditLogRepository.findDistinctUserEmailsByTenantId(tenantId);
    }

    public List<String> getAvailableClusters() {
        String tenantId = TenantContext.getCurrentTenant();
        return kafkaAuditLogRepository.findDistinctClusterNamesByTenantId(tenantId);
    }

    public List<String> getAvailableTopics() {
        String tenantId = TenantContext.getCurrentTenant();
        return kafkaAuditLogRepository.findDistinctTopicNamesByTenantId(tenantId);
    }

    public Map<String, Object> getAuditStatistics(LocalDateTime dateFrom, LocalDateTime dateTo) {
        String tenantId = TenantContext.getCurrentTenant();
        
        if (dateFrom == null) {
            dateFrom = LocalDateTime.now().minusDays(30);
        }
        if (dateTo == null) {
            dateTo = LocalDateTime.now();
        }
        
        List<Map<String, Object>> actionStats = kafkaAuditLogRepository.getActionStatsByTenantId(
            tenantId, dateFrom, dateTo
        );
        
        List<Map<String, Object>> dailyActivity = kafkaAuditLogRepository.getDailyActivityByTenantId(
            tenantId, dateFrom, dateTo
        );
        
        Long totalCount = kafkaAuditLogRepository.countByTenantIdAndTimestampAfter(
            tenantId, dateFrom
        );
        
        Double averageDuration = kafkaAuditLogRepository.getAverageDurationByTenantId(
            tenantId, dateFrom
        );
        
        return Map.of(
            "actionStats", actionStats,
            "dailyActivity", dailyActivity,
            "totalCount", totalCount != null ? totalCount : 0L,
            "averageDuration", averageDuration != null ? averageDuration : 0.0,
            "period", Map.of("from", dateFrom, "to", dateTo)
        );
    }

    public KafkaAuditLog logAuditEvent(String action, String resourceType, String resourceName, 
                                      Map<String, Object> details) {
        String tenantId = TenantContext.getCurrentTenant();
        
        KafkaAuditLog auditLog = new KafkaAuditLog();
        auditLog.setTenantId(tenantId);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceName(resourceName);
        auditLog.setDetails(details);
        auditLog.setStatus("SUCCESS");
        
        // TODO: Get current user from security context
        auditLog.setUserEmail("system@example.com");
        
        return kafkaAuditLogRepository.save(auditLog);
    }

    private KafkaAuditLogDto convertToDto(KafkaAuditLog auditLog) {
        KafkaAuditLogDto dto = new KafkaAuditLogDto();
        BeanUtils.copyProperties(auditLog, dto);
        return dto;
    }

    private KafkaAuditLog convertToEntity(KafkaAuditLogDto dto) {
        KafkaAuditLog entity = new KafkaAuditLog();
        BeanUtils.copyProperties(dto, entity);
        entity.setTenantId(TenantContext.getCurrentTenant());
        return entity;
    }
}