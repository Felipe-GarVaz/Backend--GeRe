package com.demo.GeVi.controller;

import java.util.List;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.service.DeviceReportService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deviceReport")
@CrossOrigin(origins = "*")
public class DeviceReportController {

    private DeviceReportService deviceReportService;

    public DeviceReportController(DeviceReportService deviceReportService) {
        this.deviceReportService = deviceReportService;
    }

    /*
     * Registra un nuevo reporte de dispositivo.
     */
    @PostMapping
    public ResponseEntity<DeviceReport> recordReport(@RequestBody DeviceReportRequestDTO request) {
        DeviceReport deviceReport = deviceReportService.recordReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceReport);
    }

    /*
     * Obtiene todos los reportes registrados de dispositivos.
     */
    @GetMapping
    public ResponseEntity<List<DeviceReport>> getAllDeviceReports() {
        List<DeviceReport> reports = deviceReportService.getAllDevice();
        return ResponseEntity.ok(reports);
    }

    /*
     * Obtiene los reportes de dispositivos filtrados por centro de trabajo.
     */
    @GetMapping("/workCenter/{id}")
    public ResponseEntity<List<DeviceReport>> getReportsByWorkCenter(@PathVariable Integer id) {
        List<DeviceReport> reports = deviceReportService.getByWorkCenter(id);
        return ResponseEntity.ok(reports);
    }
}
