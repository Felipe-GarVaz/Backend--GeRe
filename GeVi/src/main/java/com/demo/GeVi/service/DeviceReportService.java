package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.DeviceReport;

public interface DeviceReportService {
    DeviceReport recordReport(DeviceReportRequestDTO request);
    List<DeviceReport> getAllDevice();
    List<DeviceReport> getByWorkCenter(Integer workCenterId);
    
}
