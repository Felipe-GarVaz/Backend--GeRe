package com.demo.GeVi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {

    private static String SECRET_KEY_B64 = Optional.ofNullable(System.getenv("JWT_SECRET_BASE64"))
            .orElse("YHXxRbwkCmM8rxqzAHGE2xkp8iDLn1Sm0UPz04cJ6TZ1O4MJKC6R8FTxDccOHODD49KTxPLKo7K9cu93Pk9OQA==");

    private static long EXPIRATION_MS = Duration.ofDays(1).toMillis(); // 1 día
    private static long ALLOWED_SKEW_SEC = 60; // tolerancia de reloj

    private Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_B64));

    /*
     * =========================
     * Creación de Tokens
     * =========================
     */

    /** Genera un token simple con el RPE como subject (sin roles). */
    public String generateToken(String rpe) {
        return createToken(new HashMap<>(), rpe);
    }

    /** Genera un token con claim "roles": ["ADMIN","USER"]. */
    public String generateTokenWithRoles(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, username);
    }

    /** Implementación central para construir el JWT. */
    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /*
     * =========================
     * Lectura y Validaciones
     * =========================
     */

    /** Obtiene el RPE (subject) del token. */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extrae todos los claims del token. */
    public Claims extractAllClaims(String token) {
        return parseClaims(token);
    }

    /** Extrae la lista de roles del claim "roles". */
    public List<String> extractRoles(String token) {
        Object raw = parseClaims(token).get("roles");
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    /** Valida firma/estructura/expiración. */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Valida token y que pertenezca al usuario indicado. */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /** Verifica expiración. */
    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    /*
     * =========================
     * Helpers internos
     * =========================
     */

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .setAllowedClockSkewSeconds(ALLOWED_SKEW_SEC) // tolera pequeños desfasajes de reloj
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
