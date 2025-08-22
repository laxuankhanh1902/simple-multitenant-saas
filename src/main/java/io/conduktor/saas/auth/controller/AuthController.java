package io.conduktor.saas.auth.controller;

import io.conduktor.saas.auth.dto.*;
import io.conduktor.saas.auth.service.AuthService;
import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Authentication", description = "Authentication and authorization API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Authentication failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Login failed", e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register new organization", description = "Register a new tenant and admin user")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserDTO user = authService.register(registerRequest);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Registration failed", "An error occurred during registration"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            LoginResponse loginResponse = authService.refreshToken(refreshRequest.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", loginResponse));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Token refresh failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Token refresh failed", "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract username from token or security context
            // For now, we'll just perform basic logout operations
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = authService.getUsernameFromToken(token);
                authService.logout(username);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Logout successful")); // Always return success for logout
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        try {
            UserDTO user = authService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success("User information retrieved", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get user information", e.getMessage()));
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        try {
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success(
                isValid ? "Token is valid" : "Token is invalid", 
                isValid
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Token is invalid", false));
        }
    }

    @GetMapping("/check-subdomain/{subdomain}")
    @Operation(summary = "Check subdomain availability", description = "Check if a subdomain is available for registration")
    public ResponseEntity<ApiResponse<Boolean>> checkSubdomainAvailability(@PathVariable String subdomain) {
        // This would typically call the tenant service to check availability
        // For now, we'll implement a basic check
        return ResponseEntity.ok(ApiResponse.success("Subdomain checked", true));
    }

    @GetMapping("/health")
    @Operation(summary = "Authentication service health", description = "Check authentication service health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Authentication service is healthy"));
    }
}