package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.service.DeviceService;

@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevice() {
        return ResponseEntity.ok(deviceService.getDeviceByWorkCenter());
    }

    @GetMapping("/serialNumber")
    public List<Device> getByTypeAndWorkCenter(@RequestParam DeviceType deviceType, @RequestParam String workCenter) {
        return deviceRepository.findByDeviceTypeAndWorkCenterName(deviceType, workCenter);
    }
}
