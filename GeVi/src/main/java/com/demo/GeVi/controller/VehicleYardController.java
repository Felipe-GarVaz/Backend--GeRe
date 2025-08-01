package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.service.VehicleReportServiceImp;

@RestController
@RequestMapping("/api/yard")
@CrossOrigin(origins = "*")
public class VehicleYardController {
    
    @Autowired
    private VehicleReportServiceImp vehicleReportService;

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleLocalitationDTO>> getVehiclesInYard() {
        return ResponseEntity.ok(vehicleReportService.getVehiclesInYard());
    }
}
