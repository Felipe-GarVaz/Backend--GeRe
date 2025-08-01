package com.demo.GeVi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceReport;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.repository.DeviceReportRepository;
import com.demo.GeVi.repository.DeviceRepository;
import com.demo.GeVi.repository.WorkCenterRepository;

@Service
public class DeviceServiceImp implements DeviceService {

    @Autowired
    private WorkCenterRepository workCenterRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceReportRepository deviceReportRepository;

    @Override
    public List<DeviceDTO> getDeviceByWorkCenter() {
        List<WorkCenter> workCenters = workCenterRepository.findAll();
        List<Device> device = deviceRepository.findAll();
        List<DeviceDTO> dtoList = new ArrayList<>();

        for (WorkCenter workCenter : workCenters) {
            DeviceDTO dto = new DeviceDTO();
            dto.setWorkCenter(workCenter.getName());

            for (Device d : device) {
                if (d.getWorkCenter().getId().equals(workCenter.getId())) {
                    String type = d.getDeviceType().name();
                    boolean defective = d.getStatus().equals(Device.DeviceStatus.DEFECTUOSO);

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
                    }
                }
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<DeviceDamagedDTO> getDeviceDamaged() {
        List<Device> damaged = deviceRepository.findByStatus(DeviceStatus.DEFECTUOSO);

        return damaged.stream().map(device -> {
            List<DeviceReport> report = deviceReportRepository.findLastReportsByTypeAndWorkCenter(
                    device.getDeviceType(),
                    device.getWorkCenter().getId());

            DeviceReport lastReport = report.isEmpty() ? null : report.get(0);
            String fail = (lastReport != null)
                    ? (lastReport.getFailTypeDevice() != null ? lastReport.getFailTypeDevice().getName()
                            : lastReport.getPersonalizedFailure())
                    : "Sin información";

            LocalDateTime date = (lastReport != null) ? lastReport.getReportingDate() : null;

            return new DeviceDamagedDTO(
                    device.getDeviceType().name(),
                    device.getSerialNumber(),
                    device.getWorkCenter().getName(),
                    fail,
                    date);
        }).toList();
    }
}
