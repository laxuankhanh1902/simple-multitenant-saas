package io.conduktor.saas.tenant.dto;

import io.conduktor.saas.tenant.entity.Tenant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateTenantRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Subdomain is required")
    @Size(max = 50, message = "Subdomain must not exceed 50 characters")
    @Pattern(regexp = "^[a-z0-9]([a-z0-9-]*[a-z0-9])?$", 
             message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
    private String subdomain;

    private String description;

    @NotBlank(message = "Admin email is required")
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
    private Tenant.TenantStatus status = Tenant.TenantStatus.TRIAL;
    private LocalDateTime trialEndDate;
    private Integer maxUsers = 10;
    private Integer storageLimitGb = 100;
    private Integer apiRateLimit = 1000;

    public CreateTenantRequest() {}

    // Getters and Setters
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

    public Tenant toEntity() {
        Tenant tenant = new Tenant();
        tenant.setName(this.name);
        tenant.setSubdomain(this.subdomain);
        tenant.setDescription(this.description);
        tenant.setAdminEmail(this.adminEmail);
        tenant.setAdminFirstName(this.adminFirstName);
        tenant.setAdminLastName(this.adminLastName);
        tenant.setPhone(this.phone);
        tenant.setAddress(this.address);
        tenant.setCity(this.city);
        tenant.setState(this.state);
        tenant.setCountry(this.country);
        tenant.setPostalCode(this.postalCode);
        tenant.setStatus(this.status);
        tenant.setTrialEndDate(this.trialEndDate);
        tenant.setMaxUsers(this.maxUsers);
        tenant.setStorageLimitGb(this.storageLimitGb);
        tenant.setApiRateLimit(this.apiRateLimit);
        return tenant;
    }
}