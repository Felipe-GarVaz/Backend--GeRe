package com.demo.GeVi.controller;

import java.util.List;

import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.service.DeviceService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = "/api/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceDamagedController {

    private final DeviceService deviceService;

    // Inyección por constructor de la INTERFAZ, no de la implementación
    public DeviceDamagedController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Devuelve todos los dispositivos reportados como dañados.
     */
    @GetMapping("/damaged")
    public ResponseEntity<List<DeviceDamagedDTO>> getDeviceDamaged() {
        List<DeviceDamagedDTO> devices = deviceService.getDeviceDamaged();
        return ResponseEntity.ok(devices);
    }
}
