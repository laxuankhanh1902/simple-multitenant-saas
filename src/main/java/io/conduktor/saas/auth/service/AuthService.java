package io.conduktor.saas.auth.service;

import io.conduktor.saas.auth.dto.LoginRequest;
import io.conduktor.saas.auth.dto.LoginResponse;
import io.conduktor.saas.auth.dto.RegisterRequest;
import io.conduktor.saas.security.JwtTokenProvider;
import io.conduktor.saas.security.TenantContext;
import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.service.TenantService;
import io.conduktor.saas.user.dto.UserDTO;
import io.conduktor.saas.user.entity.User;
import io.conduktor.saas.user.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private final UserService userService;
    private final TenantService tenantService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, 
                      TenantService tenantService,
                      JwtTokenProvider jwtTokenProvider, 
                      PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tenantService = tenantService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // Set tenant context if provided
        if (loginRequest.getTenantId() != null) {
            TenantContext.setCurrentTenantId(loginRequest.getTenantId());
        }

        // Find user by username or email
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            // userService.recordFailedLogin(loginRequest.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userOpt.get();

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            throw new BadCredentialsException("Account is locked");
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        // Validate password (handle both plain text for dev and BCrypt for prod)
        boolean passwordMatches;
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$")) {
            // BCrypt encoded password
            passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        } else {
            // Plain text password (dev mode)
            passwordMatches = loginRequest.getPassword().equals(user.getPassword());
        }
        
        if (!passwordMatches) {
            // userService.recordFailedLogin(user.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        // Record successful login
//        user = userService.recordLogin(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.createToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        UserDTO userDTO = new UserDTO(user);
        
        return new LoginResponse(
            accessToken, 
            refreshToken, 
            jwtTokenProvider.getAccessTokenValidityInSeconds(), 
            userDTO,
            user.getTenantId()
        );
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }

        User user = userOpt.get();
        
        // Set tenant context
        TenantContext.setCurrentTenantId(user.getTenantId());

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.createToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        UserDTO userDTO = new UserDTO(user);
        
        return new LoginResponse(
            newAccessToken, 
            newRefreshToken, 
            jwtTokenProvider.getAccessTokenValidityInSeconds(), 
            userDTO,
            user.getTenantId()
        );
    }

    public UserDTO register(RegisterRequest registerRequest) {
        // Validate passwords match
        if (!registerRequest.isPasswordsMatch()) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if subdomain is available
        if (!tenantService.isSubdomainAvailable(registerRequest.getSubdomain())) {
            throw new IllegalArgumentException("Subdomain is not available");
        }

        // Check if organization name is available
        if (!tenantService.isNameAvailable(registerRequest.getOrganizationName())) {
            throw new IllegalArgumentException("Organization name is not available");
        }

        // Create tenant
        Tenant tenant = registerRequest.toTenantEntity();
        tenant = tenantService.createWithTrial(tenant, 30); // 30-day trial

        // Set tenant context for user creation
        TenantContext.setCurrentTenantId(tenant.getSubdomain());

        // Check if email is already used in this tenant
        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create admin user
        User user = registerRequest.toUserEntity(tenant.getSubdomain());
        user.setRoles(Set.of(User.Role.TENANT_ADMIN));
        user = userService.create(user);

        return new UserDTO(user);
    }

    public void logout(String username) {
        // In a more complete implementation, you might:
        // 1. Blacklist the token
        // 2. Clear any session data
        // 3. Log the logout event
        
        // For now, we just clear the tenant context
        TenantContext.clear();
    }

    public UserDTO getCurrentUser() {
        // This would typically be called with the current user's context
        // The username would come from the security context
        throw new UnsupportedOperationException("getCurrentUser not implemented yet");
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsername(token);
    }

    public String getTenantFromToken(String token) {
        return jwtTokenProvider.getTenantId(token);
    }
}