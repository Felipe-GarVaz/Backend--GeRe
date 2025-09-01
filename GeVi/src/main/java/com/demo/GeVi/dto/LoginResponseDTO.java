package com.demo.GeVi.dto;

import java.util.List;

/** Salida del login. */
public record LoginResponseDTO(
        String token,
        String name,
        List<String> roles) {
}
