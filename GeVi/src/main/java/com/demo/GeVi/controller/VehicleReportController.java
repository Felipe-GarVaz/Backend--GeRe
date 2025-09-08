package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.dto.VehicleReportRequestDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.model.VehicleReport;
import com.demo.GeVi.service.VehicleReportService;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/vehicle-report")
@CrossOrigin(origins = "*")
public class VehicleReportController {

    private final VehicleReportService vehicleReportService;

    public VehicleReportController(VehicleReportService vehicleReportService) {
        this.vehicleReportService = vehicleReportService;
    }

    /** Crear reporte (el servicio resuelve el usuario por RPE). */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@Valid @RequestBody VehicleReportRequestDTO dto,
            Authentication auth) {
        final String rpe = (auth != null) ? auth.getName() : null;
        vehicleReportService.recordReport(dto, rpe);
        return ResponseEntity.ok("Reporte creado exitosamente");
    }

    /** Últimos reportes que colocaron al vehículo en TALLER. */
    @GetMapping("/workshop")
    public ResponseEntity<List<VehicleLocalitationDTO>> getWorkshop() {
        return ResponseEntity.ok(vehicleReportService.getVehiclesInWorkshop());
    }

    /** Últimos reportes que colocaron al vehículo en PATIO. */
    @GetMapping("/yard")
    public ResponseEntity<List<VehicleLocalitationDTO>> getYard() {
        return ResponseEntity.ok(vehicleReportService.getVehiclesInYard());
    }

    /** Todos los reportes (si prefieres, cambia a DTO de salida). */
    @GetMapping
    public ResponseEntity<List<VehicleReport>> getAll() {
        return ResponseEntity.ok(vehicleReportService.getAllReports());
    }

    /** (Opcional) Catálogo de tipos de falla. */
    @GetMapping("/fail-types")
    public ResponseEntity<List<FailType>> getFailTypes() {
        return ResponseEntity.ok(vehicleReportService.getFailTypes());
    }
}