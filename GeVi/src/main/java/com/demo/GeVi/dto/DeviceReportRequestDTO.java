package com.demo.GeVi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO utilizado para registrar un nuevo reporte de dispositivo (TPS o lector).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReportRequestDTO {

    @NotBlank(message = "El número de serie es obligatorio")
    private String serialNumber;

    @NotBlank(message = "El tipo de dispositivo es obligatorio")
    private String deviceType;

    @NotNull(message = "El ID del centro de trabajo es obligatorio")
    private Integer workCenterId;

    // Puede ser null si el usuario escribe una falla personalizada
    private Integer failTypeDeviceId;

    // Se usa si se elige 'Otro' como tipo de falla
    private String personalizedFailure;

    // Se usa cuando el dispositivo cambia de estado (por ejemplo: de DEFECTUOSO a
    // ACTIVO)
    private String newStatus;
}
