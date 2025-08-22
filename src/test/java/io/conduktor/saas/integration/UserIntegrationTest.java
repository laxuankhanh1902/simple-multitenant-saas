package io.conduktor.saas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.conduktor.saas.SaasApplication;
import io.conduktor.saas.security.TenantContext;
import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.repository.TenantRepository;
import io.conduktor.saas.user.dto.CreateUserRequest;
import io.conduktor.saas.user.dto.UpdateUserRequest;
import io.conduktor.saas.user.entity.User;
import io.conduktor.saas.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SaasApplication.class)
@AutoConfigureWebMvc
@Testcontainers
// @Transactional
public class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("saas_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("app.bootstrap.create-sample-data", () -> "false");
        registry.add("app.bootstrap.create-rich-sample-data", () -> "false");
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test tenant
        testTenant = new Tenant();
        testTenant.setName("Test Tenant");
        testTenant.setSubdomain("testtenant");
        testTenant.setAdminEmail("admin@testtenant.com");
        testTenant.setStatus(Tenant.TenantStatus.ACTIVE);
        testTenant = tenantRepository.save(testTenant);

        // Set tenant context
        TenantContext.setCurrentTenantId(testTenant.getSubdomain());
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhone("+1-555-0100");
        request.setRoles(Set.of(User.Role.USER));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("testuser")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")))
                .andExpect(jsonPath("$.data.firstName", is("Test")))
                .andExpect(jsonPath("$.data.lastName", is("User")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldGetAllUsers() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldGetUserById() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.data.username", is("testuser")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldUpdateUser() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setPhone("+1-555-0200");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.firstName", is("Updated")))
                .andExpect(jsonPath("$.data.phone", is("+1-555-0200")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldUpdateUserStatus() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        mockMvc.perform(patch("/api/users/{id}/status", user.getId())
                .param("status", "SUSPENDED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("SUSPENDED")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldLockUser() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        mockMvc.perform(patch("/api/users/{id}/lock", user.getId())
                .param("lockDurationMinutes", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.enabled", is(false)))
                .andExpect(jsonPath("$.data.accountLockedUntil", notNullValue()));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldUnlockUser() throws Exception {
        // Create locked test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(false);
        user.setEmailVerified(true);
        user.setFailedLoginAttempts(5);
        user = userRepository.save(user);

        mockMvc.perform(patch("/api/users/{id}/unlock", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.enabled", is(true)))
                .andExpect(jsonPath("$.data.accountLockedUntil", nullValue()))
                .andExpect(jsonPath("$.data.failedLoginAttempts", is(0)));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldVerifyUserEmail() throws Exception {
        // Create test user with unverified email
        User user = new User(testTenant.getSubdomain());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus(User.UserStatus.PENDING_VERIFICATION);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user = userRepository.save(user);

        mockMvc.perform(patch("/api/users/{id}/verify-email", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.emailVerified", is(true)))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldSearchUsers() throws Exception {
        // Create test users
        User user1 = new User(testTenant.getSubdomain());
        user1.setUsername("searchtest");
        user1.setEmail("search@example.com");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setFirstName("Search");
        user1.setLastName("Test");
        user1.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user1);

        User user2 = new User(testTenant.getSubdomain());
        user2.setUsername("anotheruser");
        user2.setEmail("another@example.com");
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setFirstName("Another");
        user2.setLastName("User");
        user2.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user2);

        mockMvc.perform(get("/api/users/search")
                .param("query", "Search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldGetActiveUsers() throws Exception {
        // Create active test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("activeuser");
        user.setEmail("active@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Active");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldExportUsers() throws Exception {
        // Create test user
        User user = new User(testTenant.getSubdomain());
        user.setUsername("exportuser");
        user.setEmail("export@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Export");
        user.setLastName("User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(get("/api/users/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(content().string(containsString("Export")))
                .andExpect(content().string(containsString("export@example.com")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldNotCreateUserWithDuplicateUsername() throws Exception {
        // Create existing user
        User existingUser = new User(testTenant.getSubdomain());
        existingUser.setUsername("duplicate");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(existingUser);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("duplicate");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }

    @Test
    @WithMockUser(authorities = "TENANT_ADMIN")
    void shouldNotCreateUserWithDuplicateEmail() throws Exception {
        // Create existing user
        User existingUser = new User(testTenant.getSubdomain());
        existingUser.setUsername("existing");
        existingUser.setEmail("duplicate@example.com");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(existingUser);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }

    @Test
    void shouldRequireAuthenticationForUserOperations() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "VIEWER")
    void shouldRequireAdminRoleForUserOperations() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }
}