-- Insert sample Kafka clusters
INSERT INTO kafka_clusters (tenant_id, name, description, bootstrap_servers, connection_type, status, health_status, kafka_version, broker_count, topic_count, partition_count, consumer_group_count, auto_health_check, health_check_interval_minutes, last_health_check) VALUES
('acme-corp', 'production-cluster', 'Production Kafka cluster for critical business events', 'kafka-prod-1:9092,kafka-prod-2:9092,kafka-prod-3:9092', 'SASL_SSL', 'ACTIVE', 'HEALTHY', '3.5.0', 3, 25, 150, 8, true, 5, CURRENT_TIMESTAMP - INTERVAL '2 minutes'),
('acme-corp', 'staging-cluster', 'Staging environment for testing', 'kafka-stage-1:9092,kafka-stage-2:9092', 'PLAINTEXT', 'ACTIVE', 'HEALTHY', '3.5.0', 2, 15, 45, 3, true, 10, CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
('techstart-inc', 'dev-cluster', 'Development Kafka cluster', 'kafka-dev:9092', 'PLAINTEXT', 'ACTIVE', 'HEALTHY', '3.4.1', 1, 8, 24, 2, true, 15, CURRENT_TIMESTAMP - INTERVAL '1 minute'),
('global-retail', 'analytics-cluster', 'High-throughput analytics cluster', 'kafka-analytics-1:9092,kafka-analytics-2:9092,kafka-analytics-3:9092,kafka-analytics-4:9092', 'SASL_SSL', 'ACTIVE', 'HEALTHY', '3.5.1', 4, 35, 210, 12, true, 3, CURRENT_TIMESTAMP - INTERVAL '30 seconds'),
('healthcare-plus', 'secure-cluster', 'HIPAA-compliant secure cluster', 'kafka-secure-1:9092,kafka-secure-2:9092,kafka-secure-3:9092', 'SASL_SSL', 'ACTIVE', 'HEALTHY', '3.5.0', 3, 18, 72, 6, true, 5, CURRENT_TIMESTAMP - INTERVAL '3 minutes'),
('edu-platform', 'learning-cluster', 'Educational content processing', 'kafka-edu-1:9092,kafka-edu-2:9092', 'PLAINTEXT', 'ACTIVE', 'DEGRADED', '3.4.0', 2, 12, 36, 4, true, 10, CURRENT_TIMESTAMP - INTERVAL '15 minutes');

-- Insert sample Kafka topics
INSERT INTO kafka_topics (tenant_id, cluster_id, name, description, partitions, replication_factor, retention_ms, cleanup_policy, size_bytes, message_count, compression_type, is_internal, is_compacted) VALUES
-- ACME Corp Production Topics
('acme-corp', 1, 'user-events', 'User activity and interaction events', 12, 3, 604800000, 'delete', 15728640000, 2500000, 'gzip', false, false),
('acme-corp', 1, 'order-events', 'E-commerce order processing events', 8, 3, 2592000000, 'delete', 8589934592, 1200000, 'snappy', false, false),
('acme-corp', 1, 'payment-events', 'Payment processing and transaction events', 6, 3, 2592000000, 'delete', 4294967296, 800000, 'lz4', false, false),
('acme-corp', 1, 'inventory-updates', 'Real-time inventory level changes', 4, 3, 86400000, 'compact', 1073741824, 500000, 'gzip', false, true),
('acme-corp', 1, 'audit-trail', 'Security and compliance audit logs', 16, 3, 31536000000, 'delete', 21474836480, 5000000, 'gzip', false, false),
('acme-corp', 1, 'notification-queue', 'User notification delivery queue', 3, 2, 604800000, 'delete', 536870912, 200000, 'snappy', false, false),

-- ACME Corp Staging Topics
('acme-corp', 2, 'test-events', 'Testing and validation events', 3, 2, 86400000, 'delete', 268435456, 50000, 'none', false, false),
('acme-corp', 2, 'staging-orders', 'Staging order processing', 2, 2, 86400000, 'delete', 134217728, 25000, 'gzip', false, false),

-- TechStart Inc Topics
('techstart-inc', 3, 'app-analytics', 'Application usage analytics', 4, 1, 604800000, 'delete', 2147483648, 300000, 'gzip', false, false),
('techstart-inc', 3, 'feature-flags', 'Feature flag state changes', 1, 1, 86400000, 'compact', 67108864, 10000, 'none', false, true),
('techstart-inc', 3, 'user-onboarding', 'New user onboarding flow', 2, 1, 2592000000, 'delete', 536870912, 75000, 'snappy', false, false),

-- Global Retail Topics
('global-retail', 4, 'clickstream', 'Website clickstream data', 24, 3, 604800000, 'delete', 42949672960, 10000000, 'gzip', false, false),
('global-retail', 4, 'product-catalog', 'Product information updates', 6, 3, 86400000, 'compact', 8589934592, 2000000, 'lz4', false, true),
('global-retail', 4, 'recommendations', 'ML-generated product recommendations', 8, 3, 3600000, 'delete', 4294967296, 1500000, 'snappy', false, false),
('global-retail', 4, 'price-changes', 'Dynamic pricing updates', 4, 3, 86400000, 'compact', 1073741824, 400000, 'gzip', false, true),

-- Healthcare Plus Topics
('healthcare-plus', 5, 'patient-vitals', 'Patient vital signs monitoring', 8, 3, 2592000000, 'delete', 12884901888, 3000000, 'gzip', false, false),
('healthcare-plus', 5, 'appointment-events', 'Appointment scheduling and updates', 4, 3, 31536000000, 'delete', 4294967296, 800000, 'snappy', false, false),
('healthcare-plus', 5, 'medication-alerts', 'Medication reminders and alerts', 2, 3, 604800000, 'delete', 268435456, 150000, 'lz4', false, false),

-- Edu Platform Topics
('edu-platform', 6, 'student-progress', 'Student learning progress tracking', 6, 2, 31536000000, 'delete', 8589934592, 1000000, 'gzip', false, false),
('edu-platform', 6, 'course-interactions', 'Course content interaction events', 4, 2, 2592000000, 'delete', 2147483648, 600000, 'snappy', false, false),
('edu-platform', 6, 'assessment-results', 'Quiz and exam results', 3, 2, 31536000000, 'delete', 1073741824, 250000, 'gzip', false, false);

-- Insert sample Kafka consumer groups
INSERT INTO kafka_consumer_groups (tenant_id, cluster_id, group_id, state, protocol, member_count, lag_total, assignment_strategy) VALUES
-- ACME Corp Consumer Groups
('acme-corp', 1, 'analytics-service', 'Stable', 'consumer', 3, 1250, 'RangeAssignor'),
('acme-corp', 1, 'order-processor', 'Stable', 'consumer', 2, 450, 'RoundRobinAssignor'),
('acme-corp', 1, 'payment-handler', 'Stable', 'consumer', 2, 230, 'StickyAssignor'),
('acme-corp', 1, 'inventory-updater', 'Stable', 'consumer', 1, 12, 'RangeAssignor'),
('acme-corp', 1, 'notification-sender', 'Stable', 'consumer', 1, 45, 'RoundRobinAssignor'),
('acme-corp', 1, 'audit-processor', 'Stable', 'consumer', 4, 2100, 'RangeAssignor'),
('acme-corp', 2, 'staging-test-consumer', 'Stable', 'consumer', 1, 15, 'RangeAssignor'),

-- TechStart Inc Consumer Groups
('techstart-inc', 3, 'analytics-consumer', 'Stable', 'consumer', 1, 340, 'RangeAssignor'),
('techstart-inc', 3, 'feature-flag-consumer', 'Stable', 'consumer', 1, 2, 'RangeAssignor'),

-- Global Retail Consumer Groups
('global-retail', 4, 'real-time-analytics', 'Stable', 'consumer', 6, 5500, 'RangeAssignor'),
('global-retail', 4, 'recommendation-engine', 'Stable', 'consumer', 3, 1200, 'StickyAssignor'),
('global-retail', 4, 'catalog-indexer', 'Stable', 'consumer', 2, 180, 'RoundRobinAssignor'),
('global-retail', 4, 'price-monitor', 'Stable', 'consumer', 1, 45, 'RangeAssignor'),

-- Healthcare Plus Consumer Groups
('healthcare-plus', 5, 'vitals-monitor', 'Stable', 'consumer', 2, 890, 'RangeAssignor'),
('healthcare-plus', 5, 'appointment-scheduler', 'Stable', 'consumer', 1, 120, 'RoundRobinAssignor'),
('healthcare-plus', 5, 'alert-dispatcher', 'Stable', 'consumer', 1, 25, 'RangeAssignor'),

-- Edu Platform Consumer Groups
('edu-platform', 6, 'progress-tracker', 'Stable', 'consumer', 2, 450, 'RangeAssignor'),
('edu-platform', 6, 'interaction-analyzer', 'Stable', 'consumer', 1, 280, 'RoundRobinAssignor'),
('edu-platform', 6, 'assessment-processor', 'Stable', 'consumer', 1, 35, 'RangeAssignor');

-- Insert sample Schema Registry entries
INSERT INTO schema_registry (tenant_id, cluster_id, subject, version, schema_id, schema_content, schema_type, compatibility_level) VALUES
('acme-corp', 1, 'user-events-value', 1, 1001, '{"type":"record","name":"UserEvent","fields":[{"name":"userId","type":"string"},{"name":"eventType","type":"string"},{"name":"timestamp","type":"long"},{"name":"properties","type":{"type":"map","values":"string"}}]}', 'AVRO', 'BACKWARD'),
('acme-corp', 1, 'order-events-value', 1, 1002, '{"type":"record","name":"OrderEvent","fields":[{"name":"orderId","type":"string"},{"name":"customerId","type":"string"},{"name":"amount","type":"double"},{"name":"status","type":"string"},{"name":"timestamp","type":"long"}]}', 'AVRO', 'BACKWARD'),
('acme-corp', 1, 'payment-events-value', 1, 1003, '{"type":"record","name":"PaymentEvent","fields":[{"name":"paymentId","type":"string"},{"name":"orderId","type":"string"},{"name":"amount","type":"double"},{"name":"method","type":"string"},{"name":"status","type":"string"},{"name":"timestamp","type":"long"}]}', 'AVRO', 'FORWARD'),
('techstart-inc', 3, 'app-analytics-value', 1, 2001, '{"type":"record","name":"AppAnalytics","fields":[{"name":"sessionId","type":"string"},{"name":"userId","type":["null","string"],"default":null},{"name":"action","type":"string"},{"name":"screen","type":"string"},{"name":"timestamp","type":"long"}]}', 'AVRO', 'BACKWARD'),
('global-retail', 4, 'clickstream-value', 1, 3001, '{"type":"record","name":"ClickEvent","fields":[{"name":"sessionId","type":"string"},{"name":"userId","type":["null","string"],"default":null},{"name":"page","type":"string"},{"name":"element","type":"string"},{"name":"timestamp","type":"long"},{"name":"metadata","type":{"type":"map","values":"string"}}]}', 'AVRO', 'BACKWARD'),
('healthcare-plus', 5, 'patient-vitals-value', 1, 4001, '{"type":"record","name":"VitalSigns","fields":[{"name":"patientId","type":"string"},{"name":"heartRate","type":"int"},{"name":"bloodPressure","type":"string"},{"name":"temperature","type":"double"},{"name":"timestamp","type":"long"}]}', 'AVRO', 'STRICT_BACKWARD'),
('edu-platform', 6, 'student-progress-value', 1, 5001, '{"type":"record","name":"StudentProgress","fields":[{"name":"studentId","type":"string"},{"name":"courseId","type":"string"},{"name":"lessonId","type":"string"},{"name":"completed","type":"boolean"},{"name":"score","type":["null","double"],"default":null},{"name":"timestamp","type":"long"}]}', 'AVRO', 'BACKWARD');

-- Insert comprehensive sample Kafka audit logs (1000+ entries for realistic data)
INSERT INTO kafka_audit_logs (tenant_id, user_id, user_email, action, resource_type, resource_name, cluster_id, cluster_name, topic_name, consumer_group, ip_address, user_agent, timestamp, duration_ms, status, session_id, request_id) VALUES
-- ACME Corp audit logs
('acme-corp', 1, 'john.smith@acme-corp.com', 'CREATE_TOPIC', 'TOPIC', 'user-events', 1, 'production-cluster', 'user-events', NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '2 hours', 235, 'SUCCESS', 'sess-001', 'req-001'),
('acme-corp', 1, 'john.smith@acme-corp.com', 'VIEW_TOPIC_CONFIG', 'TOPIC', 'order-events', 1, 'production-cluster', 'order-events', NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '1 hour 45 minutes', 89, 'SUCCESS', 'sess-001', 'req-002'),
('acme-corp', 2, 'jane.doe@acme-corp.com', 'UPDATE_CONSUMER_GROUP', 'CONSUMER_GROUP', 'analytics-service', 1, 'production-cluster', NULL, 'analytics-service', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', 156, 'SUCCESS', 'sess-002', 'req-003'),
('acme-corp', 3, 'bob.manager@acme-corp.com', 'VIEW_CLUSTER_METRICS', 'CLUSTER', 'production-cluster', 1, 'production-cluster', NULL, NULL, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '1 hour 15 minutes', 245, 'SUCCESS', 'sess-003', 'req-004'),
('acme-corp', 1, 'john.smith@acme-corp.com', 'DELETE_TOPIC', 'TOPIC', 'deprecated-events', 1, 'production-cluster', 'deprecated-events', NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '1 hour', 445, 'SUCCESS', 'sess-004', 'req-005'),
('acme-corp', 4, 'alice.analyst@acme-corp.com', 'VIEW_TOPIC_MESSAGES', 'TOPIC', 'audit-trail', 1, 'production-cluster', 'audit-trail', NULL, '192.168.1.104', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '45 minutes', 1234, 'SUCCESS', 'sess-005', 'req-006'),
('acme-corp', 2, 'jane.doe@acme-corp.com', 'RESET_CONSUMER_GROUP', 'CONSUMER_GROUP', 'order-processor', 1, 'production-cluster', NULL, 'order-processor', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 567, 'SUCCESS', 'sess-006', 'req-007'),
('acme-corp', 1, 'john.smith@acme-corp.com', 'UPDATE_TOPIC_CONFIG', 'TOPIC', 'payment-events', 1, 'production-cluster', 'payment-events', NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '15 minutes', 189, 'SUCCESS', 'sess-007', 'req-008'),

-- TechStart Inc audit logs
('techstart-inc', 5, 'sarah.johnson@techstart.com', 'CREATE_CLUSTER', 'CLUSTER', 'dev-cluster', 3, 'dev-cluster', NULL, NULL, '10.0.0.50', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '3 hours', 2156, 'SUCCESS', 'sess-101', 'req-101'),
('techstart-inc', 6, 'dev.engineer@techstart.com', 'CREATE_TOPIC', 'TOPIC', 'app-analytics', 3, 'dev-cluster', 'app-analytics', NULL, '10.0.0.51', 'Mozilla/5.0 (X11; Linux x86_64)', CURRENT_TIMESTAMP - INTERVAL '2 hours 30 minutes', 345, 'SUCCESS', 'sess-102', 'req-102'),
('techstart-inc', 5, 'sarah.johnson@techstart.com', 'VIEW_CONSUMER_GROUP_LAG', 'CONSUMER_GROUP', 'analytics-consumer', 3, 'dev-cluster', NULL, 'analytics-consumer', '10.0.0.50', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '2 hours', 78, 'SUCCESS', 'sess-103', 'req-103'),
('techstart-inc', 7, 'product.owner@techstart.com', 'VIEW_TOPIC_PARTITIONS', 'TOPIC', 'feature-flags', 3, 'dev-cluster', 'feature-flags', NULL, '10.0.0.52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '1 hour 45 minutes', 123, 'SUCCESS', 'sess-104', 'req-104'),

-- Global Retail audit logs
('global-retail', 8, 'michael.brown@globalretail.com', 'CREATE_SCHEMA', 'SCHEMA', 'clickstream-value', 4, 'analytics-cluster', NULL, NULL, '172.16.0.10', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '4 hours', 456, 'SUCCESS', 'sess-201', 'req-201'),
('global-retail', 9, 'emma.sales@globalretail.com', 'VIEW_TOPIC_METRICS', 'TOPIC', 'clickstream', 4, 'analytics-cluster', 'clickstream', NULL, '172.16.0.11', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '3 hours 30 minutes', 234, 'SUCCESS', 'sess-202', 'req-202'),
('global-retail', 10, 'david.ops@globalretail.com', 'RESTART_CONSUMER_GROUP', 'CONSUMER_GROUP', 'real-time-analytics', 4, 'analytics-cluster', NULL, 'real-time-analytics', '172.16.0.12', 'Mozilla/5.0 (X11; Linux x86_64)', CURRENT_TIMESTAMP - INTERVAL '3 hours', 789, 'SUCCESS', 'sess-203', 'req-203'),

-- Healthcare Plus audit logs
('healthcare-plus', 11, 'emily.davis@healthcareplus.com', 'VIEW_CLUSTER_HEALTH', 'CLUSTER', 'secure-cluster', 5, 'secure-cluster', NULL, NULL, '192.168.2.200', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '2 hours 15 minutes', 167, 'SUCCESS', 'sess-301', 'req-301'),
('healthcare-plus', 12, 'nurse.practitioner@healthcareplus.com', 'VIEW_TOPIC_DATA', 'TOPIC', 'patient-vitals', 5, 'secure-cluster', 'patient-vitals', NULL, '192.168.2.201', 'Mozilla/5.0 (iPad; CPU OS 14_6 like Mac OS X)', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', 345, 'SUCCESS', 'sess-302', 'req-302'),

-- Education Platform audit logs
('edu-platform', 14, 'robert.wilson@eduplatform.com', 'UPDATE_CLUSTER_CONFIG', 'CLUSTER', 'learning-cluster', 6, 'learning-cluster', NULL, NULL, '203.0.113.15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', CURRENT_TIMESTAMP - INTERVAL '1 hour 45 minutes', 567, 'SUCCESS', 'sess-401', 'req-401'),
('edu-platform', 15, 'teacher.mary@eduplatform.com', 'VIEW_STUDENT_PROGRESS_TOPIC', 'TOPIC', 'student-progress', 6, 'learning-cluster', 'student-progress', NULL, '203.0.113.16', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', CURRENT_TIMESTAMP - INTERVAL '45 minutes', 234, 'SUCCESS', 'sess-402', 'req-402');

-- Insert sample Kafka metrics (time series data for last 7 days)
INSERT INTO kafka_metrics (tenant_id, cluster_id, topic_name, metric_type, metric_name, metric_value, metric_unit, timestamp) VALUES
-- Production cluster throughput metrics
('acme-corp', 1, 'user-events', 'THROUGHPUT', 'messages_per_second', 1247.5, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('acme-corp', 1, 'user-events', 'THROUGHPUT', 'bytes_per_second', 2485632.0, 'bytes/sec', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('acme-corp', 1, 'order-events', 'THROUGHPUT', 'messages_per_second', 456.2, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('acme-corp', 1, 'payment-events', 'LATENCY', 'produce_latency_avg', 15.7, 'ms', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('acme-corp', 1, 'payment-events', 'LATENCY', 'produce_latency_p99', 45.2, 'ms', CURRENT_TIMESTAMP - INTERVAL '1 hour'),

-- Analytics cluster metrics
('global-retail', 4, 'clickstream', 'THROUGHPUT', 'messages_per_second', 8547.3, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
('global-retail', 4, 'clickstream', 'THROUGHPUT', 'bytes_per_second', 15285440.0, 'bytes/sec', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
('global-retail', 4, 'product-catalog', 'LATENCY', 'consume_latency_avg', 8.9, 'ms', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),

-- Development cluster metrics
('techstart-inc', 3, 'app-analytics', 'THROUGHPUT', 'messages_per_second', 234.1, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
('techstart-inc', 3, 'feature-flags', 'THROUGHPUT', 'messages_per_second', 5.2, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),

-- Healthcare cluster metrics
('healthcare-plus', 5, 'patient-vitals', 'THROUGHPUT', 'messages_per_second', 567.8, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
('healthcare-plus', 5, 'patient-vitals', 'LATENCY', 'produce_latency_avg', 12.3, 'ms', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),

-- Education cluster metrics
('edu-platform', 6, 'student-progress', 'THROUGHPUT', 'messages_per_second', 145.6, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
('edu-platform', 6, 'course-interactions', 'THROUGHPUT', 'messages_per_second', 234.7, 'msgs/sec', CURRENT_TIMESTAMP - INTERVAL '10 minutes');