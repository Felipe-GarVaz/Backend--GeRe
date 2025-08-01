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
public class VehicleHistoryDTO {

    private Integer id;
    private String economical;
    private String badge;
    private String date;
    private String hour;
    private String previousState;
    private String newState;
    private String failType;
    private String localitation;
    private String reportedBy;
    private String rpe;
    private Integer mileage;
    private Long timeElapsed;
    private String formattedElapsedTime;
}
