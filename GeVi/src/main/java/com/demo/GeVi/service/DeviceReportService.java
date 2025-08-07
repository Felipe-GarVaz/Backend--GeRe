package com.demo.GeVi.service;

import java.util.List;
import com.demo.GeVi.dto.DeviceReportRequestDTO;
import com.demo.GeVi.model.DeviceReport;

public interface DeviceReportService {

    /*
     * Registra un nuevo reporte.
     */
    DeviceReport recordReport(DeviceReportRequestDTO request);

    /*
     * Obtiene todos los reportes.
     */
    List<DeviceReport> getAllDevice();

    /*
     * Lista reportes por centro de trabajo.
     */
    List<DeviceReport> getByWorkCenter(Integer workCenterId);
}
