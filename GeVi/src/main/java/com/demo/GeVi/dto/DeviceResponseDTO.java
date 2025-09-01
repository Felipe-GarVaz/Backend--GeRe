package com.demo.GeVi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {

        private Integer id;
        private String serialNumber;
        private String deviceType;
        private String status;
        private Integer workCenterId;
        private String workCenterName;
}
