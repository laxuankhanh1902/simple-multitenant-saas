-- Initialize additional databases for development and testing
CREATE DATABASE saas_framework_dev;
CREATE DATABASE saas_framework_test;

-- Grant permissions to postgres user
GRANT ALL PRIVILEGES ON DATABASE saas_framework TO postgres;
GRANT ALL PRIVILEGES ON DATABASE saas_framework_dev TO postgres;
GRANT ALL PRIVILEGES ON DATABASE saas_framework_test TO postgres;