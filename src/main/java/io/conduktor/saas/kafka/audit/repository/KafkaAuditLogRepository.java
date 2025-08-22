package io.conduktor.saas.kafka.audit.repository;

import io.conduktor.saas.core.repository.BaseRepository;
import io.conduktor.saas.kafka.audit.entity.KafkaAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface KafkaAuditLogRepository extends BaseRepository<KafkaAuditLog, Long> {

    @Query("SELECT k FROM KafkaAuditLog k WHERE k.tenantId = :tenantId")
    Page<KafkaAuditLog> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("""
        SELECT k FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND (:userEmail IS NULL OR k.userEmail LIKE %:userEmail%)
        AND (:action IS NULL OR k.action = :action)
        AND (:resourceType IS NULL OR k.resourceType = :resourceType)
        AND (:resourceName IS NULL OR k.resourceName LIKE %:resourceName%)
        AND (:clusterId IS NULL OR k.clusterId = :clusterId)
        AND (:clusterName IS NULL OR k.clusterName LIKE %:clusterName%)
        AND (:topicName IS NULL OR k.topicName LIKE %:topicName%)
        AND (:consumerGroup IS NULL OR k.consumerGroup LIKE %:consumerGroup%)
        AND (:status IS NULL OR k.status = :status)
        AND (:ipAddress IS NULL OR k.ipAddress = :ipAddress)
        AND (:dateFrom IS NULL OR k.timestamp >= :dateFrom)
        AND (:dateTo IS NULL OR k.timestamp <= :dateTo)
        AND (:searchTerm IS NULL OR 
             k.userEmail LIKE %:searchTerm% OR 
             k.action LIKE %:searchTerm% OR 
             k.resourceName LIKE %:searchTerm% OR 
             k.clusterName LIKE %:searchTerm% OR 
             k.topicName LIKE %:searchTerm% OR 
             k.consumerGroup LIKE %:searchTerm%)
        """)
    Page<KafkaAuditLog> findByTenantIdWithFilters(
        @Param("tenantId") String tenantId,
        @Param("userEmail") String userEmail,
        @Param("action") String action,
        @Param("resourceType") String resourceType,
        @Param("resourceName") String resourceName,
        @Param("clusterId") Long clusterId,
        @Param("clusterName") String clusterName,
        @Param("topicName") String topicName,
        @Param("consumerGroup") String consumerGroup,
        @Param("status") String status,
        @Param("ipAddress") String ipAddress,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );

    @Query("""
        SELECT k FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND k.action IN :actions
        """)
    Page<KafkaAuditLog> findByTenantIdAndActionIn(
        @Param("tenantId") String tenantId,
        @Param("actions") List<String> actions,
        Pageable pageable
    );

    @Query("SELECT DISTINCT k.action FROM KafkaAuditLog k WHERE k.tenantId = :tenantId ORDER BY k.action")
    List<String> findDistinctActionsByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT DISTINCT k.userEmail FROM KafkaAuditLog k WHERE k.tenantId = :tenantId ORDER BY k.userEmail")
    List<String> findDistinctUserEmailsByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT DISTINCT k.clusterName FROM KafkaAuditLog k WHERE k.tenantId = :tenantId AND k.clusterName IS NOT NULL ORDER BY k.clusterName")
    List<String> findDistinctClusterNamesByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT DISTINCT k.topicName FROM KafkaAuditLog k WHERE k.tenantId = :tenantId AND k.topicName IS NOT NULL ORDER BY k.topicName")
    List<String> findDistinctTopicNamesByTenantId(@Param("tenantId") String tenantId);

    @Query("""
        SELECT k.action as action, COUNT(k) as count 
        FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND (:dateFrom IS NULL OR k.timestamp >= :dateFrom)
        AND (:dateTo IS NULL OR k.timestamp <= :dateTo)
        GROUP BY k.action 
        ORDER BY count DESC
        """)
    List<Map<String, Object>> getActionStatsByTenantId(
        @Param("tenantId") String tenantId,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    @Query("""
        SELECT DATE(k.timestamp) as date, COUNT(k) as count 
        FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND k.timestamp >= :dateFrom
        AND k.timestamp <= :dateTo
        GROUP BY DATE(k.timestamp) 
        ORDER BY date
        """)
    List<Map<String, Object>> getDailyActivityByTenantId(
        @Param("tenantId") String tenantId,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    @Query("""
        SELECT COUNT(k) 
        FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND k.timestamp >= :dateFrom
        """)
    Long countByTenantIdAndTimestampAfter(
        @Param("tenantId") String tenantId,
        @Param("dateFrom") LocalDateTime dateFrom
    );

    @Query("""
        SELECT AVG(k.durationMs) 
        FROM KafkaAuditLog k 
        WHERE k.tenantId = :tenantId 
        AND k.durationMs IS NOT NULL
        AND k.timestamp >= :dateFrom
        """)
    Double getAverageDurationByTenantId(
        @Param("tenantId") String tenantId,
        @Param("dateFrom") LocalDateTime dateFrom
    );
}