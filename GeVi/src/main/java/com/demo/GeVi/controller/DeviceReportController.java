package com.demo.GeVi.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.service.DeviceReportService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@Validated
@RestController
@RequestMapping(path = "/api/deviceReport", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceReportController {

    private final DeviceReportService deviceReportService;

    public DeviceReportController(DeviceReportService deviceReportService) {
        this.deviceReportService = deviceReportService;
    }

   /**
 * Registra un nuevo reporte de dispositivo.
 * 
 * - 201 Created: se creó el reporte y se retorna en el cuerpo.
 * - 204 No Content: sólo se cambió estado a ACTIVO (no hay reporte).
 */
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<DeviceReport> create(@Valid @RequestBody DeviceReportRequestDTO request) {
    DeviceReport saved = deviceReportService.recordReport(request);

    if (saved == null) {
        // Caso "reactivación" (no se crea reporte)
        return ResponseEntity.noContent().build(); // 204
        // Si prefieres 202 + Location al device:
        // URI devUri = fromCurrentContextPath()
        //        .path("/api/device/serialNumber/{serialNumber}")
        //        .buildAndExpand(request.getSerialNumber())
        //        .toUri();
        // return ResponseEntity.accepted().location(devUri).build();
    }

    URI location = fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
    return ResponseEntity.created(location).body(saved);
}

    /**
     * Lista todos los reportes de dispositivos.
     */
    @GetMapping
    public ResponseEntity<List<DeviceReport>> getAll() {
        return ResponseEntity.ok(deviceReportService.getAllDevice());
    }

    /**
     * Lista reportes filtrados por centro de trabajo.
     */
    @GetMapping("/workCenter/{id}")
    public ResponseEntity<List<DeviceReport>> getByWorkCenter(
            @PathVariable @Positive Integer id) {
        return ResponseEntity.ok(deviceReportService.getByWorkCenter(id));
    }
}
