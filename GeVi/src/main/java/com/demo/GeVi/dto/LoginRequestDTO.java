package com.demo.GeVi.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO utilizado para la solicitud de login.
 * Contiene el RPE y la contraseña del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "El RPE es requerido")
    private String rpe;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
