package io.conduktor.saas.security;

import org.slf4j.MDC;

public class TenantContext {
    
    private static final String TENANT_ID_KEY = "tenantId";
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    public static void setCurrentTenantId(String tenantId) {
        currentTenant.set(tenantId);
        MDC.put(TENANT_ID_KEY, tenantId);
    }
    
    public static String getCurrentTenantId() {
        return currentTenant.get();
    }
    
    public static String getCurrentTenant() {
        return getCurrentTenantId();
    }
    
    public static void clear() {
        currentTenant.remove();
        MDC.remove(TENANT_ID_KEY);
    }
    
    public static boolean hasTenantContext() {
        return getCurrentTenantId() != null;
    }
}