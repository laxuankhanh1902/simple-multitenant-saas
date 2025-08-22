package io.conduktor.saas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.conduktor.saas.SaasApplication;
import io.conduktor.saas.tenant.dto.CreateTenantRequest;
import io.conduktor.saas.tenant.dto.TenantDTO;
import io.conduktor.saas.tenant.dto.UpdateTenantRequest;
import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SaasApplication.class)
@AutoConfigureWebMvc
@Testcontainers
// @Transactional
public class TenantIntegrationTest {

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
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldCreateTenant() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest();
        request.setName("Test Company");
        request.setSubdomain("testco");
        request.setDescription("Test company description");
        request.setAdminEmail("admin@testco.com");
        request.setAdminFirstName("John");
        request.setAdminLastName("Doe");
        request.setPhone("+1-555-0100");
        request.setAddress("123 Test St");
        request.setCity("Test City");
        request.setState("TS");
        request.setCountry("Test Country");
        request.setPostalCode("12345");

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Test Company")))
                .andExpect(jsonPath("$.data.subdomain", is("testco")))
                .andExpect(jsonPath("$.data.adminEmail", is("admin@testco.com")))
                .andExpect(jsonPath("$.data.status", is("TRIAL")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldGetAllTenants() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("testtenant");
        tenant.setAdminEmail("admin@testtenant.com");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldGetTenantById() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("testtenant");
        tenant.setAdminEmail("admin@testtenant.com");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);

        mockMvc.perform(get("/api/tenants/{id}", tenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(tenant.getId().intValue())))
                .andExpect(jsonPath("$.data.name", is("Test Tenant")))
                .andExpect(jsonPath("$.data.subdomain", is("testtenant")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldUpdateTenant() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("testtenant");
        tenant.setAdminEmail("admin@testtenant.com");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);

        UpdateTenantRequest updateRequest = new UpdateTenantRequest();
        updateRequest.setName("Updated Test Tenant");
        updateRequest.setDescription("Updated description");
        updateRequest.setPhone("+1-555-0200");

        mockMvc.perform(put("/api/tenants/{id}", tenant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Updated Test Tenant")))
                .andExpect(jsonPath("$.data.description", is("Updated description")))
                .andExpect(jsonPath("$.data.phone", is("+1-555-0200")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldUpdateTenantStatus() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("testtenant");
        tenant.setAdminEmail("admin@testtenant.com");
        tenant.setStatus(Tenant.TenantStatus.TRIAL);
        tenant = tenantRepository.save(tenant);

        mockMvc.perform(patch("/api/tenants/{id}/status", tenant.getId())
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldSuspendTenant() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("testtenant");
        tenant.setAdminEmail("admin@testtenant.com");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant = tenantRepository.save(tenant);

        mockMvc.perform(patch("/api/tenants/{id}/suspend", tenant.getId())
                .param("reason", "Policy violation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("SUSPENDED")));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldValidateSubdomainAvailability() throws Exception {
        mockMvc.perform(get("/api/tenants/validate/subdomain/{subdomain}", "availablesubdomain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(true)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldReturnSubdomainNotAvailable() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setSubdomain("existingsubdomain");
        tenant.setAdminEmail("admin@existing.com");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        mockMvc.perform(get("/api/tenants/validate/subdomain/{subdomain}", "existingsubdomain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(false)));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldSearchTenants() throws Exception {
        // Create test tenants
        Tenant tenant1 = new Tenant();
        tenant1.setName("Search Test Company");
        tenant1.setSubdomain("searchtest");
        tenant1.setAdminEmail("admin@searchtest.com");
        tenant1.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant1);

        Tenant tenant2 = new Tenant();
        tenant2.setName("Another Company");
        tenant2.setSubdomain("another");
        tenant2.setAdminEmail("admin@another.com");
        tenant2.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant2);

        mockMvc.perform(get("/api/tenants/search")
                .param("query", "Search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldExportTenants() throws Exception {
        // Create test tenant
        Tenant tenant = new Tenant();
        tenant.setName("Export Test");
        tenant.setSubdomain("exporttest");
        tenant.setAdminEmail("admin@exporttest.com");
        tenant.setAdminFirstName("Export");
        tenant.setAdminLastName("User");
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        mockMvc.perform(get("/api/tenants/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(content().string(containsString("Export Test")))
                .andExpect(content().string(containsString("exporttest")));
    }

    @Test
    void shouldRequireAuthenticationForTenantOperations() throws Exception {
        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void shouldRequireAdminRoleForTenantOperations() throws Exception {
        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }
}