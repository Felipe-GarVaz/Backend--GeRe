package com.demo.GeVi.service;

import java.util.List;
import java.util.Map;
import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.model.Vehicle;

public interface VehicleService {

    List<VehicleDTO> findAll(Integer workCenterId,
            Integer processId,
            String status,
            String property,
            String search);

    VehicleDTO save(VehicleDTO dto);

    VehicleDTO update(Integer id, VehicleDTO dto);

    void delete(Integer id);

    Map<String, List<Map<String, Object>>> getFilterOptions();

    public List<VehicleDTO> searchVehicles(String query);

    List <Vehicle> getAllVehicles();

}
