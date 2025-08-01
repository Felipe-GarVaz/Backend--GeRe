package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
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
