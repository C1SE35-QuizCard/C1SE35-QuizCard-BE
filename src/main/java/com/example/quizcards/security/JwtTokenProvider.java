package com.example.quizcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.jwtExpirationInSec}")
    private Long jwtExpirationInSec;

    // Tạo Access Token
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Map<String, String> claims = new HashMap<>();
        claims.put("email", userPrincipal.getUsername());
        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);
        return generateToken(claims, userPrincipal, jwtExpirationInSec);
    }

    // Lấy Username từ JWT
    public String getUsernameFromJWT(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long getUserIdFromJWT(String token) {
        return Long.valueOf(extractClaim(token, Claims::getSubject));
    }

    public boolean isTokenValid(String token, UserPrincipal userDetails) {
        if (!validateToken(token)) {
            return false;
        }
        final String username = getUsernameFromJWT(token);
        return username.equals(userDetails.getUsername());
    }


    // Xác thực JWT Token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty");
        }
        return false;
    }

    private String generateToken(Map<String, String> extraClaims,
                                 UserPrincipal user,
                                 long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

