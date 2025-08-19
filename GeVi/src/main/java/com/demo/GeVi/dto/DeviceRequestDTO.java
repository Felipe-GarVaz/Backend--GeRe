package com.demo.GeVi.dto;

import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceRequestDTO {

    @NotBlank
    private String serialNumber;

    @NotNull
    private DeviceType deviceType;

    private DeviceStatus status;

    @NotNull
    private Integer workCenterId;

}
