package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.VehicleHistoryDTO;
import com.demo.GeVi.service.VehicleHistoryService;

@RestController
@RequestMapping("/api/reports/history")
@CrossOrigin(origins = "*")
public class VehicleHistoryController {

    private VehicleHistoryService vehicleHistoryService;

    public VehicleHistoryController(VehicleHistoryService vehicleHistoryService) {
        this.vehicleHistoryService = vehicleHistoryService;
    }

    @GetMapping
    public List<VehicleHistoryDTO> getReports(@RequestParam String search) {
        return vehicleHistoryService.getVehicleHistory(search);
    }

    @GetMapping("/suggestions")
    public List<String> getVehicleSuggestions() {
        return vehicleHistoryService.getSuggestions();
    }
}
