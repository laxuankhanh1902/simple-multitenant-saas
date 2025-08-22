package io.conduktor.saas.user.service;

import io.conduktor.saas.core.exception.ResourceNotFoundException;
import io.conduktor.saas.core.service.BaseService;
import io.conduktor.saas.security.TenantContext;
import io.conduktor.saas.security.UserPrincipal;
import io.conduktor.saas.user.entity.User;
import io.conduktor.saas.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
// @Transactional
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    protected String getCurrentTenantId() {
        String tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalStateException("No tenant context available");
        }
        return tenantId;
    }

    // @Transactional(readOnly = true)
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    // @Transactional(readOnly = true)
    public List<User> findAll(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

//    // @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByTenantIdAndUsername(tenantId, username);
    }

    // @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByTenantIdAndEmail(tenantId, email);
    }

    // @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        String tenantId = getCurrentTenantId();
        return userRepository.existsByTenantIdAndUsername(tenantId, username);
    }

    // @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        String tenantId = getCurrentTenantId();
        return userRepository.existsByTenantIdAndEmail(tenantId, email);
    }

    // @Transactional(readOnly = true)
    public List<User> findByRole(User.Role role) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByTenantIdAndRolesContaining(tenantId, role);
    }

    // @Transactional(readOnly = true)
    public List<User> findByStatus(User.UserStatus status) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByTenantIdAndStatus(tenantId, status);
    }

    // @Transactional(readOnly = true)
    public long countByTenant(String tenantId) {
        return userRepository.countByTenantId(tenantId);
    }

    // @Transactional(readOnly = true)
    public long countActiveByTenant(String tenantId) {
        return userRepository.countByTenantIdAndStatus(tenantId, User.UserStatus.ACTIVE);
    }

    // @Transactional(readOnly = true)
    public User findById(Long id) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // @Transactional
    public void deleteById(Long id) {
        String tenantId = getCurrentTenantId();
        if (!userRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteByIdAndTenantId(id, tenantId);
    }

    // @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findByTenantId(getCurrentTenantId());
    }

    public User create(User user) {
        user.setTenantId(getCurrentTenantId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // For authentication, we need to find user across all tenants initially
        // Then we'll use TenantFilter to set the tenant context
        Optional<User> userOpt = findByUsernameGlobal(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        User user = userOpt.get();
        List<String> roles = user.getRoles().stream()
                .map(role -> role.name())
                .toList();
        
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getTenantId(),
                roles,
                user.isEnabled() && user.isAccountNonLocked()
        );
    }
    
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> 
            new UsernameNotFoundException("User not found with id: " + id));
            
        List<String> roles = user.getRoles().stream()
                .map(role -> role.name())
                .toList();
        
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getTenantId(),
                roles,
                user.isEnabled() && user.isAccountNonLocked()
        );
    }
    
    // @Transactional(readOnly = true)
    public Optional<User> findByUsernameGlobal(String username) {
        // This method searches across all tenants for authentication
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public User createWithRoles(User user, Set<User.Role> roles) {
        user.setRoles(roles);
        return create(user);
    }

    public User update(Long id, User updatedUser) {
        User existingUser = findById(id);
        
        // Update fields but preserve password if not provided
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
        existingUser.setTimezone(updatedUser.getTimezone());
        existingUser.setLocale(updatedUser.getLocale());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setEnabled(updatedUser.isEnabled());
        existingUser.setEmailVerified(updatedUser.isEmailVerified());
        
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        return userRepository.save(existingUser);
    }

    public User changePassword(Long id, String oldPassword, String newPassword) {
        User user = findById(id);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User updateRoles(Long id, Set<User.Role> roles) {
        User user = findById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User updateStatus(Long id, User.UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    public User lockAccount(Long id, LocalDateTime lockUntil) {
        User user = findById(id);
        user.setAccountLockedUntil(lockUntil);
        user.setEnabled(false);
        return userRepository.save(user);
    }

    public User unlockAccount(Long id) {
        User user = findById(id);
        user.setAccountLockedUntil(null);
        user.setEnabled(true);
        user.setFailedLoginAttempts(0);
        return userRepository.save(user);
    }

    public User recordLogin(String username) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            user.setLoginCount(user.getLoginCount() + 1);
            user.setFailedLoginAttempts(0);
            return userRepository.save(user);
        }
        throw new ResourceNotFoundException("User not found with username: " + username);
    }

    public User recordLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setFailedLoginAttempts(0);
        return userRepository.save(user);
    }

    public User recordFailedLogin(String username) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                user.setEnabled(false);
            }
            
            return userRepository.save(user);
        }
        throw new ResourceNotFoundException("User not found with username: " + username);
    }

    public User verifyEmail(Long id) {
        User user = findById(id);
        user.setEmailVerified(true);
        if (user.getStatus() == User.UserStatus.PENDING_VERIFICATION) {
            user.setStatus(User.UserStatus.ACTIVE);
        }
        return userRepository.save(user);
    }

    public List<User> searchUsers(String query) {
        String tenantId = getCurrentTenantId();
        return userRepository.searchByTenantIdAndQuery(tenantId, query);
    }

    public Page<User> findActiveUsers(Pageable pageable) {
        String tenantId = getCurrentTenantId();
        return userRepository.findByTenantIdAndStatusAndEnabled(tenantId, User.UserStatus.ACTIVE, true, pageable);
    }

    public List<User> findRecentlyCreated(int days) {
        String tenantId = getCurrentTenantId();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.findByTenantIdAndCreatedAtAfter(tenantId, since);
    }
}