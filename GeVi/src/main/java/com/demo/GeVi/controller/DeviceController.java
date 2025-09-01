package com.demo.GeVi.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.dto.DeviceRequestDTO;
import com.demo.GeVi.dto.DeviceResponseDTO;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.service.DeviceService;

@Validated
@RestController
@RequestMapping(path = "/api/device", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // Crear dispositivo: devuelve DTO y Location
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponseDTO> create(@Valid @RequestBody DeviceRequestDTO request) {
        DeviceResponseDTO saved = deviceService.saveDevice(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/serialNumber/{serialNumber}")
                .buildAndExpand(saved.getSerialNumber())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // Buscar por número de serie: NO expone entidad
    @GetMapping("/search")
    public ResponseEntity<List<DeviceResponseDTO>> search(
            @RequestParam @NotBlank @Size(min = 2, max = 100) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        // limita size máximo
        if (size > 100)
            size = 100;

        List<DeviceResponseDTO> matches = deviceService.searchBySerial(query /* , page, size */);
        return ResponseEntity.ok(matches);
    }

    // Agrupado por centro de trabajo: NO expone entidad
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> byWorkCenter() {
        List<DeviceDTO> grouped = deviceService.getDeviceByWorkCenter();
        return ResponseEntity.ok(grouped);
    }

    // Eliminar por número de serie: valida formato
    @DeleteMapping("/serialNumber/{serialNumber}")
    public ResponseEntity<Void> delete(
            @PathVariable("serialNumber") String serialNumber) {
        deviceService.deleteBySerial(serialNumber);
        return ResponseEntity.noContent().build();
    }

    // Exportar a Excel: content-type correcto + filename UTF‑8 + cache control
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportExcel() {
        var export = deviceService.exportDevicesExcel();

        String defaultFileName = "dispositivos_" + LocalDate.now() + ".xlsx";
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

    /*
     * Obtiene dispositivos por tipo (TP, Lector) y centro de trabajo.
     */
    @GetMapping("/serialNumber")
    public ResponseEntity<List<DeviceResponseDTO>> getByTypeAndWorkCenter(
            @RequestParam DeviceType deviceType,
            String workCenter) {

        List<DeviceResponseDTO> devices = deviceService.findByTypeAndWorkCenter(deviceType, workCenter);
        return ResponseEntity.ok(devices);
    }
}
