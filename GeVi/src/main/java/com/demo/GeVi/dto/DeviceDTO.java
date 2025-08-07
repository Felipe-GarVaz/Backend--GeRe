package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO que representa el resumen de dispositivos por centro de trabajo,
 * separando por tipo y estado (activos/defectuosos).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {

    private String workCenter;
    private int tpNewland;
    private int tpNewlandDamaged;
    private int readerNewland;
    private int readerNewlandDamaged;
    private int tpDolphin9900;
    private int tpDolphin9900Damaged;
    private int readerDolphin9900;
    private int readerDolphin9900Damaged;
}
