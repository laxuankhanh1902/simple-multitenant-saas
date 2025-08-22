-- Create projects table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    budget DECIMAL(15, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    tags TEXT[], -- Array of tags
    metadata JSONB, -- Additional metadata as JSON
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_projects_tenant_name UNIQUE (tenant_id, name),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Create project_members table for project-user relationships
CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_project_members_project_user UNIQUE (project_id, user_id),
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_projects_tenant_id ON projects(tenant_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_owner_id ON projects(owner_id);
CREATE INDEX idx_projects_start_date ON projects(start_date);
CREATE INDEX idx_projects_end_date ON projects(end_date);
CREATE INDEX idx_projects_created_at ON projects(created_at);

CREATE INDEX idx_project_members_tenant_id ON project_members(tenant_id);
CREATE INDEX idx_project_members_project_id ON project_members(project_id);
CREATE INDEX idx_project_members_user_id ON project_members(user_id);
CREATE INDEX idx_project_members_role ON project_members(role);

-- Add constraints
ALTER TABLE projects ADD CONSTRAINT chk_project_status 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'COMPLETED'));

ALTER TABLE project_members ADD CONSTRAINT chk_project_member_role 
    CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'VIEWER'));

-- Add triggers
CREATE TRIGGER update_projects_updated_at 
    BEFORE UPDATE ON projects 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_project_members_updated_at 
    BEFORE UPDATE ON project_members 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();