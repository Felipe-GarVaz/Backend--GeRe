package com.demo.GeVi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY_STRING = "YHXxRbwkCmM8rxqzAHGE2xkp8iDLn1Sm0UPz04cJ6TZ1O4MJKC6R8FTxDccOHODD49KTxPLKo7K9cu93Pk9OQA==";
    private final long EXPIRATION_TIME = 86400000; // 1 día
    private final Key secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    /*
     * Genera un token JWT con RPE como subject.
     */
    public String generateToken(String rpe) {
        return Jwts.builder()
                .setSubject(rpe)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /*
     * Obtiene el RPE desde el token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /*
     * Valida si el token es correcto (firma y estructura).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /*
     * Valida el token y que pertenezca al usuario dado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /*
     * Verifica si el token ya expiró.
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /*
     * Extrae todos los claims del token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
