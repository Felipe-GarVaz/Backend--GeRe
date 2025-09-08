package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.dto.VehicleReportRequestDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.model.VehicleReport;

public interface VehicleReportService {

    void recordReport(VehicleReportRequestDTO request, String rpe); // <— nueva

    List<VehicleLocalitationDTO> getVehiclesInWorkshop();

    List<VehicleLocalitationDTO> getVehiclesInYard();

    List<VehicleReport> getAllReports();

    List<FailType> getFailTypes();
}
