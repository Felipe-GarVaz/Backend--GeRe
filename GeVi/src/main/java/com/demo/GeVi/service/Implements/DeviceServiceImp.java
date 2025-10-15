package com.demo.GeVi.service.Implements;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.dto.DeviceRequestDTO;
import com.demo.GeVi.dto.DeviceResponseDTO;
import com.demo.GeVi.exception.ResourceNotFoundException;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.repository.WorkCenterRepository;
import com.demo.GeVi.service.DeviceService;
import com.demo.GeVi.service.DeviceExcelService;

@Service
public class DeviceServiceImp implements DeviceService {

    private final WorkCenterRepository workCenterRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceReportRepository deviceReportRepository;
    private final DeviceExcelService deviceExcelService;

    public DeviceServiceImp(
            WorkCenterRepository workCenterRepository,
            DeviceRepository deviceRepository,
            DeviceReportRepository deviceReportRepository,
            DeviceExcelService deviceExcelService) {
        this.workCenterRepository = workCenterRepository;
        this.deviceRepository = deviceRepository;
        this.deviceReportRepository = deviceReportRepository;
        this.deviceExcelService = deviceExcelService;
    }

    @Override
    public List<DeviceDTO> getDeviceByWorkCenter() {
        List<WorkCenter> workCenters = workCenterRepository.findAll();
        List<Device> devices = deviceRepository.findAll();
        List<DeviceDTO> dtoList = new ArrayList<>();

        for (WorkCenter workCenter : workCenters) {
            DeviceDTO dto = new DeviceDTO();
            dto.setWorkCenter(workCenter.getName());

            for (Device d : devices) {
                if (d.getWorkCenter() != null && d.getWorkCenter().getId().equals(workCenter.getId())) {
                    String type = d.getDeviceType().name();
                    boolean defective = d.getStatus() == DeviceStatus.DEFECTUOSO;

                    switch (type) {
                        case "TP_NEWLAND" -> {
                            dto.setTpNewland(dto.getTpNewland() + 1);
                            if (defective)
                                dto.setTpNewlandDamaged(dto.getTpNewlandDamaged() + 1);
                        }
                        case "LECTOR_NEWLAND" -> {
                            dto.setReaderNewland(dto.getReaderNewland() + 1);
                            if (defective)
                                dto.setReaderNewlandDamaged(dto.getReaderNewlandDamaged() + 1);
                        }
                        case "TP_DOLPHIN_9900" -> {
                            dto.setTpDolphin9900(dto.getTpDolphin9900() + 1);
                            if (defective)
                                dto.setTpDolphin9900Damaged(dto.getTpDolphin9900Damaged() + 1);
                        }
                        case "LECTOR_DOLPHIN_9900" -> {
                            dto.setReaderDolphin9900(dto.getReaderDolphin9900() + 1);
                            if (defective)
                                dto.setReaderDolphin9900Damaged(dto.getReaderDolphin9900Damaged() + 1);
                        }
                        case "BLUEBIRD" -> {
                            dto.setBluebird(dto.getBluebird() + 1);
                            if (defective)
                                dto.setBluebirdDamaged(dto.getBluebirdDamaged() + 1);
                        }
                        case "CELULAR_OTROS" -> {
                            dto.setPhoneOthers(dto.getPhoneOthers() + 1);
                            if (defective)
                                dto.setPhoneOthersDamaged(dto.getPhoneOthersDamaged() + 1);
                        }
                        default -> {
                            /* no-op */ }
                    }
                }
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<DeviceDamagedDTO> getDeviceDamaged() {
        List<Device> damaged = deviceRepository.findAllByStatusFetchWorkCenter(DeviceStatus.DEFECTUOSO);

        return damaged.stream().map(device -> {
            // buscar último reporte por dispositivo
            Optional<DeviceReport> lastOpt = deviceReportRepository
                    .findTopByDeviceIdOrderByReportingDateDesc(device.getId());

            String fail = lastOpt
                    .map(dr -> dr.getFailTypeDevice() != null
                            ? dr.getFailTypeDevice().getName()
                            : dr.getPersonalizedFailure())
                    .filter(s -> s != null && !s.isBlank())
                    .orElse("Sin información");

            LocalDateTime date = lastOpt.map(DeviceReport::getReportingDate).orElse(null);

            return new DeviceDamagedDTO(
                    device.getDeviceType().name(),
                    device.getSerialNumber(),
                    device.getWorkCenter().getName(),
                    fail,
                    date);
        }).toList();
    }

    /* ===================== Crear ===================== */
    @Override
    @Transactional
    public DeviceResponseDTO saveDevice(DeviceRequestDTO request) {
        String serial = request.getSerialNumber().trim().toUpperCase();

        if (!serial.matches("^[A-Z0-9-]{3,50}$")) {
            throw new IllegalArgumentException("El número de serie debe usar MAYÚSCULAS, números o guiones (3-50).");
        }

        if (deviceRepository.existsBySerialNumber(serial)) {
            throw new DataIntegrityViolationException("Duplicado serial");
        }

        WorkCenter wc = workCenterRepository.findById(request.getWorkCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkCenter", "id",
                        String.valueOf(request.getWorkCenterId())));

        DeviceStatus status = (request.getStatus() != null) ? request.getStatus() : DeviceStatus.ACTIVO;

        Device entity = new Device();
        entity.setSerialNumber(serial);
        entity.setDeviceType(request.getDeviceType());
        entity.setStatus(status);
        entity.setWorkCenter(wc);

        Device saved = deviceRepository.save(entity);
        return toResponseDTO(saved);
    }

    /* ===================== Buscar ===================== */
    @Override
    public List<DeviceResponseDTO> searchBySerial(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        // si tienes versión paginada en repo, cámbialo aquí y arma PageResponseDTO
        List<Device> result = deviceRepository.searchBySerial(query);
        return result.stream().map(this::toResponseDTO).toList();
    }

    /* ===================== Borrar ===================== */
    @Override
    @Transactional
    public void deleteBySerial(String serialNumber) {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("El número de serie es requerido");
        }
        String serial = serialNumber.trim();

        Device device = deviceRepository.findBySerialNumberIgnoreCase(serial)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "serialNumber", serial));

        deviceRepository.delete(device);
    }

    /* ===================== Exportar Excel ===================== */
    @Override
    @Transactional(readOnly = true)
    public FilePayload exportDevicesExcel() {
        var devices = deviceRepository.findAll();
        var reports = deviceReportRepository.findAllByOrderByReportingDateDesc();

        byte[] bytes = deviceExcelService.exportDevicesExcel(devices, reports);
        return new FilePayload(
                bytes,
                null,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    /* ===================== Mapeos ===================== */
    private DeviceResponseDTO toResponseDTO(Device d) {
        return new DeviceResponseDTO(
                d.getId(),
                d.getSerialNumber(),
                d.getDeviceType() != null ? d.getDeviceType().name() : null,
                d.getStatus() != null ? d.getStatus().name() : null,
                d.getWorkCenter() != null ? d.getWorkCenter().getId() : null,
                d.getWorkCenter() != null ? d.getWorkCenter().getName() : null);
    }

    @Override
    public List<DeviceResponseDTO> findByTypeAndWorkCenter(DeviceType deviceType, String workCenter) {
        return deviceRepository
                .findByDeviceTypeAndWorkCenterName(deviceType, workCenter.trim())
                .stream()
                .map(d -> new DeviceResponseDTO(
                        d.getId(),
                        d.getSerialNumber(),
                        d.getDeviceType().name(),
                        d.getStatus().name(),
                        d.getWorkCenter().getId(),
                        d.getWorkCenter().getName()))
                .toList();
    }
}
