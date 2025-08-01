package com.demo.GeVi.service;

import com.demo.GeVi.dto.VehicleHistoryDTO;
import java.util.List;

public interface VehicleHistoryService {
    List<VehicleHistoryDTO> getVehicleHistory(String searchTerm);

    List<String> getSuggestions();
}
