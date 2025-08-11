package com.demo.GeVi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {

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
    @JsonAlias({ "workCenterId" })
    private Integer workCenter;

    @NotNull
    @JsonAlias({ "processId" }) 
    private Integer process;
}
