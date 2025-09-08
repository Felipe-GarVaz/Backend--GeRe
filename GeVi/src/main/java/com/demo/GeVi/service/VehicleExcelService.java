package com.demo.GeVi.service;

import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.VehicleReport;
import java.util.List;

public interface VehicleExcelService {
    byte[] exportVehiclesExcel(List<Vehicle> vehicles, List<VehicleReport> reports);
}