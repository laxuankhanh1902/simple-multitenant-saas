package io.conduktor.saas.security;

import io.conduktor.saas.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey jwtSecret;
    private final int jwtExpirationInMs;
    private final int refreshExpirationInMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String jwtSecret,
                           @Value("${app.jwt.expiration}") int jwtExpirationInMs,
                           @Value("${app.jwt.refresh-expiration}") int refreshExpirationInMs) {
        this.jwtSecret = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = jwtExpirationInMs;
        this.refreshExpirationInMs = refreshExpirationInMs;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationInMs, ChronoUnit.MILLIS);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .toList();

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .claim("username", userPrincipal.getUsername())
                .claim("email", userPrincipal.getEmail())
                .claim("tenantId", userPrincipal.getTenantId())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(jwtSecret, Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Instant now = Instant.now();
        Instant expiryDate = now.plus(refreshExpirationInMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .claim("type", "refresh")
                .claim("tenantId", userPrincipal.getTenantId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(jwtSecret, Jwts.SIG.HS512)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("username", String.class);
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("email", String.class);
    }

    public String getTenantIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tenantId", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", List.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpirationDateFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    // Additional methods needed by AuthService
    public String createToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationInMs, ChronoUnit.MILLIS);

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("tenantId", user.getTenantId())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(jwtSecret, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(refreshExpirationInMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .claim("type", "refresh")
                .claim("username", user.getUsername())
                .claim("tenantId", user.getTenantId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(jwtSecret, Jwts.SIG.HS512)
                .compact();
    }

    public String getUsername(String token) {
        return getUsernameFromJWT(token);
    }

    public String getTenantId(String token) {
        return getTenantIdFromJWT(token);
    }

    public Long getAccessTokenValidityInSeconds() {
        return (long) jwtExpirationInMs / 1000;
    }

    public Long getRefreshTokenValidityInSeconds() {
        return (long) refreshExpirationInMs / 1000;
    }
}