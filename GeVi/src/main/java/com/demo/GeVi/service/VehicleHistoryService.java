package com.demo.GeVi.service;

import com.demo.GeVi.dto.VehicleHistoryDTO;
import java.util.List;

public interface VehicleHistoryService {

    /*
     * Obtiene el historial del vehículo por número económico o placa.
     */
    List<VehicleHistoryDTO> getVehicleHistory(String searchTerm);

    /*
     * Retorna sugerencias de búsqueda (número económico o placas).
     */
    List<String> getSuggestions();
}
