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
@RequestMapping("/api/workshop")
@CrossOrigin(origins = "*")
public class VehicleWorkshopController {

    private VehicleReportServiceImp vehicleReportService;

    public VehicleWorkshopController(VehicleReportServiceImp vehicleReportService) {
        this.vehicleReportService = vehicleReportService;
    }

    /*
     * Devuelve lista de vehículos actualmente en taller.
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleLocalitationDTO>> getVehiclesInWorkshop() {
        List<VehicleLocalitationDTO> vehicles = vehicleReportService.getVehiclesInWorkshop();
        return ResponseEntity.ok(vehicles);
    }
}
