package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO que representa un historial de reporte de un vehículo,
 * incluyendo información del estado, falla, ubicación, kilometraje,
 * usuario y la duración que estuvo activo ese reporte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleHistoryDTO {

    private Integer id;
    private String economical;
    private String badge;
    private String date;
    private String hour;
    private String previousState;
    private String newState;
    private String failType;
    private String localitation;
    private String reportedBy;
    private String rpe;
    private Integer mileage;
    private Long timeElapsed;
    private String formattedElapsedTime;
}
