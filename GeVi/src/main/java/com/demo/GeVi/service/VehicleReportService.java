package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.model.VehicleReport;

public interface VehicleReportService {

    /*
     * Vehículos en estado 'Indisponible' ubicados en taller.
     */
    List<VehicleLocalitationDTO> getVehiclesInWorkshop();

    /*
     * Vehículos en estado 'Indisponible' ubicados en patio.
     */
    List<VehicleLocalitationDTO> getVehiclesInYard();

    /*
     * Retorna todos los reportes registrados.
     */
    List<VehicleReport> getAllReports();
}
