package com.demo.GeVi.controller;

import java.util.List;

import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.service.Implements.DeviceServiceImp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceDamagedController {

    private DeviceServiceImp deviceService;

    public DeviceDamagedController(DeviceServiceImp deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Obtiene todos los dispositivos que han sido reportados como defectuosos.
     */
    @GetMapping("/damaged")
    public ResponseEntity<List<DeviceDamagedDTO>> getDeviceDamaged() {
        List<DeviceDamagedDTO> devices = deviceService.getDeviceDamaged();
        return ResponseEntity.ok(devices);
    }
}
