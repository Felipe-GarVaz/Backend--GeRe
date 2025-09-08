package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.demo.GeVi.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle> {

    /*
     * Nombres distintos de centros de trabajo.
     */
    @Query("SELECT DISTINCT v.workCenter.name FROM Vehicle v")
    List<String> findDistinctWorkCenterNames();

    /*
     * Nombres distintos de procesos.
     */
    @Query("SELECT DISTINCT v.process.name FROM Vehicle v")
    List<String> findDistinctProcessNames();

    /*
     * Estados distintos de vehículos.
     */
    @Query("SELECT DISTINCT v.status FROM Vehicle v")
    List<String> findDistinctStatuses();

    /*
     * Tipos de propiedad distintos.
     */
    @Query("SELECT DISTINCT v.property FROM Vehicle v")
    List<String> findDistinctProperties();

    /*
     * Búsqueda por número económico o placa
     */
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.economical) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.badge) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Vehicle> searchByEconomicalOrBadge(String query);

    boolean existsByEconomical(String economical);

    boolean existsByBadge(String badge);

    void deleteByEconomical(String economical);

    // ===== NUEVO: búsquedas exactas (para seleccionar / eliminar) =====
    Optional<Vehicle> findByEconomicalIgnoreCase(String economical);

    Optional<Vehicle> findByBadgeIgnoreCase(String badge);
}
