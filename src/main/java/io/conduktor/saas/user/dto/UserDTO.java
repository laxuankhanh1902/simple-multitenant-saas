package io.conduktor.saas.user.dto;

import io.conduktor.saas.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDTO {

    private Long id;
    
    @NotBlank
    @Size(max = 50)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    private String email;
    
    @Size(max = 50)
    private String firstName;
    
    @Size(max = 50)
    private String lastName;
    
    private String phone;
    private String avatarUrl;
    private String timezone = "UTC";
    private String locale = "en_US";
    private User.UserStatus status = User.UserStatus.ACTIVE;
    private boolean enabled = true;
    private boolean emailVerified = false;
    private LocalDateTime lastLogin;
    private Long loginCount = 0L;
    private Integer failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil;
    private Set<User.Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.avatarUrl = user.getAvatarUrl();
        this.timezone = user.getTimezone();
        this.locale = user.getLocale();
        this.status = user.getStatus();
        this.enabled = user.isEnabled();
        this.emailVerified = user.isEmailVerified();
        this.lastLogin = user.getLastLogin();
        this.loginCount = user.getLoginCount();
        this.failedLoginAttempts = user.getFailedLoginAttempts();
        this.accountLockedUntil = user.getAccountLockedUntil();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.createdBy = user.getCreatedBy();
        this.updatedBy = user.getUpdatedBy();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public User.UserStatus getStatus() { return status; }
    public void setStatus(User.UserStatus status) { this.status = status; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Long getLoginCount() { return loginCount; }
    public void setLoginCount(Long loginCount) { this.loginCount = loginCount; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }

    public Set<User.Role> getRoles() { return roles; }
    public void setRoles(Set<User.Role> roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }

    public boolean hasRole(User.Role role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(User.Role.ADMIN);
    }

    public boolean isTenantAdmin() {
        return hasRole(User.Role.TENANT_ADMIN);
    }

    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || LocalDateTime.now().isAfter(accountLockedUntil);
    }
}