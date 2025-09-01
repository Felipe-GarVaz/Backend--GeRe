package com.demo.GeVi.service;

import com.demo.GeVi.dto.LoginRequestDTO;
import com.demo.GeVi.dto.LoginResponseDTO;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.UserRepository;
import com.demo.GeVi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contiene la lógica de autenticación (SRP).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO req) {
        // 1) Autenticar credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.rpe(), req.password()));

        // 2) Buscar usuario y mapear roles a String
        User user = userRepository.findByRpe(req.rpe())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getName()) // ajusta al nombre del campo en tu entidad Rol
                .toList();

        // 3) Generar token con claim de roles
        String token = jwtUtil.generateTokenWithRoles(user.getRpe(), roles);

        // 4) Construir respuesta
        String fullName = (user.getName() + " " + user.getLastName()).trim();
        return new LoginResponseDTO(token, fullName, roles);
    }
}
