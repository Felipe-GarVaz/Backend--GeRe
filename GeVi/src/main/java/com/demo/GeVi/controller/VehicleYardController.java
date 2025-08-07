package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.service.Implements.VehicleReportServiceImp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/yard")
@CrossOrigin(origins = "*")
public class VehicleYardController {

    private VehicleReportServiceImp vehicleReportService;

    public VehicleYardController(VehicleReportServiceImp vehicleReportService) {
        this.vehicleReportService = vehicleReportService;
    }

    /*
     * Devuelve lista de vehículos actualmente en patio.
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleLocalitationDTO>> getVehiclesInYard() {
        List<VehicleLocalitationDTO> vehicles = vehicleReportService.getVehiclesInYard();
        return ResponseEntity.ok(vehicles);
    }
}
