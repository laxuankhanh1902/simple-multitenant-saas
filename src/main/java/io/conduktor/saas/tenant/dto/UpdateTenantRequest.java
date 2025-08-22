package io.conduktor.saas.tenant.dto;

import io.conduktor.saas.tenant.entity.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UpdateTenantRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;

    @Email(message = "Admin email must be valid")
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

    public UpdateTenantRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public void updateEntity(Tenant tenant) {
        if (this.name != null) tenant.setName(this.name);
        if (this.description != null) tenant.setDescription(this.description);
        if (this.adminEmail != null) tenant.setAdminEmail(this.adminEmail);
        if (this.adminFirstName != null) tenant.setAdminFirstName(this.adminFirstName);
        if (this.adminLastName != null) tenant.setAdminLastName(this.adminLastName);
        if (this.phone != null) tenant.setPhone(this.phone);
        if (this.address != null) tenant.setAddress(this.address);
        if (this.city != null) tenant.setCity(this.city);
        if (this.state != null) tenant.setState(this.state);
        if (this.country != null) tenant.setCountry(this.country);
        if (this.postalCode != null) tenant.setPostalCode(this.postalCode);
        if (this.status != null) tenant.setStatus(this.status);
        if (this.trialEndDate != null) tenant.setTrialEndDate(this.trialEndDate);
        if (this.maxUsers != null) tenant.setMaxUsers(this.maxUsers);
        if (this.storageLimitGb != null) tenant.setStorageLimitGb(this.storageLimitGb);
        if (this.apiRateLimit != null) tenant.setApiRateLimit(this.apiRateLimit);
    }
}