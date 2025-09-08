package com.demo.GeVi.service.Implements;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.exception.ResourceNotFoundException;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.FailTypeDevice;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.repository.FailTypeDeviceRepository;
import com.demo.GeVi.repository.WorkCenterRepository;
import com.demo.GeVi.service.DeviceReportService;

import jakarta.transaction.Transactional;

@Service
public class DeviceReportServiceImp implements DeviceReportService {

    @Autowired
    private DeviceReportRepository deviceReportRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WorkCenterRepository workCenterRepository;

    @Autowired
    private FailTypeDeviceRepository failTypeDeviceRepository;

    @Override
    @Transactional
    public DeviceReport recordReport(DeviceReportRequestDTO request) {
        // Sanitiza entradas
        String serial = request.getSerialNumber() == null ? null : request.getSerialNumber().trim();

        Device device = deviceRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo no encontrado (id=" + serial + ")"));

        WorkCenter workCenter = workCenterRepository.findById(request.getWorkCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Centro de trabajo no encontrado (id=" + request.getWorkCenterId() + ")"));

        // Si se desea reactivar el dispositivo: NO crear reporte
        if ("ACTIVO".equalsIgnoreCase(request.getNewStatus())) {
            device.setStatus(DeviceStatus.ACTIVO);
            // No necesitas save explícito si está manejado, pero lo dejamos claro
            deviceRepository.save(device);
            return null; // -> controller devolverá 204
        }

        // Validar causa de falla: failType o personalizada (al menos una)
        FailTypeDevice failTypeDevice = null;
        String personalized = request.getPersonalizedFailure();
        boolean hasPersonalized = personalized != null && !personalized.isBlank();

        if (request.getFailTypeDeviceId() != null) {
            failTypeDevice = failTypeDeviceRepository.findById(request.getFailTypeDeviceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de falla no encontrado (id=" + request.getFailTypeDeviceId() + ")"));
        }

        // Construir y persistir reporte
        DeviceReport report = new DeviceReport();
        report.setDevice(device); // ⚠️ llena la FK (dispositivoId)
        report.setWorkCenter(workCenter);
        report.setDeviceType(device.getDeviceType()); // usa el del Device para consistencia
        report.setFailTypeDevice(failTypeDevice);
        report.setPersonalizedFailure(hasPersonalized ? personalized.trim() : null);
        report.setReportingDate(LocalDateTime.now());

        // Marcar como DEFECTUOSO
        device.setStatus(DeviceStatus.DEFECTUOSO);
        // deviceRepository.save(device); // innecesario si el contexto hace
        // flush/commit; deja si prefieres explícito

        return deviceReportRepository.save(report); // devuelve entidad con ID
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
