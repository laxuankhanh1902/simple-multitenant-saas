package io.conduktor.saas.core.service;

import io.conduktor.saas.core.entity.BaseEntity;
import io.conduktor.saas.core.exception.ResourceNotFoundException;
import io.conduktor.saas.core.repository.BaseRepository;
import io.conduktor.saas.security.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends BaseEntity, ID> {

    protected final BaseRepository<T, ID> repository;
    protected final String entityName;

    protected BaseService(BaseRepository<T, ID> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    // @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findByTenantId(getCurrentTenantId());
    }

    // @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findByTenantId(getCurrentTenantId(), pageable);
    }

    // @Transactional(readOnly = true)
    public Optional<T> findByIdOptional(ID id) {
        return repository.findByIdAndTenantId(id, getCurrentTenantId());
    }

    // @Transactional(readOnly = true)
    public T findById(ID id) {
        return findByIdOptional(id).orElseThrow(() -> 
            new ResourceNotFoundException("Entity not found with id: " + id));
    }

    public abstract Page<T> findAll(Specification<T> spec, Pageable pageable);

    public abstract List<T> findAll(Specification<T> spec);

    public abstract T create(T entity);

    public abstract T update(ID id, T entity);

    // @Transactional
    public void deleteById(ID id) {
        if (!repository.existsByIdAndTenantId(id, getCurrentTenantId())) {
            throw new ResourceNotFoundException(entityName + " not found with id: " + id);
        }
        repository.deleteByIdAndTenantId(id, getCurrentTenantId());
    }

    // @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsByIdAndTenantId(id, getCurrentTenantId());
    }

    // @Transactional(readOnly = true)
    public long count() {
        return repository.countByTenantId(getCurrentTenantId());
    }

    // @Transactional(readOnly = true)
    public List<T> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByTenantIdAndCreatedAtBetween(getCurrentTenantId(), startDate, endDate);
    }

    // @Transactional(readOnly = true)
    public List<T> findRecent(LocalDateTime since) {
        return repository.findRecentByTenantId(getCurrentTenantId(), since);
    }

    protected String getCurrentTenantId() {
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalStateException("No tenant context available");
        }
        return tenantId;
    }

    protected void validateTenantAccess(T entity) {
        if (!getCurrentTenantId().equals(entity.getTenantId())) {
            throw new IllegalArgumentException("Access denied: Entity belongs to different tenant");
        }
    }
}