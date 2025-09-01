package com.demo.GeVi.dto;

import jakarta.validation.constraints.NotBlank;

/** Entrada del login. */
public record LoginRequestDTO(
        @NotBlank(message = "El RPE es obligatorio") String rpe,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
