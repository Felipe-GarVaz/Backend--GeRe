package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.model.VehicleReport;

public interface VehicleReportService {
    List<VehicleLocalitationDTO> getVehiclesInWorkshop();
    List<VehicleLocalitationDTO> getVehiclesInYard();
    List<VehicleReport> getAllReports();
}
