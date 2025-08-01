package com.demo.GeVi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoginRequestDTO {
    
    @NotBlank(message = "El RPE es requerido")
    private String rpe;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

}
