package com.demo.GeVi.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.service.DeviceExcelService;
import com.demo.GeVi.service.DeviceService;

@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceReportRepository deviceReportRepository;

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevice() {
        return ResponseEntity.ok(deviceService.getDeviceByWorkCenter());
    }

    @GetMapping("/serialNumber")
    public List<Device> getByTypeAndWorkCenter(@RequestParam DeviceType deviceType, @RequestParam String workCenter) {
        return deviceRepository.findByDeviceTypeAndWorkCenterName(deviceType, workCenter);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadDeviceExcel() {
        try {
            List<Device> devices = deviceRepository.findAll();
            List<DeviceReport> reports = deviceReportRepository.findAllByOrderByReportingDateDesc();

            ByteArrayInputStream excel = DeviceExcelService.exportToExcel(devices, reports);

            String date = java.time.LocalDate.now().toString();
            String fileName = "dispositivos_" + date + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excel.readAllBytes());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
