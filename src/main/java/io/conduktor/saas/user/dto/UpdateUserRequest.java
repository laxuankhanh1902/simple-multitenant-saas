package io.conduktor.saas.user.dto;

import io.conduktor.saas.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UpdateUserRequest {

    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    private String phone;
    private String avatarUrl;
    private String timezone;
    private String locale;
    private User.UserStatus status;
    private Boolean enabled;
    private Boolean emailVerified;
    private Set<User.Role> roles;

    public UpdateUserRequest() {}

    // Getters and Setters
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

    public Boolean isEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Set<User.Role> getRoles() { return roles; }
    public void setRoles(Set<User.Role> roles) { this.roles = roles; }

    public void updateEntity(User user) {
        if (this.username != null) user.setUsername(this.username);
        if (this.email != null) user.setEmail(this.email);
        if (this.firstName != null) user.setFirstName(this.firstName);
        if (this.lastName != null) user.setLastName(this.lastName);
        if (this.phone != null) user.setPhone(this.phone);
        if (this.avatarUrl != null) user.setAvatarUrl(this.avatarUrl);
        if (this.timezone != null) user.setTimezone(this.timezone);
        if (this.locale != null) user.setLocale(this.locale);
        if (this.status != null) user.setStatus(this.status);
        if (this.enabled != null) user.setEnabled(this.enabled);
        if (this.emailVerified != null) user.setEmailVerified(this.emailVerified);
        if (this.roles != null) user.setRoles(this.roles);
    }
}