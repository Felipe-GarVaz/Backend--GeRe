package com.demo.GeVi.dto;

import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Ubication;
import jakarta.validation.constraints.NotNull;
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
public class VehicleReportRequestDTO {

    @NotNull
    private Integer vehicleId;

    @NotNull
    private Status newStatus;

    private Integer failTypeId;
    private String personalizedFailure;

    @NotNull
    private Integer mileage;

    private Ubication locationUnavailable;
}
