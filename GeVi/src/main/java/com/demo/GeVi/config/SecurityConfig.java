package com.demo.GeVi.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.demo.GeVi.security.JwtAuthFilter;
import com.demo.GeVi.security.JwtUtil;

@Configuration
public class SecurityConfig {

    /*
     * Filtro personalizado para validar JWT
     */
    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }

    /*
     * Configuración principal de la cadena de seguridad
     */
    @Bean
 public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Preflight CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth público
                .requestMatchers("/auth/**").permitAll()

                // Endpoints protegidos por rol
                .requestMatchers(HttpMethod.POST,   "/api/vehicles/**").hasRole("ADMIN")   // crear
                .requestMatchers(HttpMethod.PUT,    "/api/vehicles/**").hasRole("ADMIN")   // editar (si aplica)
                .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**").hasRole("ADMIN")   // eliminar

                // Resto del API requiere estar autenticado
                .requestMatchers("/api/**").authenticated()

                // Cualquier otra ruta (si tienes estáticos, docs, etc.)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Expone el AuthenticationManager necesario para autenticación manual
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * Codificar Contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Configuración global para CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://172.20.10.2:3000",
                "http://192.168.1.64:3000",
                "http://10.16.118.223:3000",
                "http://192.168.1.64:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true); // Permite el uso de cookies o tokens con CORS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica configuración a todas las rutas
        return source;
    }
}
