package com.demo.GeVi.controller;

import com.demo.GeVi.dto.LoginRequestDTO;
import com.demo.GeVi.dto.LoginResponseDTO;
import com.demo.GeVi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticación.
 * Mantiene el controlador delgado: delega la lógica en el servicio.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        /**
         * Valida credenciales y devuelve JWT + nombre y roles.
         */
        @PostMapping("/login")
        public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
                log.debug("Intento de login para RPE {}", body.rpe());
                LoginResponseDTO response = authService.login(body);
                return ResponseEntity.ok(response);
        }
}
