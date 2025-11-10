// File: com/project/edusync/common/security/AuthUtil.java
package com.project.edusync.common.security;

import com.project.edusync.iam.model.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthUtil {

    // --- REMOVED --- Unnecessary UserRepository dependency
    // private final UserRepository userRepository;

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${app.jwt.refresh-expirationTime}")
    private long jwtRefreshExpirationTime;

    private SecretKey _signingKey;

    @PostConstruct
    public void init() {
        this._signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getSigningKey() {
        return this._signingKey;
    }

    /**
     * Generates a short-lived Access Token containing user authorities.
     *
     * @param username The user's username (subject).
     * @param roles    The user's roles.
     * @return A signed JWT Access Token.
     */
    public String generateAccessToken(String username, Set<Role> roles) {

        // 1. Convert roles to a simple List<String> for the claim
        List<String> authorityStrings = roles.stream()
                .map(Role::getName) // Assuming Role has a getName() or similar
                .collect(Collectors.toList());

        // 2. Create a 'claims' map to store the authorities
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorityStrings);

        // 3. Build the token
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * --- NEW ---
     * Generates a long-lived Refresh Token.
     * This token ONLY contains the username and has no authorities.
     *
     * @param username The user's username (subject).
     * @return A signed JWT Refresh Token.
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts all claims from the token using the secure key.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the username (subject) from the token.
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Extracts authorities from the token's "authorities" claim.
     */
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * --- NEW ---
     * Validates a token by attempting to parse it.
     *
     * @param token The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}