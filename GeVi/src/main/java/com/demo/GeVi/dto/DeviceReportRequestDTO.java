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
public class DeviceReportRequestDTO {

    private String serialNumber;
    private String deviceType;
    private Integer workCenterId;
    private Integer failTypeDeviceId;
    private String personalizedFailure;
    private String newStatus;

}
