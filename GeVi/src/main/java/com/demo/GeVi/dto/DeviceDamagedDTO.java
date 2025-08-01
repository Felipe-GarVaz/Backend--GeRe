package com.demo.GeVi.dto;

import java.time.LocalDateTime;

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
public class DeviceDamagedDTO {

    private String deviceType;
    private String serialNumber;
    private String workCenter;
    private String failType;
    private LocalDateTime reportDate;
}
