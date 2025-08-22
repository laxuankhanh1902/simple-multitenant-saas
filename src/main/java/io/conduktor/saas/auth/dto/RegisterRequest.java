package io.conduktor.saas.auth.dto;

import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    // Tenant Information
    @NotBlank(message = "Organization name is required")
    @Size(max = 100, message = "Organization name must not exceed 100 characters")
    private String organizationName;

    @NotBlank(message = "Subdomain is required")
    @Size(max = 50, message = "Subdomain must not exceed 50 characters")
    @Pattern(regexp = "^[a-z0-9]([a-z0-9-]*[a-z0-9])?$", 
             message = "Subdomain must contain only lowercase letters, numbers, and hyphens")
    private String subdomain;

    // User Information
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String phone;
    private String timezone = "UTC";
    private String locale = "en_US";

    public RegisterRequest() {}

    // Getters and Setters
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public Tenant toTenantEntity() {
        Tenant tenant = new Tenant();
        tenant.setName(this.organizationName);
        tenant.setSubdomain(this.subdomain);
        tenant.setAdminEmail(this.email);
        tenant.setAdminFirstName(this.firstName);
        tenant.setAdminLastName(this.lastName);
        tenant.setPhone(this.phone);
        tenant.setStatus(Tenant.TenantStatus.TRIAL);
        return tenant;
    }

    public User toUserEntity(String tenantId) {
        User user = new User(tenantId);
        user.setUsername(this.email); // Use email as username
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setPhone(this.phone);
        user.setTimezone(this.timezone);
        user.setLocale(this.locale);
        user.setStatus(User.UserStatus.PENDING_VERIFICATION);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.getRoles().add(User.Role.TENANT_ADMIN);
        return user;
    }
}