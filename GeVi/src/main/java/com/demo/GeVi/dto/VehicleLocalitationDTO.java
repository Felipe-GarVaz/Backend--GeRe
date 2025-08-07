package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO que representa un vehículo en estado INDISPONIBLE,
 * incluyendo su ubicación, fecha de reporte y tiempo transcurrido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocalitationDTO {

    private Integer id;
    private String economical;
    private String badge;
    private String fail;
    private String reportDate;

}
