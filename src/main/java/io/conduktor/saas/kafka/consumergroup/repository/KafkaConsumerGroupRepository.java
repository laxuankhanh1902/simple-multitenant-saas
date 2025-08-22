package io.conduktor.saas.kafka.consumergroup.repository;

import io.conduktor.saas.kafka.consumergroup.entity.KafkaConsumerGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KafkaConsumerGroupRepository extends JpaRepository<KafkaConsumerGroup, Long> {

    Optional<KafkaConsumerGroup> findByTenantIdAndClusterIdAndGroupId(
        String tenantId, Long clusterId, String groupId);

    Page<KafkaConsumerGroup> findByTenantIdAndClusterId(
        String tenantId, Long clusterId, Pageable pageable);

    List<KafkaConsumerGroup> findByTenantIdAndClusterId(String tenantId, Long clusterId);

    @Query("SELECT cg FROM KafkaConsumerGroup cg WHERE cg.tenantId = :tenantId AND cg.clusterId = :clusterId AND cg.state = :state")
    List<KafkaConsumerGroup> findByTenantIdAndClusterIdAndState(
        @Param("tenantId") String tenantId, 
        @Param("clusterId") Long clusterId, 
        @Param("state") String state);

    @Query("SELECT cg FROM KafkaConsumerGroup cg WHERE cg.tenantId = :tenantId AND cg.clusterId = :clusterId AND cg.lagTotal > :lagThreshold")
    List<KafkaConsumerGroup> findConsumerGroupsWithHighLag(
        @Param("tenantId") String tenantId, 
        @Param("clusterId") Long clusterId, 
        @Param("lagThreshold") Long lagThreshold);

    @Query("SELECT SUM(cg.lagTotal) FROM KafkaConsumerGroup cg WHERE cg.tenantId = :tenantId AND cg.clusterId = :clusterId")
    Long getTotalLagByCluster(@Param("tenantId") String tenantId, @Param("clusterId") Long clusterId);

    @Query("SELECT COUNT(cg) FROM KafkaConsumerGroup cg WHERE cg.tenantId = :tenantId AND cg.clusterId = :clusterId AND cg.state = 'STABLE'")
    Long countActiveConsumerGroups(@Param("tenantId") String tenantId, @Param("clusterId") Long clusterId);

    @Query("SELECT cg FROM KafkaConsumerGroup cg WHERE cg.tenantId = :tenantId AND cg.groupId LIKE %:groupIdPattern%")
    Page<KafkaConsumerGroup> findByTenantIdAndGroupIdContaining(
        @Param("tenantId") String tenantId, 
        @Param("groupIdPattern") String groupIdPattern, 
        Pageable pageable);

    List<KafkaConsumerGroup> findByTenantId(String tenantId);

    void deleteByTenantIdAndClusterId(String tenantId, Long clusterId);
}