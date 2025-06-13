package com.case_wallet.apirest.infrastructure.security.jwt;

import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expirationTime;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.debug("Generating token for user: {} with authorities: {}",
                userDetails.getUsername(),
                userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Username es el número de teléfono
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getToken(UserEntity user) {
        log.debug("Generating token for user: {} with role: {}", user.getPhoneNumber(), user.getRole());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("role", user.getRole().name());
        return generateToken(claims, user);
    }

    public String extractUsername(String jwt) {
        try {
            String phoneNumber = extractPhoneNumber(jwt);
            log.debug("Extracted phone number from token: {}", phoneNumber);
            return phoneNumber;
        } catch (Exception e) {
            log.error("Error extracting phone number from token: {}", e.getMessage(), e);
            return null;
        }
    }

    private String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String phoneNumber = extractUsername(jwt);
        return (phoneNumber != null &&
                phoneNumber.equals(userDetails.getUsername()) &&
                !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
