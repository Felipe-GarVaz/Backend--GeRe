package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.service.DeviceReportService;

@RestController
@RequestMapping("/api/deviceReport")
@CrossOrigin(origins = "*")
public class DeviceReportController {

    @Autowired
    private DeviceReportService deviceReportService;

    @PostMapping
    public ResponseEntity<DeviceReport> recordReport(@RequestBody DeviceReportRequestDTO request) {
        DeviceReport deviceReport = deviceReportService.recordReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceReport);
    }

    @GetMapping
    public ResponseEntity<List<DeviceReport>> getAllDevice() {
        return ResponseEntity.ok(deviceReportService.getAllDevice());
    }

    @GetMapping("/workCenter/{id}")
    public ResponseEntity<List<DeviceReport>> getByWorkCenter(@PathVariable Integer id) {
        return ResponseEntity.ok(deviceReportService.getByWorkCenter(id));
    }
}
