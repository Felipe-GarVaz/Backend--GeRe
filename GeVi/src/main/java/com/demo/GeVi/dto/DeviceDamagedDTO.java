package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * DTO que representa un dispositivo reportado como defectuoso,
 * incluyendo detalles como tipo, número de serie, centro de trabajo,
 * tipo de falla y fecha del reporte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDamagedDTO {

    private String deviceType;
    private String serialNumber;
    private String workCenter;
    private String failType;
    private LocalDateTime reportDate;
}
