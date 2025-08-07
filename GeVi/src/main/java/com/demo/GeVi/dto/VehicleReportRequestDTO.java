package com.demo.GeVi.dto;

import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Ubication;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO que representa los datos requeridos para registrar un reporte de
 * vehículo.
 * Incluye el identificador del vehículo, nuevo estado, tipo de falla
 * (predefinida o personalizada),
 * kilometraje y ubicación si aplica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReportRequestDTO {

    @NotNull(message = "El ID del vehículo es obligatorio")
    private Integer vehicleId;

    @NotNull(message = "El nuevo estado es obligatorio")
    private Status newStatus;

    private Integer failTypeId;
    private String personalizedFailure;
    private Integer mileage;
    private Ubication locationUnavailable;
}
