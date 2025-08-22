package io.conduktor.saas.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Profile({"dev", "demo"})
public class SampleDataGenerator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataGenerator.class);

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    @Value("${app.bootstrap.create-rich-sample-data:false}")
    private boolean createRichSampleData;

    public SampleDataGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (createRichSampleData) {
            logger.info("Creating rich sample data for development/demo environment...");
            try {
                generateSampleProjects();
                generateSampleAuditLogs();
                generateSampleNotifications();
                generateSampleUsageMetrics();
                generateSampleSystemEvents();
                generateSampleUserActivity();
                logger.info("Rich sample data creation completed!");
            } catch (Exception e) {
                logger.error("Error creating rich sample data", e);
            }
        }
    }

    private void generateSampleProjects() {
        logger.info("Generating sample projects...");
        
        List<String> projectNames = Arrays.asList(
            "Website Redesign", "Mobile App Development", "API Integration", 
            "Database Migration", "Security Audit", "Performance Optimization",
            "User Experience Research", "Cloud Infrastructure", "Data Analytics Platform",
            "Customer Portal", "Internal Tools", "Marketing Automation"
        );

        List<String> descriptions = Arrays.asList(
            "Redesigning the company website with modern UI/UX principles",
            "Developing a cross-platform mobile application",
            "Integrating third-party APIs for enhanced functionality",
            "Migrating legacy database to modern cloud solution",
            "Comprehensive security audit and vulnerability assessment",
            "Optimizing application performance and scalability",
            "Research project to improve user experience",
            "Setting up cloud infrastructure for scalability",
            "Building analytics platform for business intelligence",
            "Creating customer-facing portal for self-service",
            "Developing internal tools for team productivity",
            "Implementing marketing automation workflows"
        );

        String sql = """
            INSERT INTO projects (tenant_id, name, description, status, owner_id, start_date, end_date, budget, currency, tags, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        // Get tenant IDs and user IDs
        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        for (String tenantId : tenantIds) {
            List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE tenant_id = ?", Long.class, tenantId);
            
            if (userIds.isEmpty()) continue;

            int projectCount = random.nextInt(8) + 3; // 3-10 projects per tenant
            
            for (int i = 0; i < projectCount && i < projectNames.size(); i++) {
                String projectName = projectNames.get(i);
                String description = descriptions.get(i);
                String status = getRandomProjectStatus();
                Long ownerId = userIds.get(random.nextInt(userIds.size()));
                LocalDateTime startDate = LocalDateTime.now().minusDays(random.nextInt(180));
                LocalDateTime endDate = startDate.plusDays(random.nextInt(90) + 30);
                double budget = 10000 + (random.nextDouble() * 90000); // $10k - $100k
                String[] tags = getRandomProjectTags();
                
                jdbcTemplate.update(sql, tenantId, projectName, description, status, ownerId,
                    startDate, endDate, budget, "USD", 
                    "{" + String.join(",", Arrays.stream(tags).map(tag -> "\"" + tag + "\"").toArray(String[]::new)) + "}",
                    LocalDateTime.now(), LocalDateTime.now());
            }
        }
        
        logger.info("Generated sample projects for {} tenants", tenantIds.size());
    }

    private void generateSampleAuditLogs() {
        logger.info("Generating sample audit logs...");
        
        String sql = """
            INSERT INTO audit_logs (tenant_id, user_id, username, entity_type, entity_id, action, 
                                   old_values, new_values, ip_address, user_agent, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::inet, ?, ?)
            """;

        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        String[] entityTypes = {"USER", "PROJECT", "TENANT", "SUBSCRIPTION"};
        String[] actions = {"CREATE", "UPDATE", "DELETE", "LOGIN", "LOGOUT"};
        String[] ipAddresses = {"192.168.1.100", "10.0.0.50", "172.16.0.25", "203.0.113.10"};
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        for (String tenantId : tenantIds) {
            List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE tenant_id = ?", Long.class, tenantId);
            List<String> usernames = jdbcTemplate.queryForList(
                "SELECT username FROM users WHERE tenant_id = ?", String.class, tenantId);
            
            if (userIds.isEmpty()) continue;

            int logCount = random.nextInt(50) + 20; // 20-70 logs per tenant
            
            for (int i = 0; i < logCount; i++) {
                int userIndex = random.nextInt(userIds.size());
                Long userId = userIds.get(userIndex);
                String username = usernames.get(userIndex);
                String entityType = entityTypes[random.nextInt(entityTypes.length)];
                String entityId = String.valueOf(random.nextInt(1000) + 1);
                String action = actions[random.nextInt(actions.length)];
                String ipAddress = ipAddresses[random.nextInt(ipAddresses.length)];
                LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(30));
                
                String oldValues = action.equals("UPDATE") ? "{\"status\":\"old_value\"}" : null;
                String newValues = action.equals("CREATE") || action.equals("UPDATE") ? "{\"status\":\"new_value\"}" : null;
                
                jdbcTemplate.update(sql, tenantId, userId, username, entityType, entityId, action,
                    oldValues, newValues, ipAddress, userAgent, createdAt);
            }
        }
        
        logger.info("Generated sample audit logs for {} tenants", tenantIds.size());
    }

    private void generateSampleNotifications() {
        logger.info("Generating sample notifications...");
        
        String sql = """
            INSERT INTO notifications (tenant_id, recipient_id, recipient_email, type, channel, priority,
                                     subject, body, status, scheduled_at, sent_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        String[] notificationTypes = {"WELCOME", "PROJECT_UPDATE", "BILLING", "SYSTEM_ALERT"};
        String[] channels = {"EMAIL", "IN_APP"};
        String[] priorities = {"LOW", "NORMAL", "HIGH"};
        String[] statuses = {"SENT", "DELIVERED", "READ", "PENDING"};

        for (String tenantId : tenantIds) {
            List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE tenant_id = ?", Long.class, tenantId);
            List<String> userEmails = jdbcTemplate.queryForList(
                "SELECT email FROM users WHERE tenant_id = ?", String.class, tenantId);
            
            if (userIds.isEmpty()) continue;

            int notificationCount = random.nextInt(30) + 10; // 10-40 notifications per tenant
            
            for (int i = 0; i < notificationCount; i++) {
                int userIndex = random.nextInt(userIds.size());
                Long recipientId = userIds.get(userIndex);
                String recipientEmail = userEmails.get(userIndex);
                String type = notificationTypes[random.nextInt(notificationTypes.length)];
                String channel = channels[random.nextInt(channels.length)];
                String priority = priorities[random.nextInt(priorities.length)];
                String status = statuses[random.nextInt(statuses.length)];
                
                String subject = "Sample notification: " + type;
                String body = "This is a sample notification of type " + type + " for testing purposes.";
                
                LocalDateTime scheduledAt = LocalDateTime.now().minusDays(random.nextInt(15));
                LocalDateTime sentAt = status.equals("PENDING") ? null : scheduledAt.plusMinutes(random.nextInt(60));
                LocalDateTime createdAt = scheduledAt.minusMinutes(random.nextInt(30));
                
                jdbcTemplate.update(sql, tenantId, recipientId, recipientEmail, type, channel, priority,
                    subject, body, status, scheduledAt, sentAt, createdAt, LocalDateTime.now());
            }
        }
        
        logger.info("Generated sample notifications for {} tenants", tenantIds.size());
    }

    private void generateSampleUsageMetrics() {
        logger.info("Generating sample usage metrics...");
        
        String sql = """
            INSERT INTO usage_metrics (tenant_id, metric_type, metric_name, value, unit, tags, recorded_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?)
            """;

        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        String[] metricTypes = {"USAGE", "PERFORMANCE", "BILLING", "SYSTEM"};
        String[][] metricsAndUnits = {
            {"api_requests", "count"},
            {"storage_used", "bytes"},
            {"cpu_usage", "percentage"},
            {"memory_usage", "bytes"},
            {"database_connections", "count"},
            {"response_time", "milliseconds"},
            {"active_users", "count"},
            {"projects_created", "count"}
        };

        for (String tenantId : tenantIds) {
            // Generate metrics for the last 30 days
            for (int day = 0; day < 30; day++) {
                LocalDateTime recordedAt = LocalDateTime.now().minusDays(day);
                
                for (String[] metricAndUnit : metricsAndUnits) {
                    String metricName = metricAndUnit[0];
                    String unit = metricAndUnit[1];
                    String metricType = metricTypes[random.nextInt(metricTypes.length)];
                    
                    double value = generateMetricValue(metricName);
                    String tags = String.format("{\"tenant_id\":\"%s\",\"source\":\"sample_data\"}", tenantId);
                    
                    jdbcTemplate.update(sql, tenantId, metricType, metricName, value, unit, tags, 
                        recordedAt, recordedAt);
                }
            }
        }
        
        logger.info("Generated sample usage metrics for {} tenants", tenantIds.size());
    }

    private void generateSampleSystemEvents() {
        logger.info("Generating sample system events...");
        
        String sql = """
            INSERT INTO system_events (tenant_id, event_type, event_category, severity, message, 
                                     source, metadata, user_id, resolved, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?)
            """;

        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        String[] eventTypes = {"USER_LOGIN", "PROJECT_CREATED", "PAYMENT_FAILED", "API_LIMIT_EXCEEDED", "SYSTEM_MAINTENANCE"};
        String[] categories = {"SECURITY", "SYSTEM", "USER", "BILLING", "PERFORMANCE"};
        String[] severities = {"DEBUG", "INFO", "WARN", "ERROR"};

        for (String tenantId : tenantIds) {
            List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE tenant_id = ?", Long.class, tenantId);
            
            int eventCount = random.nextInt(20) + 5; // 5-25 events per tenant
            
            for (int i = 0; i < eventCount; i++) {
                String eventType = eventTypes[random.nextInt(eventTypes.length)];
                String category = categories[random.nextInt(categories.length)];
                String severity = severities[random.nextInt(severities.length)];
                String message = "Sample system event: " + eventType;
                String source = "sample_generator";
                String metadata = String.format("{\"event_id\":\"%d\",\"tenant_id\":\"%s\"}", i, tenantId);
                Long userId = userIds.isEmpty() ? null : userIds.get(random.nextInt(userIds.size()));
                boolean resolved = random.nextBoolean();
                LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(30));
                
                jdbcTemplate.update(sql, tenantId, eventType, category, severity, message, 
                    source, metadata, userId, resolved, createdAt);
            }
        }
        
        logger.info("Generated sample system events for {} tenants", tenantIds.size());
    }

    private void generateSampleUserActivity() {
        logger.info("Generating sample user activity...");
        
        String sql = """
            INSERT INTO user_activity (tenant_id, user_id, activity_type, resource_type, resource_id,
                                     description, metadata, ip_address, user_agent, duration_ms, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?::jsonb, ?::inet, ?, ?, ?)
            """;

        List<String> tenantIds = jdbcTemplate.queryForList(
            "SELECT tenant_id FROM tenants WHERE tenant_id != 'system'", String.class);

        String[] activityTypes = {"LOGIN", "LOGOUT", "CREATE_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT", "VIEW_DASHBOARD"};
        String[] resourceTypes = {"USER", "PROJECT", "DASHBOARD", "SETTINGS"};
        String[] ipAddresses = {"192.168.1.100", "10.0.0.50", "172.16.0.25"};
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        for (String tenantId : tenantIds) {
            List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE tenant_id = ?", Long.class, tenantId);
            
            if (userIds.isEmpty()) continue;

            int activityCount = random.nextInt(100) + 50; // 50-150 activities per tenant
            
            for (int i = 0; i < activityCount; i++) {
                Long userId = userIds.get(random.nextInt(userIds.size()));
                String activityType = activityTypes[random.nextInt(activityTypes.length)];
                String resourceType = resourceTypes[random.nextInt(resourceTypes.length)];
                String resourceId = String.valueOf(random.nextInt(100) + 1);
                String description = "User performed " + activityType + " on " + resourceType;
                String metadata = String.format("{\"activity_id\":\"%d\",\"session_id\":\"session_%d\"}", i, random.nextInt(1000));
                String ipAddress = ipAddresses[random.nextInt(ipAddresses.length)];
                int durationMs = random.nextInt(5000) + 100; // 100-5100ms
                LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(30));
                
                jdbcTemplate.update(sql, tenantId, userId, activityType, resourceType, resourceId,
                    description, metadata, ipAddress, userAgent, durationMs, createdAt);
            }
        }
        
        logger.info("Generated sample user activity for {} tenants", tenantIds.size());
    }

    private String getRandomProjectStatus() {
        String[] statuses = {"ACTIVE", "INACTIVE", "ARCHIVED", "COMPLETED"};
        return statuses[random.nextInt(statuses.length)];
    }

    private String[] getRandomProjectTags() {
        String[] allTags = {"web", "mobile", "api", "database", "security", "performance", "ui", "ux", "cloud", "analytics"};
        int tagCount = random.nextInt(3) + 1; // 1-3 tags
        String[] selectedTags = new String[tagCount];
        for (int i = 0; i < tagCount; i++) {
            selectedTags[i] = allTags[random.nextInt(allTags.length)];
        }
        return selectedTags;
    }

    private double generateMetricValue(String metricName) {
        return switch (metricName) {
            case "api_requests" -> random.nextInt(10000) + 1000;
            case "storage_used" -> random.nextLong(1000000000L) + 100000000L; // 100MB - 1GB
            case "cpu_usage" -> random.nextDouble() * 100;
            case "memory_usage" -> random.nextLong(2000000000L) + 500000000L; // 500MB - 2.5GB
            case "database_connections" -> random.nextInt(50) + 5;
            case "response_time" -> random.nextInt(1000) + 50;
            case "active_users" -> random.nextInt(100) + 10;
            case "projects_created" -> random.nextInt(5) + 1;
            default -> random.nextDouble() * 100;
        };
    }
}