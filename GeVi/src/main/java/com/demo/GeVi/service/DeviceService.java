package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.dto.DeviceDamagedDTO;
import com.demo.GeVi.dto.DeviceRequestDTO;
import com.demo.GeVi.dto.DeviceResponseDTO;
import com.demo.GeVi.model.DeviceType;

public interface DeviceService {

    List<DeviceDTO> getDeviceByWorkCenter();

    List<DeviceDamagedDTO> getDeviceDamaged();

    DeviceResponseDTO saveDevice(DeviceRequestDTO request);

    List<DeviceResponseDTO> searchBySerial(String query);

    void deleteBySerial(String serialNumber);

    FilePayload exportDevicesExcel();

    List<DeviceResponseDTO> findByTypeAndWorkCenter(DeviceType deviceType, String workCenter);

    record FilePayload(byte[] content, String fileName, String contentType) {
    }
}
