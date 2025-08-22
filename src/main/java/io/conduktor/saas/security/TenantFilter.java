package io.conduktor.saas.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_PARAM = "tenantId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String tenantId = extractTenantId(httpRequest);
            
            if (StringUtils.hasText(tenantId)) {
                TenantContext.setCurrentTenantId(tenantId);
            } else {
                tenantId = extractTenantIdFromAuthentication();
                if (StringUtils.hasText(tenantId)) {
                    TenantContext.setCurrentTenantId(tenantId);
                }
            }

            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (!StringUtils.hasText(tenantId)) {
            tenantId = request.getParameter(TENANT_PARAM);
        }
        return tenantId;
    }

    private String extractTenantIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getTenantId();
        }
        return null;
    }
}