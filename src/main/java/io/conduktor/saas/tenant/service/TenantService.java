package io.conduktor.saas.tenant.service;

import io.conduktor.saas.core.service.BaseService;
import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.repository.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
// @Transactional
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // @Transactional(readOnly = true)
    public Page<Tenant> findAll(Specification<Tenant> spec, Pageable pageable) {
        return tenantRepository.findAll(spec, pageable);
    }

    // @Transactional(readOnly = true)
    public List<Tenant> findAll(Specification<Tenant> spec) {
        return tenantRepository.findAll(spec);
    }
    
    // @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
    
    // @Transactional(readOnly = true)
    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
    }
    
    // @Transactional
    public void deleteById(Long id) {
        tenantRepository.deleteById(id);
    }

    // @Transactional(readOnly = true)
    public Optional<Tenant> findBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain);
    }

    // @Transactional(readOnly = true)
    public Optional<Tenant> findByName(String name) {
        return tenantRepository.findByName(name);
    }

    // @Transactional(readOnly = true)
    public boolean existsBySubdomain(String subdomain) {
        return tenantRepository.existsBySubdomain(subdomain);
    }

    // @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tenantRepository.existsByName(name);
    }

    // @Transactional(readOnly = true)
    public List<Tenant> findByStatus(Tenant.TenantStatus status) {
        return tenantRepository.findByStatus(status);
    }

    // @Transactional(readOnly = true)
    public List<Tenant> findTrialTenants() {
        return tenantRepository.findByStatus(Tenant.TenantStatus.TRIAL);
    }

    // @Transactional(readOnly = true)
    public List<Tenant> findExpiredTrials() {
        LocalDateTime now = LocalDateTime.now();
        return tenantRepository.findByStatusAndTrialEndDateBefore(Tenant.TenantStatus.TRIAL, now);
    }

    // @Transactional(readOnly = true)
    public List<Tenant> findExpiringTrials(int days) {
        LocalDateTime cutoff = LocalDateTime.now().plusDays(days);
        return tenantRepository.findByStatusAndTrialEndDateBefore(Tenant.TenantStatus.TRIAL, cutoff);
    }

    // @Transactional(readOnly = true)
    public long countByStatus(Tenant.TenantStatus status) {
        return tenantRepository.countByStatus(status);
    }

    // @Transactional(readOnly = true)
    public long countActiveTenants() {
        return tenantRepository.countByStatus(Tenant.TenantStatus.ACTIVE);
    }

    public Tenant create(Tenant tenant) {
        // Generate tenant ID based on subdomain
        tenant.setTenantId(tenant.getSubdomain());
        
        // Set default trial period if status is TRIAL
        if (tenant.getStatus() == Tenant.TenantStatus.TRIAL && tenant.getTrialEndDate() == null) {
            tenant.setTrialEndDate(LocalDateTime.now().plusDays(30));
        }
        
        return tenantRepository.save(tenant);
    }

    public Tenant createWithTrial(Tenant tenant, int trialDays) {
        tenant.setStatus(Tenant.TenantStatus.TRIAL);
        tenant.setTrialEndDate(LocalDateTime.now().plusDays(trialDays));
        return create(tenant);
    }

    public Tenant update(Long id, Tenant updatedTenant) {
        Tenant existingTenant = findById(id);
        
        existingTenant.setName(updatedTenant.getName());
        existingTenant.setDescription(updatedTenant.getDescription());
        existingTenant.setAdminEmail(updatedTenant.getAdminEmail());
        existingTenant.setAdminFirstName(updatedTenant.getAdminFirstName());
        existingTenant.setAdminLastName(updatedTenant.getAdminLastName());
        existingTenant.setPhone(updatedTenant.getPhone());
        existingTenant.setAddress(updatedTenant.getAddress());
        existingTenant.setCity(updatedTenant.getCity());
        existingTenant.setState(updatedTenant.getState());
        existingTenant.setCountry(updatedTenant.getCountry());
        existingTenant.setPostalCode(updatedTenant.getPostalCode());
        existingTenant.setStatus(updatedTenant.getStatus());
        existingTenant.setTrialEndDate(updatedTenant.getTrialEndDate());
        existingTenant.setMaxUsers(updatedTenant.getMaxUsers());
        existingTenant.setStorageLimitGb(updatedTenant.getStorageLimitGb());
        existingTenant.setApiRateLimit(updatedTenant.getApiRateLimit());

        return tenantRepository.save(existingTenant);
    }

    public Tenant updateStatus(Long id, Tenant.TenantStatus status) {
        Tenant tenant = findById(id);
        tenant.setStatus(status);
        
        // Clear trial end date if no longer trial
        if (status != Tenant.TenantStatus.TRIAL) {
            tenant.setTrialEndDate(null);
        }
        
        return tenantRepository.save(tenant);
    }

    public Tenant extendTrial(Long id, int additionalDays) {
        Tenant tenant = findById(id);
        
        if (tenant.getStatus() != Tenant.TenantStatus.TRIAL) {
            throw new IllegalStateException("Tenant is not in trial status");
        }
        
        LocalDateTime currentEndDate = tenant.getTrialEndDate() != null 
            ? tenant.getTrialEndDate() 
            : LocalDateTime.now();
        
        tenant.setTrialEndDate(currentEndDate.plusDays(additionalDays));
        return tenantRepository.save(tenant);
    }

    public Tenant updateLimits(Long id, Integer maxUsers, Integer storageLimitGb, Integer apiRateLimit) {
        Tenant tenant = findById(id);
        
        if (maxUsers != null) {
            tenant.setMaxUsers(maxUsers);
        }
        if (storageLimitGb != null) {
            tenant.setStorageLimitGb(storageLimitGb);
        }
        if (apiRateLimit != null) {
            tenant.setApiRateLimit(apiRateLimit);
        }
        
        return tenantRepository.save(tenant);
    }

    public Tenant suspend(Long id, String reason) {
        Tenant tenant = findById(id);
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        return tenantRepository.save(tenant);
    }

    public Tenant reactivate(Long id) {
        Tenant tenant = findById(id);
        
        if (tenant.getStatus() == Tenant.TenantStatus.SUSPENDED) {
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        } else if (tenant.getStatus() == Tenant.TenantStatus.CANCELED) {
            throw new IllegalStateException("Cannot reactivate a canceled tenant");
        }
        
        return tenantRepository.save(tenant);
    }

    public List<Tenant> searchTenants(String query) {
        return tenantRepository.searchByQuery(query);
    }

    public List<Tenant> findRecentlyCreated(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return tenantRepository.findByCreatedAtAfter(since);
    }

    public void processExpiredTrials() {
        List<Tenant> expiredTrials = findExpiredTrials();
        for (Tenant tenant : expiredTrials) {
            tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
            tenantRepository.save(tenant);
        }
    }

    // @Transactional(readOnly = true)
    public boolean isSubdomainAvailable(String subdomain) {
        return !tenantRepository.existsBySubdomain(subdomain);
    }

    // @Transactional(readOnly = true)
    public boolean isNameAvailable(String name) {
        return !tenantRepository.existsByName(name);
    }
}