package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.dto.VehicleRequest;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.VehicleReport;
import com.demo.GeVi.repository.VehicleRepository;
import com.demo.GeVi.service.VehicleExcelService;
import com.demo.GeVi.service.VehicleReportService;
import com.demo.GeVi.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleReportService reportService;

    /*
     * Obtiene lista de vehículos con filtros opcionales.
     */
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> list(
            @RequestParam(value = "workCenter", required = false) Integer workCenterId,
            @RequestParam(value = "process", required = false) Integer processId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String property,
            @RequestParam(value = "search", required = false) String economical) {

        List<VehicleDTO> result = vehicleService.findAll(
                workCenterId, processId, status, property, economical);
        return ResponseEntity.ok(result);
    }

    /*
     * Obtiene las opciones únicas para cada filtro de vehículo.
     */
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getFilterOptions() {
        return ResponseEntity.ok(vehicleService.getFilterOptions());
    }

    /*
     * Actualiza un vehículo existente por su ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(
            @PathVariable Integer id,
            @RequestBody VehicleDTO dto) {
        VehicleDTO updated = vehicleService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /*
     * Búsqueda simple por texto libre (económico o placa).
     */
    @GetMapping("/search")
    public ResponseEntity<List<VehicleDTO>> searchVehicles(@RequestParam String query) {
        return ResponseEntity.ok(vehicleService.searchVehicles(query));
    }

    /*
     * Obtiene todos los vehículos sin filtros (uso interno).
     */
    @GetMapping("/all")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /*
     * Genera y descarga un archivo Excel con los datos de vehículos
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            List<VehicleReport> reports = reportService.getAllReports();

            ByteArrayInputStream excel = VehicleExcelService.exportToExcel(vehicles, reports);
            String date = java.time.LocalDate.now().toString();
            String fileName = "vehiculos_" + date + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excel.readAllBytes());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequest request) {
        try {
            Vehicle saved = vehicleService.saveVehicle(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ===== NUEVO: obtener uno por económico O placa exacto (opcional para tu
    // front) =====
    @GetMapping("/economical-or-badge")
    public ResponseEntity<VehicleDTO> getOneByEconomicalOrBadge(@RequestParam String query) {
        return ResponseEntity.ok(vehicleService.getOneByEconomicalOrBadge(query));
    }

    // ===== NUEVO: eliminar por número económico =====
    @DeleteMapping("/economical/{economical}")
    public ResponseEntity<Void> deleteByEconomical(@PathVariable String economical) {
        vehicleService.deleteByEconomical(economical);
        return ResponseEntity.noContent().build();
    }
}
