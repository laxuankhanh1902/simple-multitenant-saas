package io.conduktor.saas.user.repository;

import io.conduktor.saas.core.repository.BaseRepository;
import io.conduktor.saas.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByTenantIdAndUsername(String tenantId, String username);
    
    Optional<User> findByTenantIdAndEmail(String tenantId, String email);
    
    List<User> findByTenantIdAndStatus(String tenantId, User.UserStatus status);
    
    Page<User> findByTenantIdAndStatus(String tenantId, User.UserStatus status, Pageable pageable);
    
    List<User> findByTenantIdAndRolesContaining(String tenantId, User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND " +
           "(u.username LIKE %:search% OR u.email LIKE %:search% OR u.firstName LIKE %:search% OR u.lastName LIKE %:search%)")
    Page<User> findByTenantIdAndSearchTerm(@Param("tenantId") String tenantId, @Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.lastLogin >= :since")
    List<User> findActiveUsersSince(@Param("tenantId") String tenantId, @Param("since") LocalDateTime since);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.enabled = false")
    List<User> findDisabledUsers(@Param("tenantId") String tenantId);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.accountLockedUntil > :now")
    List<User> findLockedUsers(@Param("tenantId") String tenantId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") User.UserStatus status);
    
    boolean existsByTenantIdAndUsername(String tenantId, String username);
    
    boolean existsByTenantIdAndEmail(String tenantId, String email);
    
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND " +
           "(u.username LIKE %:query% OR u.email LIKE %:query% OR u.firstName LIKE %:query% OR u.lastName LIKE %:query%)")
    List<User> searchByTenantIdAndQuery(@Param("tenantId") String tenantId, @Param("query") String query);
    
    Page<User> findByTenantIdAndStatusAndEnabled(String tenantId, User.UserStatus status, boolean enabled, Pageable pageable);
    
    List<User> findByTenantIdAndCreatedAtAfter(String tenantId, LocalDateTime createdAt);
}