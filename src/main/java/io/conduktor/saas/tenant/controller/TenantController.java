package io.conduktor.saas.tenant.controller;

import io.conduktor.saas.common.dto.ApiResponse;
import io.conduktor.saas.common.dto.PageResponse;
import io.conduktor.saas.common.dto.SearchRequest;
import io.conduktor.saas.tenant.dto.*;
import io.conduktor.saas.tenant.entity.Tenant;
import io.conduktor.saas.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tenants")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Tenants", description = "Tenant management API")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @Operation(summary = "Get all tenants", description = "Retrieve paginated list of tenants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<TenantDTO>>> getAllTenants(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Tenant> tenants = tenantService.findAll(null, pageable);
        PageResponse<TenantDTO> tenantDTOs = new PageResponse<>(
            tenants.map(TenantDTO::new)
        );
        
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", tenantDTOs));
    }

    @GetMapping("/search")
    @Operation(summary = "Search tenants", description = "Search tenants by query string")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<TenantDTO>>> searchTenants(
            @Valid @ModelAttribute SearchRequest searchRequest) {
        
        if (!searchRequest.hasQuery()) {
            return getAllTenants(searchRequest.toPageable());
        }

        List<Tenant> tenants = tenantService.searchTenants(searchRequest.getQuery());
        List<TenantDTO> tenantDTOs = tenants.stream()
            .map(TenantDTO::new)
            .collect(Collectors.toList());
        
        // Create a mock page response for search results
        PageResponse<TenantDTO> response = new PageResponse<>();
        response.setContent(tenantDTOs);
        response.setTotalElements(tenantDTOs.size());
        response.setPageSize(searchRequest.getSize());
        response.setPageNumber(searchRequest.getPage());
        response.setTotalPages((int) Math.ceil((double) tenantDTOs.size() / searchRequest.getSize()));
        response.setFirst(searchRequest.getPage() == 0);
        response.setLast(searchRequest.getPage() >= response.getTotalPages() - 1);
        response.setEmpty(tenantDTOs.isEmpty());

        return ResponseEntity.ok(ApiResponse.success("Tenants found", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Retrieve a tenant by their ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> getTenantById(
            @Parameter(description = "Tenant ID") @PathVariable Long id) {
        
        Tenant tenant = tenantService.findById(id);
        TenantDTO tenantDTO = new TenantDTO(tenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", tenantDTO));
    }

    @GetMapping("/subdomain/{subdomain}")
    @Operation(summary = "Get tenant by subdomain", description = "Retrieve a tenant by their subdomain")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> getTenantBySubdomain(
            @Parameter(description = "Tenant subdomain") @PathVariable String subdomain) {
        
        return tenantService.findBySubdomain(subdomain)
            .map(tenant -> ResponseEntity.ok(ApiResponse.success("Tenant retrieved successfully", new TenantDTO(tenant))))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new tenant", description = "Create a new tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        
        if (tenantService.existsBySubdomain(request.getSubdomain())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Subdomain already exists"));
        }
        
        if (tenantService.existsByName(request.getName())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Tenant name already exists"));
        }

        Tenant tenant = tenantService.create(request.toEntity());
        TenantDTO tenantDTO = new TenantDTO(tenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant created successfully", tenantDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant", description = "Update an existing tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenant(
            @Parameter(description = "Tenant ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        
        Tenant existingTenant = tenantService.findById(id);
        
        // Check for name conflicts
        if (request.getName() != null && 
            !request.getName().equals(existingTenant.getName()) &&
            tenantService.existsByName(request.getName())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Tenant name already exists"));
        }

        request.updateEntity(existingTenant);
        Tenant updatedTenant = tenantService.update(id, existingTenant);
        TenantDTO tenantDTO = new TenantDTO(updatedTenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", tenantDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant", description = "Delete a tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @Parameter(description = "Tenant ID") @PathVariable Long id) {
        
        tenantService.deleteById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update tenant status", description = "Update a tenant's status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenantStatus(
            @Parameter(description = "Tenant ID") @PathVariable Long id,
            @RequestParam Tenant.TenantStatus status) {
        
        Tenant tenant = tenantService.updateStatus(id, status);
        TenantDTO tenantDTO = new TenantDTO(tenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant status updated successfully", tenantDTO));
    }

    @PatchMapping("/{id}/extend-trial")
    @Operation(summary = "Extend tenant trial", description = "Extend a tenant's trial period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> extendTrial(
            @Parameter(description = "Tenant ID") @PathVariable Long id,
            @RequestParam(defaultValue = "30") int additionalDays) {
        
        try {
            Tenant tenant = tenantService.extendTrial(id, additionalDays);
            TenantDTO tenantDTO = new TenantDTO(tenant);
            
            return ResponseEntity.ok(ApiResponse.success("Trial extended successfully", tenantDTO));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/limits")
    @Operation(summary = "Update tenant limits", description = "Update a tenant's resource limits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> updateLimits(
            @Parameter(description = "Tenant ID") @PathVariable Long id,
            @RequestParam(required = false) Integer maxUsers,
            @RequestParam(required = false) Integer storageLimitGb,
            @RequestParam(required = false) Integer apiRateLimit) {
        
        Tenant tenant = tenantService.updateLimits(id, maxUsers, storageLimitGb, apiRateLimit);
        TenantDTO tenantDTO = new TenantDTO(tenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant limits updated successfully", tenantDTO));
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend tenant", description = "Suspend a tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> suspendTenant(
            @Parameter(description = "Tenant ID") @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        
        Tenant tenant = tenantService.suspend(id, reason);
        TenantDTO tenantDTO = new TenantDTO(tenant);
        
        return ResponseEntity.ok(ApiResponse.success("Tenant suspended successfully", tenantDTO));
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate tenant", description = "Reactivate a suspended tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantDTO>> reactivateTenant(
            @Parameter(description = "Tenant ID") @PathVariable Long id) {
        
        try {
            Tenant tenant = tenantService.reactivate(id);
            TenantDTO tenantDTO = new TenantDTO(tenant);
            
            return ResponseEntity.ok(ApiResponse.success("Tenant reactivated successfully", tenantDTO));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tenants by status", description = "Get all tenants with a specific status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getTenantsByStatus(
            @Parameter(description = "Tenant status") @PathVariable Tenant.TenantStatus status) {
        
        List<Tenant> tenants = tenantService.findByStatus(status);
        List<TenantDTO> tenantDTOs = tenants.stream()
            .map(TenantDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", tenantDTOs));
    }

    @GetMapping("/trial/expiring")
    @Operation(summary = "Get expiring trials", description = "Get tenants with trials expiring in N days")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getExpiringTrials(
            @RequestParam(defaultValue = "7") int days) {
        
        List<Tenant> tenants = tenantService.findExpiringTrials(days);
        List<TenantDTO> tenantDTOs = tenants.stream()
            .map(TenantDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Expiring trials retrieved successfully", tenantDTOs));
    }

    @GetMapping("/trial/expired")
    @Operation(summary = "Get expired trials", description = "Get tenants with expired trials")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getExpiredTrials() {
        
        List<Tenant> tenants = tenantService.findExpiredTrials();
        List<TenantDTO> tenantDTOs = tenants.stream()
            .map(TenantDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Expired trials retrieved successfully", tenantDTOs));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently created tenants", description = "Get tenants created in the last N days")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getRecentTenants(
            @RequestParam(defaultValue = "30") int days) {
        
        List<Tenant> tenants = tenantService.findRecentlyCreated(days);
        List<TenantDTO> tenantDTOs = tenants.stream()
            .map(TenantDTO::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Recent tenants retrieved successfully", tenantDTOs));
    }

    @GetMapping("/validate/subdomain/{subdomain}")
    @Operation(summary = "Validate subdomain availability", description = "Check if a subdomain is available")
    public ResponseEntity<ApiResponse<Boolean>> validateSubdomain(
            @Parameter(description = "Subdomain to validate") @PathVariable String subdomain) {
        
        boolean available = tenantService.isSubdomainAvailable(subdomain);
        
        return ResponseEntity.ok(ApiResponse.success(
            available ? "Subdomain is available" : "Subdomain is taken", 
            available
        ));
    }

    @GetMapping("/validate/name/{name}")
    @Operation(summary = "Validate name availability", description = "Check if a tenant name is available")
    public ResponseEntity<ApiResponse<Boolean>> validateName(
            @Parameter(description = "Name to validate") @PathVariable String name) {
        
        boolean available = tenantService.isNameAvailable(name);
        
        return ResponseEntity.ok(ApiResponse.success(
            available ? "Name is available" : "Name is taken", 
            available
        ));
    }

    @PostMapping("/process-expired-trials")
    @Operation(summary = "Process expired trials", description = "Process all expired trials and update their status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> processExpiredTrials() {
        
        tenantService.processExpiredTrials();
        
        return ResponseEntity.ok(ApiResponse.success("Expired trials processed successfully"));
    }

    @GetMapping("/export")
    @Operation(summary = "Export tenants to CSV", description = "Export all tenants to CSV format")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportTenants() {
        try {
            List<Tenant> tenants = tenantService.findAll();
            
            StringWriter stringWriter = new StringWriter();
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Name", "Subdomain", "Admin Email", "Admin Name", 
                         "Status", "Trial End Date", "Max Users", "Storage Limit GB", 
                         "API Rate Limit", "Created At")
                .build();
            
            try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, csvFormat)) {
                for (Tenant tenant : tenants) {
                    csvPrinter.printRecord(
                        tenant.getId(),
                        tenant.getName(),
                        tenant.getSubdomain(),
                        tenant.getAdminEmail(),
                        (tenant.getAdminFirstName() != null ? tenant.getAdminFirstName() + " " : "") +
                        (tenant.getAdminLastName() != null ? tenant.getAdminLastName() : ""),
                        tenant.getStatus(),
                        tenant.getTrialEndDate(),
                        tenant.getMaxUsers(),
                        tenant.getStorageLimitGb(),
                        tenant.getApiRateLimit(),
                        tenant.getCreatedAt()
                    );
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "tenants.csv");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(stringWriter.toString());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}