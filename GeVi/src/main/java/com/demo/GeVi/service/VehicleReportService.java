package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.VehicleLocalitationDTO;

public interface VehicleReportService {
    List<VehicleLocalitationDTO> getVehiclesInWorkshop();
    List<VehicleLocalitationDTO> getVehiclesInYard();
}
