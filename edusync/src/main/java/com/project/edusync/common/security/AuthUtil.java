package com.project.edusync.common.security;

import com.project.edusync.iam.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; // Standard Spring/Jakarta annotation
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
import java.util.stream.Collectors;

@Component
public class AuthUtil {

    private final UserRepository userRepository;
    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value(("${app.jwt.expirationTime}"))
    private String jwtExpirationTime; // Kept as String as in your code

    // We will parse and cache the key for performance and security
    private SecretKey _signingKey;

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method runs once after the bean is constructed.
     * It parses the string key into a secure SecretKey object.
     */
    @PostConstruct
    public void init() {
        this._signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * A private getter to retrieve the initialized, secure key.
     */
    private SecretKey getSigningKey() {
        return this._signingKey;
    }

    /**
     * Creates a JWT token. This method is now DECOUPLED from your User entity.
     *
     * @param username The user's username (the 'subject' of the token).
     * @param authorities The collection of authorities (permissions) to embed.
     */
    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {

        // 1. Convert authorities to a simple List<String> for the claim
        List<String> authorityStrings = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 2. Create a 'claims' map to store the authorities
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorityStrings); // "authorities" is our custom claim key

        // 3. BUG FIX: Parse expiration time from String to long
        long expirationTimeMs = Long.parseLong(jwtExpirationTime);

        // 4. Build the token
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
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
     * NEW METHOD: Extracts authorities directly from the token's claims.
     * NO DATABASE CALL.
     */
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        // This requires a type-safe cast
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}