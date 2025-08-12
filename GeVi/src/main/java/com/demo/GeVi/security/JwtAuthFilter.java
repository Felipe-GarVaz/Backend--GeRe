package com.demo.GeVi.security;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;


    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

    }

    /*
     * Extrae y valida el token JWT, y configura la autenticación si es válido.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            // Valida firma/estructura/expiración
            if (!jwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Construye authorities desde el claim "roles"
                var authorities = jwtUtil.extractRoles(token).stream()
                        .map(r -> "ROLE_" + r) // "ADMIN" -> "ROLE_ADMIN"
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList();

                // Puedes usar un principal simple; no es obligatorio cargar de BD
                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            // Token inválido/malformado -> seguir sin autenticar
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
