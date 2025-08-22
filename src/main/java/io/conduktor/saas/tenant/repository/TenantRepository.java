package io.conduktor.saas.tenant.repository;

import io.conduktor.saas.core.repository.BaseRepository;
import io.conduktor.saas.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends BaseRepository<Tenant, Long> {

    Optional<Tenant> findByName(String name);
    
    Optional<Tenant> findBySubdomain(String subdomain);
    
    List<Tenant> findByStatus(Tenant.TenantStatus status);
    
    Page<Tenant> findByStatus(Tenant.TenantStatus status, Pageable pageable);
    
    List<Tenant> findByTrialEndDateBefore(LocalDateTime date);
    
    @Query("SELECT t FROM Tenant t WHERE t.name LIKE %:search% OR t.subdomain LIKE %:search% OR t.adminEmail LIKE %:search%")
    Page<Tenant> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.status = :status")
    long countByStatus(@Param("status") Tenant.TenantStatus status);
    
    @Query("SELECT t FROM Tenant t WHERE t.createdAt >= :since")
    List<Tenant> findTenantsCreatedSince(@Param("since") LocalDateTime since);
    
    boolean existsByName(String name);
    
    boolean existsBySubdomain(String subdomain);
    
    List<Tenant> findByStatusAndTrialEndDateBefore(Tenant.TenantStatus status, LocalDateTime date);
    
    @Query("SELECT t FROM Tenant t WHERE t.name LIKE %:query% OR t.subdomain LIKE %:query% OR t.adminEmail LIKE %:query%")
    List<Tenant> searchByQuery(@Param("query") String query);
    
    List<Tenant> findByCreatedAtAfter(LocalDateTime since);
}