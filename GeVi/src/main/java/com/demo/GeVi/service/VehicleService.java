package com.demo.GeVi.service;

import java.util.List;
import java.util.Map;
import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.dto.VehicleRequestDTO;
import com.demo.GeVi.model.Vehicle;

public interface VehicleService {

    /*
     * Obtiene vehículos filtrados por centro, proceso, estado, propiedad o
     * búsqueda.
     */
    List<VehicleDTO> findAll(Integer workCenterId,
            Integer processId,
            String status,
            String property,
            String search);

    /*
     * Guarda un nuevo vehículo.
     */
    VehicleDTO save(VehicleDTO dto);

    /*
     * Actualiza un vehículo existente.
     */
    VehicleDTO update(Integer id, VehicleDTO dto);

    /*
     * Devuelve opciones para filtros dinámicos.
     */
    Map<String, List<Map<String, Object>>> getFilterOptions();

    /*
     * Busca vehículos por texto (económico o placa).
     */
    List<VehicleDTO> searchVehicles(String query);

    /*
     * Retorna todos los vehículos sin filtros.
     */
    List<Vehicle> getAllVehicles();

    Vehicle saveVehicle(VehicleRequestDTO request);

    VehicleDTO getOneByEconomicalOrBadge(String query);

    void deleteByEconomical(String economical);
}
