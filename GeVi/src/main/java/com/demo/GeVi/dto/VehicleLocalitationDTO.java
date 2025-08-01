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
public class VehicleLocalitationDTO {

    private Integer id;
    private String economical;
    private String badge;
    private String fail;
    private String reportDate; 

}
