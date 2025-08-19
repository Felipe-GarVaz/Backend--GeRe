package com.demo.GeVi.service;

import java.util.List;
import com.demo.GeVi.dto.DeviceDTO;
import com.demo.GeVi.dto.DeviceRequestDTO;
import com.demo.GeVi.model.Device;

public interface DeviceService {

    /*
     * Obtiene dispositivos agrupados por centro de trabajo.
     */
    List<DeviceDTO> getDeviceByWorkCenter();

    Device saveDevice(DeviceRequestDTO request);
}
