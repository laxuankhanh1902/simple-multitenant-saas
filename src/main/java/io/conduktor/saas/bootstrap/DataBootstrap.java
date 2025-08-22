package io.conduktor.saas.bootstrap;

import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.service.TenantService;
import io.conduktor.saas.user.entity.User;
import io.conduktor.saas.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@Profile({"dev", "demo"})
public class DataBootstrap implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataBootstrap.class);

    private final TenantService tenantService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.create-sample-data:false}")
    private boolean createSampleData;

    public DataBootstrap(TenantService tenantService, 
                        UserService userService,
                        PasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (createSampleData) {
            logger.info("Creating sample data for development/demo environment...");
            createSampleTenants();
            logger.info("Sample data creation completed!");
        }
    }

    private void createSampleTenants() {
        List<SampleTenantData> sampleTenants = Arrays.asList(
            new SampleTenantData(
                "Acme Corporation", "acme", "admin@acme.com", 
                "John", "Doe", "+1-555-0101",
                "123 Business St", "New York", "NY", "USA", "10001"
            ),
            new SampleTenantData(
                "Tech Innovations", "techinnovations", "admin@techinnovations.com",
                "Jane", "Smith", "+1-555-0102", 
                "456 Innovation Ave", "San Francisco", "CA", "USA", "94105"
            ),
            new SampleTenantData(
                "Global Enterprises", "globalent", "admin@globalent.com",
                "Robert", "Johnson", "+1-555-0103",
                "789 Enterprise Blvd", "Chicago", "IL", "USA", "60601"
            ),
            new SampleTenantData(
                "Creative Studio", "creativestudio", "admin@creativestudio.com",
                "Emily", "Davis", "+1-555-0104",
                "321 Creative Way", "Los Angeles", "CA", "USA", "90210"
            ),
            new SampleTenantData(
                "StartupXYZ", "startupxyz", "admin@startupxyz.com",
                "Michael", "Brown", "+1-555-0105",
                "654 Startup Lane", "Austin", "TX", "USA", "73301"
            )
        );

        for (SampleTenantData sampleData : sampleTenants) {
            createTenantWithUsers(sampleData);
        }
    }

    private void createTenantWithUsers(SampleTenantData sampleData) {
        try {
            // Check if tenant already exists
            if (tenantService.existsBySubdomain(sampleData.subdomain)) {
                logger.info("Tenant with subdomain '{}' already exists, skipping...", sampleData.subdomain);
                return;
            }

            // Create tenant
            Tenant tenant = new Tenant();
            tenant.setName(sampleData.name);
            tenant.setSubdomain(sampleData.subdomain);
            tenant.setDescription("Sample tenant for " + sampleData.name);
            tenant.setAdminEmail(sampleData.adminEmail);
            tenant.setAdminFirstName(sampleData.adminFirstName);
            tenant.setAdminLastName(sampleData.adminLastName);
            tenant.setPhone(sampleData.phone);
            tenant.setAddress(sampleData.address);
            tenant.setCity(sampleData.city);
            tenant.setState(sampleData.state);
            tenant.setCountry(sampleData.country);
            tenant.setPostalCode(sampleData.postalCode);
            tenant.setStatus(Tenant.TenantStatus.TRIAL);
            tenant.setTrialEndDate(LocalDateTime.now().plusDays(30));
            tenant.setMaxUsers(25);
            tenant.setStorageLimitGb(500);
            tenant.setApiRateLimit(5000);

            tenant = tenantService.create(tenant);
            logger.info("Created tenant: {} ({})", tenant.getName(), tenant.getSubdomain());

            // Create admin user for tenant
            createAdminUser(tenant, sampleData);

            // Create additional sample users
            createSampleUsers(tenant);

        } catch (Exception e) {
            logger.error("Error creating tenant '{}': {}", sampleData.name, e.getMessage(), e);
        }
    }

    private void createAdminUser(Tenant tenant, SampleTenantData sampleData) {
        User admin = new User(tenant.getSubdomain());
        admin.setUsername(sampleData.adminEmail);
        admin.setEmail(sampleData.adminEmail);
        admin.setPassword("admin123"); // Plain text for dev - will be encoded by service
        admin.setFirstName(sampleData.adminFirstName);
        admin.setLastName(sampleData.adminLastName);
        admin.setPhone(sampleData.phone);
        admin.setStatus(User.UserStatus.ACTIVE);
        admin.setEnabled(true);
        admin.setEmailVerified(true);
        admin.setRoles(Set.of(User.Role.TENANT_ADMIN));

        userService.create(admin);
        logger.info("Created admin user: {} for tenant {} with password: admin123", admin.getEmail(), tenant.getName());
    }

    private void createSampleUsers(Tenant tenant) {
        List<SampleUserData> sampleUsers = Arrays.asList(
            new SampleUserData("Alice", "Johnson", "alice.johnson", "alice.johnson@" + tenant.getSubdomain() + ".com", User.Role.USER),
            new SampleUserData("Bob", "Smith", "bob.smith", "bob.smith@" + tenant.getSubdomain() + ".com", User.Role.USER),
            new SampleUserData("Carol", "Davis", "carol.davis", "carol.davis@" + tenant.getSubdomain() + ".com", User.Role.USER),
            new SampleUserData("David", "Wilson", "david.wilson", "david.wilson@" + tenant.getSubdomain() + ".com", User.Role.VIEWER),
            new SampleUserData("Eve", "Taylor", "eve.taylor", "eve.taylor@" + tenant.getSubdomain() + ".com", User.Role.USER)
        );

        for (SampleUserData userData : sampleUsers) {
            try {
                User user = new User(tenant.getSubdomain());
                user.setUsername(userData.username);
                user.setEmail(userData.email);
                user.setPassword("user123"); // Plain text for dev - will be encoded by service
                user.setFirstName(userData.firstName);
                user.setLastName(userData.lastName);
                user.setStatus(User.UserStatus.ACTIVE);
                user.setEnabled(true);
                user.setEmailVerified(true);
                user.setRoles(Set.of(userData.role));
                user.setTimezone("UTC");
                user.setLocale("en_US");

                userService.create(user);
                logger.info("Created user: {} for tenant {} with password: user123", user.getEmail(), tenant.getName());
            } catch (Exception e) {
                logger.warn("Error creating user {}: {}", userData.email, e.getMessage());
            }
        }
    }

    private static class SampleTenantData {
        final String name;
        final String subdomain;
        final String adminEmail;
        final String adminFirstName;
        final String adminLastName;
        final String phone;
        final String address;
        final String city;
        final String state;
        final String country;
        final String postalCode;

        SampleTenantData(String name, String subdomain, String adminEmail, 
                        String adminFirstName, String adminLastName, String phone,
                        String address, String city, String state, String country, String postalCode) {
            this.name = name;
            this.subdomain = subdomain;
            this.adminEmail = adminEmail;
            this.adminFirstName = adminFirstName;
            this.adminLastName = adminLastName;
            this.phone = phone;
            this.address = address;
            this.city = city;
            this.state = state;
            this.country = country;
            this.postalCode = postalCode;
        }
    }

    private static class SampleUserData {
        final String firstName;
        final String lastName;
        final String username;
        final String email;
        final User.Role role;

        SampleUserData(String firstName, String lastName, String username, String email, User.Role role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.email = email;
            this.role = role;
        }
    }
}