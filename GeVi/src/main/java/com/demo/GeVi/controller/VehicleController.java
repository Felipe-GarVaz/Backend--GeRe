package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.dto.VehicleRequestDTO;
import com.demo.GeVi.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping(path = "/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final VehicleService vehicleService;

    // Inyección por constructor (preferible a @Autowired en campo)
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Lista de vehículos con filtros dinámicos (coincide con
     * VehicleService.findAll).
     * Filtros opcionales: workCenterId, processId, status, property, search
     */
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAll(
            @RequestParam(required = false) Integer workCenterId,
            @RequestParam(required = false) Integer processId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String property,
            @RequestParam(name = "search", required = false) String economicalOrBadge) {

        List<VehicleDTO> result = vehicleService.findAll(
                workCenterId, processId, status, property, economicalOrBadge);
        return ResponseEntity.ok(result);
    }

    /**
     * Opciones únicas para filtros del frontend.
     * (coincide con VehicleService.getFilterOptions)
     */
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getFilterOptions() {
        return ResponseEntity.ok(vehicleService.getFilterOptions());
    }

    /**
     * Búsqueda por económico o placa (contiene).
     * (coincide con VehicleService.searchVehicles)
     */
    @GetMapping("/search")
    public ResponseEntity<List<VehicleDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(vehicleService.searchVehicles(query));
    }

    /**
     * Obtener uno por económico O placa exacto.
     * (coincide con VehicleService.getOneByEconomicalOrBadge)
     */
    @GetMapping("/economical-or-badge")
    public ResponseEntity<VehicleDTO> getOneByEconomicalOrBadge(@RequestParam String query) {
        return ResponseEntity.ok(vehicleService.getOneByEconomicalOrBadge(query));
    }

    /**
     * Crear vehículo.
     * (coincide con VehicleService.saveVehicle)
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> create(@Valid @RequestBody VehicleRequestDTO request) {
        // saveVehicle retorna Entity, pero exponemos DTO: reutiliza
        // getOneByEconomicalOrBadge
        // tras crear para responder con DTO y Location consistente.
        var saved = vehicleService.saveVehicle(request);
        var dto = vehicleService.getOneByEconomicalOrBadge(saved.getEconomical());

        URI location = URI.create(String.format("/api/vehicles/economical-or-badge?query=%s", dto.getEconomical()));
        return ResponseEntity.created(location)
                .header(HttpHeaders.LOCATION, location.toString())
                .body(dto);
    }

    /**
     * Eliminar por número económico (case-insensitive).
     * (coincide con VehicleService.deleteByEconomical)
     */
    @DeleteMapping("/economical/{economical}")
    public ResponseEntity<Void> deleteByEconomical(@PathVariable String economical) {
        vehicleService.deleteByEconomical(economical);
        return ResponseEntity.noContent().build();
    }

    // Exportar a Excel: content-type correcto + filename UTF-8 + cache control
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportExcel() {
        var export = vehicleService.exportVehiclesExcel();

        String defaultFileName = "vehiculos_" + LocalDate.now() + ".xlsx";
        String fileName = (export.fileName() != null && !export.fileName().isBlank())
                ? export.fileName()
                : defaultFileName;

        String contentDisposition = "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentType(MediaType.parseMediaType(EXCEL_MIME))
                .body(export.content());
    }
}
