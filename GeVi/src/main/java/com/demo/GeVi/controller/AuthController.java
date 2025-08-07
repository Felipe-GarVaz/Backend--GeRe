package com.demo.GeVi.controller;

import com.demo.GeVi.dto.LoginRequestDTO;
import com.demo.GeVi.dto.LoginResponseDTO;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.UserRepository;
import com.demo.GeVi.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /*
     * Valida credenciales y retorna un token JWT junto con el nombre completo del
     * usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        // Autenticación de credenciales del usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getRpe(),
                        loginRequest.getPassword()));

        String token = jwtUtil.generateToken(loginRequest.getRpe());

        // Buscar al usuario en la base de datos por RPE
        User user = userRepository.findByRpe(loginRequest.getRpe())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Retornar token y nombre completo
        LoginResponseDTO response = new LoginResponseDTO(
                token,
                user.getName() + " " + user.getLastName());

        return ResponseEntity.ok(response);
    }
}
