package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.repository.VehicleRepository;
import com.demo.GeVi.service.VehicleExcelService;
import com.demo.GeVi.service.VehicleService;
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
@CrossOrigin(origins = "*") // Permitir solicitudes desde el frontend local
public class VehicleController {

    @Autowired
    private VehicleService service;

    @Autowired
    private VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> list(
            @RequestParam(value = "centroTrabajo", required = false) Integer workCenterId,
            @RequestParam(value = "proceso", required = false) Integer processId,
            @RequestParam(value = "estado", required = false) String status,
            @RequestParam(value = "propiedad", required = false) String property,
            @RequestParam(value = "busqueda", required = false) String economical) {
        List<VehicleDTO> result = service.findAll(
                workCenterId,
                processId,
                status,
                property,
                economical);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getFilterOptions() {
        System.out.println("GET /filters called");
        return ResponseEntity.ok(service.getFilterOptions());
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> create(@RequestBody VehicleDTO dto) {
        VehicleDTO created = service.save(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(
            @PathVariable Integer id,
            @RequestBody VehicleDTO dto) {
        VehicleDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleDTO>> searchVehicles(@RequestParam("query") String query) {
        return ResponseEntity.ok(service.searchVehicles(query));
    }

    @GetMapping("/all")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            List<Vehicle> vehicle = service.getAllVehicles();
            ByteArrayInputStream excel = VehicleExcelService.exportToExcel(vehicle);

            // Obtener fecha actual en formato YYYY-MM-DD
            String date = java.time.LocalDate.now().toString(); 
            String fileName= "vehiculos_" + date + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excel.readAllBytes());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
