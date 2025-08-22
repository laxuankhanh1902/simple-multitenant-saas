package io.conduktor.saas.tenant.dto;

import io.conduktor.saas.tenant.entity.Tenant;

import java.time.LocalDateTime;

public class TenantDTO {

    private Long id;
    private String name;
    private String subdomain;
    private String description;
    private String adminEmail;
    private String adminFirstName;
    private String adminLastName;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Tenant.TenantStatus status;
    private LocalDateTime trialEndDate;
    private Integer maxUsers;
    private Integer storageLimitGb;
    private Integer apiRateLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public TenantDTO() {}

    public TenantDTO(Tenant tenant) {
        this.id = tenant.getId();
        this.name = tenant.getName();
        this.subdomain = tenant.getSubdomain();
        this.description = tenant.getDescription();
        this.adminEmail = tenant.getAdminEmail();
        this.adminFirstName = tenant.getAdminFirstName();
        this.adminLastName = tenant.getAdminLastName();
        this.phone = tenant.getPhone();
        this.address = tenant.getAddress();
        this.city = tenant.getCity();
        this.state = tenant.getState();
        this.country = tenant.getCountry();
        this.postalCode = tenant.getPostalCode();
        this.status = tenant.getStatus();
        this.trialEndDate = tenant.getTrialEndDate();
        this.maxUsers = tenant.getMaxUsers();
        this.storageLimitGb = tenant.getStorageLimitGb();
        this.apiRateLimit = tenant.getApiRateLimit();
        this.createdAt = tenant.getCreatedAt();
        this.updatedAt = tenant.getUpdatedAt();
        this.createdBy = tenant.getCreatedBy();
        this.updatedBy = tenant.getUpdatedBy();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAdminFirstName() { return adminFirstName; }
    public void setAdminFirstName(String adminFirstName) { this.adminFirstName = adminFirstName; }

    public String getAdminLastName() { return adminLastName; }
    public void setAdminLastName(String adminLastName) { this.adminLastName = adminLastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Tenant.TenantStatus getStatus() { return status; }
    public void setStatus(Tenant.TenantStatus status) { this.status = status; }

    public LocalDateTime getTrialEndDate() { return trialEndDate; }
    public void setTrialEndDate(LocalDateTime trialEndDate) { this.trialEndDate = trialEndDate; }

    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }

    public Integer getStorageLimitGb() { return storageLimitGb; }
    public void setStorageLimitGb(Integer storageLimitGb) { this.storageLimitGb = storageLimitGb; }

    public Integer getApiRateLimit() { return apiRateLimit; }
    public void setApiRateLimit(Integer apiRateLimit) { this.apiRateLimit = apiRateLimit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public String getAdminFullName() {
        if (adminFirstName != null && adminLastName != null) {
            return adminFirstName + " " + adminLastName;
        } else if (adminFirstName != null) {
            return adminFirstName;
        } else if (adminLastName != null) {
            return adminLastName;
        }
        return adminEmail;
    }

    public boolean isTrialExpired() {
        return status == Tenant.TenantStatus.TRIAL && 
               trialEndDate != null && 
               trialEndDate.isBefore(LocalDateTime.now());
    }

    public boolean isTrialExpiringSoon(int days) {
        return status == Tenant.TenantStatus.TRIAL && 
               trialEndDate != null && 
               trialEndDate.isBefore(LocalDateTime.now().plusDays(days));
    }
}