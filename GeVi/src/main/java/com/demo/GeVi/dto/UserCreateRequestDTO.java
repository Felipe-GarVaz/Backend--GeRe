package com.demo.GeVi.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public record UserCreateRequestDTO(
                @NotBlank String rpe,
                @NotBlank String name,
                @NotBlank String lastName,
                @NotBlank String password,
                @NotEmpty Set<Integer> roleIds // IDs de roles a asignar
) {
}
