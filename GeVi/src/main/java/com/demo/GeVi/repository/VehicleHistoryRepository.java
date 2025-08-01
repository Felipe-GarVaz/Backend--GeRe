package com.demo.GeVi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import com.demo.GeVi.model.VehicleReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleHistoryRepository extends JpaRepository<VehicleReport, Integer> {

    @Query("SELECT r FROM VehicleReport r " +
            "JOIN FETCH r.vehicle v " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.failType f " +
            "WHERE LOWER(v.economical) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "   OR LOWER(v.badge) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY r.reportingDate DESC")
    List<VehicleReport> searchByEconomicalOrBadge(String searchTerm);

}
