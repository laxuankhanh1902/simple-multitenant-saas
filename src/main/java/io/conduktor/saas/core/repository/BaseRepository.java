package io.conduktor.saas.core.repository;

import io.conduktor.saas.core.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    List<T> findByTenantId(String tenantId);
    
    Page<T> findByTenantId(String tenantId, Pageable pageable);
    
    Optional<T> findByIdAndTenantId(ID id, String tenantId);
    
    List<T> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.tenantId = :tenantId AND e.createdAt >= :since")
    List<T> findRecentByTenantId(@Param("tenantId") String tenantId, @Param("since") LocalDateTime since);
    
    void deleteByIdAndTenantId(ID id, String tenantId);
    
    boolean existsByIdAndTenantId(ID id, String tenantId);
}