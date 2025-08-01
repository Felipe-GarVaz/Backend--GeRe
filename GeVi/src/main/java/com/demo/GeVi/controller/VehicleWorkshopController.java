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
@RequestMapping("/api/workshop")
@CrossOrigin(origins = "*")
public class VehicleWorkshopController {

    @Autowired
    private VehicleReportServiceImp vehicleReportService;

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleLocalitationDTO>> getVehiclesInWorkshop() {
        return ResponseEntity.ok(vehicleReportService.getVehiclesInWorkshop());
    }
}
