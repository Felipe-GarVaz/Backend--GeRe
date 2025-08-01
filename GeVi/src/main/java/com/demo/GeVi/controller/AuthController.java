package com.demo.GeVi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.demo.GeVi.dto.LoginRequestDTO;
import com.demo.GeVi.dto.LoginResponseDTO;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.UserRepository;
import com.demo.GeVi.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getRpe(),
                        loginRequest.getPassword()));

        // GENERAR TOKEN JWT
        String token = jwtUtil.generateToken(loginRequest.getRpe());

        // OBTENER NOMBRE DE LA BASE DE DATOS
        User user = userRepository.findByRpe(loginRequest.getRpe())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // REATORNAR TOKEN Y NOMBRE
        LoginResponseDTO response = new LoginResponseDTO(token, user.getName() + " " + user.getLastName());
        return ResponseEntity.ok(response);
    }

}
