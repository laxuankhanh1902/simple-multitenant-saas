package io.conduktor.saas.kafka.cluster.repository;

import io.conduktor.saas.core.repository.BaseRepository;
import io.conduktor.saas.kafka.cluster.entity.KafkaCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KafkaClusterRepository extends BaseRepository<KafkaCluster, Long> {

    @Query("SELECT k FROM KafkaCluster k WHERE k.tenantId = :tenantId")
    Page<KafkaCluster> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT k FROM KafkaCluster k WHERE k.tenantId = :tenantId")
    List<KafkaCluster> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT k FROM KafkaCluster k WHERE k.tenantId = :tenantId AND k.id = :id")
    Optional<KafkaCluster> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") Long id);

    @Query("SELECT k FROM KafkaCluster k WHERE k.tenantId = :tenantId AND k.name = :name")
    Optional<KafkaCluster> findByTenantIdAndName(@Param("tenantId") String tenantId, @Param("name") String name);

    @Query("""
        SELECT k FROM KafkaCluster k 
        WHERE k.tenantId = :tenantId 
        AND (:status IS NULL OR k.status = :status)
        AND (:healthStatus IS NULL OR k.healthStatus = :healthStatus)
        AND (:searchTerm IS NULL OR 
             k.name LIKE %:searchTerm% OR 
             k.description LIKE %:searchTerm% OR 
             k.bootstrapServers LIKE %:searchTerm%)
        """)
    Page<KafkaCluster> findByTenantIdWithFilters(
        @Param("tenantId") String tenantId,
        @Param("status") String status,
        @Param("healthStatus") String healthStatus,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );

    @Query("SELECT k FROM KafkaCluster k WHERE k.autoHealthCheck = true AND k.status = 'ACTIVE'")
    List<KafkaCluster> findActiveWithAutoHealthCheck();

    @Query("""
        SELECT k FROM KafkaCluster k 
        WHERE k.autoHealthCheck = true 
        AND k.status = 'ACTIVE'
        AND (k.lastHealthCheck IS NULL OR k.lastHealthCheck < :threshold)
        """)
    List<KafkaCluster> findClustersNeedingHealthCheck(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(k) FROM KafkaCluster k WHERE k.tenantId = :tenantId")
    Long countClustersByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(k) FROM KafkaCluster k WHERE k.tenantId = :tenantId AND k.status = 'ACTIVE'")
    Long countActiveByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(k) FROM KafkaCluster k WHERE k.tenantId = :tenantId AND k.healthStatus = 'HEALTHY'")
    Long countHealthyByTenantId(@Param("tenantId") String tenantId);

    @Query("""
        SELECT k.healthStatus as status, COUNT(k) as count 
        FROM KafkaCluster k 
        WHERE k.tenantId = :tenantId 
        GROUP BY k.healthStatus
        """)
    List<Object[]> getHealthStatusStatsByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT DISTINCT k.connectionType FROM KafkaCluster k WHERE k.tenantId = :tenantId")
    List<String> findDistinctConnectionTypesByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT DISTINCT k.kafkaVersion FROM KafkaCluster k WHERE k.tenantId = :tenantId AND k.kafkaVersion IS NOT NULL")
    List<String> findDistinctVersionsByTenantId(@Param("tenantId") String tenantId);

    @Query("""
        SELECT SUM(k.topicCount) 
        FROM KafkaCluster k 
        WHERE k.tenantId = :tenantId 
        AND k.topicCount IS NOT NULL
        """)
    Long getTotalTopicCountByTenantId(@Param("tenantId") String tenantId);

    @Query("""
        SELECT SUM(k.partitionCount) 
        FROM KafkaCluster k 
        WHERE k.tenantId = :tenantId 
        AND k.partitionCount IS NOT NULL
        """)
    Long getTotalPartitionCountByTenantId(@Param("tenantId") String tenantId);

    @Query("""
        SELECT SUM(k.consumerGroupCount) 
        FROM KafkaCluster k 
        WHERE k.tenantId = :tenantId 
        AND k.consumerGroupCount IS NOT NULL
        """)
    Long getTotalConsumerGroupCountByTenantId(@Param("tenantId") String tenantId);
}