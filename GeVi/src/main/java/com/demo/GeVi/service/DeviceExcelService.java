package com.demo.GeVi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.DeviceReport;

@Service
public interface DeviceExcelService {
       byte[] exportDevicesExcel(List<Device> devices, List<DeviceReport> reports);
}
