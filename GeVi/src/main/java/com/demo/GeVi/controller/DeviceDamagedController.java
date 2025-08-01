package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.service.DeviceServiceImp;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceDamagedController {

    @Autowired
    private DeviceServiceImp deviceService;

    @GetMapping("/damaged")
    public ResponseEntity<List<DeviceDamagedDTO>> getDeviceDamaged() {
        List<DeviceDamagedDTO> device = deviceService.getDeviceDamaged();
        return ResponseEntity.ok(device);
    }

}
