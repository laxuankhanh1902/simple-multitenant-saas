package io.conduktor.saas.tenant.entity;

import io.conduktor.saas.core.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"}),
    @UniqueConstraint(columnNames = {"subdomain"})
})
public class Tenant extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "subdomain", nullable = false)
    private String subdomain;

    @Column(name = "description")
    private String description;

    @NotBlank
    @Email
    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    @Column(name = "admin_first_name")
    private String adminFirstName;

    @Column(name = "admin_last_name")
    private String adminLastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "max_users")
    private Integer maxUsers = 10;

    @Column(name = "storage_limit_gb")
    private Integer storageLimitGb = 100;

    @Column(name = "api_rate_limit")
    private Integer apiRateLimit = 1000;

    public Tenant() {
        super();
    }

    public Tenant(String tenantId) {
        super(tenantId);
    }

    public enum TenantStatus {
        ACTIVE,
        SUSPENDED,
        TRIAL,
        CANCELED
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public void setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public LocalDateTime getTrialEndDate() {
        return trialEndDate;
    }

    public void setTrialEndDate(LocalDateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getStorageLimitGb() {
        return storageLimitGb;
    }

    public void setStorageLimitGb(Integer storageLimitGb) {
        this.storageLimitGb = storageLimitGb;
    }

    public Integer getApiRateLimit() {
        return apiRateLimit;
    }

    public void setApiRateLimit(Integer apiRateLimit) {
        this.apiRateLimit = apiRateLimit;
    }
}