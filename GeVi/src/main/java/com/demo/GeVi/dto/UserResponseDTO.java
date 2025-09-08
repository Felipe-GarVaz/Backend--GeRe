package com.demo.GeVi.dto;

import java.util.Set;

public record UserResponseDTO(
        Integer id,
        String rpe,
        String name,
        Set<String> roles) {
}
