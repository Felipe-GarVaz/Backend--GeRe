package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.DeviceDTO;

public interface DeviceService {
    List<DeviceDTO> getDeviceByWorkCenter();
}
