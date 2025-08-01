package com.demo.GeVi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.model.FailTypeDevice;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.repository.FailTypeDeviceRepository;
import com.demo.GeVi.repository.WorkCenterRepository;

@Service
public class DeviceReportServiceImp implements DeviceReportService {

    @Autowired
    private DeviceReportRepository deviceReportRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WorkCenterRepository WorkCenterRepository;

    @Autowired
    private FailTypeDeviceRepository failTypeDeviceRepository;

    @Override
    public DeviceReport recordReport(DeviceReportRequestDTO request) {
        Device device = deviceRepository.findBySerialNumber(request.getSerialNumber())
                .orElseThrow(() -> new RuntimeException("Dispositivo no encontrado"));

        WorkCenter workCenter = WorkCenterRepository.findById(request.getWorkCenterId())
                .orElseThrow(() -> new RuntimeException("Centro de trabajo no encontrado"));

        FailTypeDevice failTypeDevice = null;
        if (request.getFailTypeDeviceId() != null && !"otros".equalsIgnoreCase(request.getPersonalizedFailure())) {
            failTypeDevice = failTypeDeviceRepository.findById(request.getFailTypeDeviceId())
                    .orElseThrow(() -> new RuntimeException("Tipo de falla no encontrado"));
        }

        // Si se desea reactivar el dispositivo
        if ("ACTIVO".equalsIgnoreCase(request.getNewStatus())) {
            device.setStatus(DeviceStatus.ACTIVO);
            deviceRepository.save(device);
            return null; // No se registra reporte en este caso
        }

        // Registrar nuevo reporte y marcar como DEFECTUOSO
        DeviceReport report = new DeviceReport();
        report.setDevice(DeviceType.valueOf(request.getDeviceType()));
        report.setWorkCenter(workCenter);
        report.setFailTypeDevice(failTypeDevice);
        report.setPersonalizedFailure(request.getPersonalizedFailure());
        report.setReportingDate(LocalDateTime.now());

        device.setStatus(DeviceStatus.DEFECTUOSO);
        deviceRepository.save(device);

        return deviceReportRepository.save(report);
    }

    @Override
    public List<DeviceReport> getAllDevice() {
        return deviceReportRepository.findAll();
    }

    @Override
    public List<DeviceReport> getByWorkCenter(Integer workCenterId) {
        return deviceReportRepository.findByWorkCenterId(workCenterId);
    }
}
