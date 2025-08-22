package io.conduktor.saas.user.controller;

import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.common.dto.SearchRequest;
import io.conduktor.saas.user.dto.*;
import io.conduktor.saas.user.entity.User;
import io.conduktor.saas.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve paginated list of users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<User> users = userService.findAll(null, pageable);
        PageResponse<UserDTO> userDTOs = new PageResponse<>(
            users.map(UserDTO::new)
        );
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDTOs));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by query string")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> searchUsers(
            @Valid @ModelAttribute SearchRequest searchRequest) {
        
        if (!searchRequest.hasQuery()) {
            return getAllUsers(searchRequest.toPageable());
        }

        List<User> users = userService.searchUsers(searchRequest.getQuery());
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
        
        // Create a mock page response for search results
        PageResponse<UserDTO> response = new PageResponse<>();
        response.setContent(userDTOs);
        response.setTotalElements(userDTOs.size());
        response.setPageSize(searchRequest.getSize());
        response.setPageNumber(searchRequest.getPage());
        response.setTotalPages((int) Math.ceil((double) userDTOs.size() / searchRequest.getSize()));
        response.setFirst(searchRequest.getPage() == 0);
        response.setLast(searchRequest.getPage() >= response.getTotalPages() - 1);
        response.setEmpty(userDTOs.isEmpty());

        return ResponseEntity.ok(ApiResponse.success("Users found", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN') or @userService.findById(#id).username == authentication.name")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        User user = userService.findById(id);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Username already exists"));
        }
        
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Email already exists"));
        }

        User user = userService.create(request.toEntity());
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User created successfully", userDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN') or @userService.findById(#id).username == authentication.name")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        User existingUser = userService.findById(id);
        
        // Check for username conflicts
        if (request.getUsername() != null && 
            !request.getUsername().equals(existingUser.getUsername()) &&
            userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Username already exists"));
        }
        
        // Check for email conflicts
        if (request.getEmail() != null && 
            !request.getEmail().equals(existingUser.getEmail()) &&
            userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Email already exists"));
        }

        request.updateEntity(existingUser);
        User updatedUser = userService.update(id, existingUser);
        UserDTO userDTO = new UserDTO(updatedUser);
        
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        userService.deleteById(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Change user password", description = "Change a user's password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN') or @userService.findById(#id).username == authentication.name")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        if (!request.isPasswordsMatch()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("New passwords do not match"));
        }

        try {
            userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/roles")
    @Operation(summary = "Update user roles", description = "Update a user's roles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRoles(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestBody Set<User.Role> roles) {
        
        User user = userService.updateRoles(id, roles);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User roles updated successfully", userDTO));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "Update a user's status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestParam User.UserStatus status) {
        
        User user = userService.updateStatus(id, status);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", userDTO));
    }

    @PatchMapping("/{id}/lock")
    @Operation(summary = "Lock user account", description = "Lock a user account")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> lockUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestParam(defaultValue = "30") int lockDurationMinutes) {
        
        LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
        User user = userService.lockAccount(id, lockUntil);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User account locked successfully", userDTO));
    }

    @PatchMapping("/{id}/unlock")
    @Operation(summary = "Unlock user account", description = "Unlock a user account")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> unlockUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        User user = userService.unlockAccount(id);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User account unlocked successfully", userDTO));
    }

    @PatchMapping("/{id}/verify-email")
    @Operation(summary = "Verify user email", description = "Mark a user's email as verified")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> verifyEmail(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        User user = userService.verifyEmail(id);
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(ApiResponse.success("User email verified successfully", userDTO));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Get all users with a specific role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable User.Role role) {
        
        List<User> users = userService.findByRole(role);
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDTOs));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Get all users with a specific status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable User.UserStatus status) {
        
        List<User> users = userService.findByStatus(status);
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDTOs));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Get all active users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getActiveUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<User> users = userService.findActiveUsers(pageable);
        PageResponse<UserDTO> userDTOs = new PageResponse<>(
            users.map(UserDTO::new)
        );
        
        return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", userDTOs));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently created users", description = "Get users created in the last N days")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getRecentUsers(
            @RequestParam(defaultValue = "30") int days) {
        
        List<User> users = userService.findRecentlyCreated(days);
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Recent users retrieved successfully", userDTOs));
    }

    @GetMapping("/export")
    @Operation(summary = "Export users to CSV", description = "Export all users to CSV format")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_ADMIN')")
    public ResponseEntity<String> exportUsers() {
        try {
            List<User> users = userService.findAll();
            
            StringWriter stringWriter = new StringWriter();
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Username", "Email", "First Name", "Last Name", 
                         "Phone", "Status", "Enabled", "Email Verified", "Last Login", 
                         "Login Count", "Created At")
                .build();
            
            try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, csvFormat)) {
                for (User user : users) {
                    csvPrinter.printRecord(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone(),
                        user.getStatus(),
                        user.isEnabled(),
                        user.isEmailVerified(),
                        user.getLastLogin(),
                        user.getLoginCount(),
                        user.getCreatedAt()
                    );
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "users.csv");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(stringWriter.toString());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}