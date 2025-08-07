package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleHistoryDTO;
import com.demo.GeVi.service.VehicleHistoryService;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/history")
@CrossOrigin(origins = "*")
public class VehicleHistoryController {

    private VehicleHistoryService vehicleHistoryService;

    public VehicleHistoryController(VehicleHistoryService vehicleHistoryService) {
        this.vehicleHistoryService = vehicleHistoryService;
    }

    /*
     * Obtiene el historial de reportes para un vehículo filtrando por económico o
     * placa.
     */
    @GetMapping
    public List<VehicleHistoryDTO> getReports(@RequestParam String search) {
        return vehicleHistoryService.getVehicleHistory(search);
    }

    /*
     * Obtiene sugerencias de números económicos o placas existentes.
     */
    @GetMapping("/suggestions")
    public List<String> getVehicleSuggestions() {
        return vehicleHistoryService.getSuggestions();
    }
}
