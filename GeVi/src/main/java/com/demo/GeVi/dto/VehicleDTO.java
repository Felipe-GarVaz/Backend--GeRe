package com.demo.GeVi.dto;

import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.demo.GeVi.model.Vehicle;

/*
 * DTO que representa la información básica de un vehículo.
 * Usado para visualización, filtrado y exportación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private Integer id;
    private String economical;
    private String badge;
    private String property;
    private Integer mileage;
    private String brand;
    private String model;
    private Year year;
    private Integer workCenterId;
    private Integer processId;
    private String status;

    private String workCenterName;
    private String processName;

    public static VehicleDTO fromEntity(Vehicle v) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(v.getId());
        dto.setEconomical(v.getEconomical());
        dto.setBadge(v.getBadge());
        dto.setProperty(v.getProperty().name());
        dto.setMileage(v.getMileage());
        dto.setBrand(v.getBrand());
        dto.setModel(v.getModel());
        dto.setYear(v.getYear());
        dto.setStatus(v.getStatus().name());

        if (v.getWorkCenter() != null) {
            dto.setWorkCenterId(v.getWorkCenter().getId());
            dto.setWorkCenterName(v.getWorkCenter().getName());
        }

        if (v.getProcess() != null) {
            dto.setProcessId(v.getProcess().getId());
            dto.setProcessName(v.getProcess().getName());
        }

        return dto;
    }
}
