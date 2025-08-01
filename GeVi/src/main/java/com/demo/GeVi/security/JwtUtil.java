package com.demo.GeVi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private String SECRET_KEY_STRING = "YHXxRbwkCmM8rxqzAHGE2xkp8iDLn1Sm0UPz04cJ6TZ1O4MJKC6R8FTxDccOHODD49KTxPLKo7K9cu93Pk9OQA==";
    private long EXPIRATION_TIME = 86400000; // 1 día en ms

    private Key secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    public String generateToken(String rpe) {
        return Jwts.builder()
                .setSubject(rpe)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

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

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
    final Date expiration = extractAllClaims(token).getExpiration();
    return expiration.before(new Date());
}


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}