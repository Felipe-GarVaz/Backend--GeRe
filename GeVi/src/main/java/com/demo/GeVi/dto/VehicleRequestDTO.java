package com.demo.GeVi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequestDTO {

    @NotBlank
    private String economical;

    @NotBlank
    private String badge;

    @NotBlank
    private String property; // PROPIO / ARRENDADO

    @Min(0)
    private Integer mileage;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    @Min(1900)
    private Integer year;

    @NotNull
    private Integer workCenterId;

    @NotNull
    private Integer processId;
}
