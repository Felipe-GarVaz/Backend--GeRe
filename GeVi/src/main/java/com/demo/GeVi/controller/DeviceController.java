package com.demo.GeVi.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.service.DeviceExcelService;
import com.demo.GeVi.service.DeviceService;

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

    /*
     * Obtiene lista de dispositivos agrupados por centro de trabajo.
     */
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevicesByWorkCenter() {
        List<DeviceDTO> devices = deviceService.getDeviceByWorkCenter();
        return ResponseEntity.ok(devices);
    }

    /*
     * Obtiene dispositivos por tipo (TP, Lector) y centro de trabajo.
     */
    @GetMapping("/serialNumber")
    public ResponseEntity<List<Device>> getByTypeAndWorkCenter(
            @RequestParam DeviceType deviceType,
            @RequestParam String workCenter) {

        List<Device> devices = deviceRepository.findByDeviceTypeAndWorkCenterName(deviceType, workCenter);
        return ResponseEntity.ok(devices);
    }

    /*
     * Genera y descarga un archivo Excel con información de dispositivos
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadDeviceExcel() {
        try {
            List<Device> devices = deviceRepository.findAll();
            List<DeviceReport> reports = deviceReportRepository.findAllByOrderByReportingDateDesc();

            ByteArrayInputStream excel = DeviceExcelService.exportToExcel(devices, reports);

            String fileName = "dispositivos_" + LocalDate.now() + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excel.readAllBytes());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
