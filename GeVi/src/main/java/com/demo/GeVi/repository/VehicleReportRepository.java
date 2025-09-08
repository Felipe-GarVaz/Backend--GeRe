package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.demo.GeVi.model.VehicleReport;

public interface VehicleReportRepository extends JpaRepository<VehicleReport, Integer> {

    /*
     * Últimos reportes con cualquier tipo de falla para un vehículo
     */
    @Query("SELECT v FROM VehicleReport v WHERE v.vehicle.id = :vehicleId " +
            "AND (v.failType IS NOT NULL OR v.personalizedFailure IS NOT NULL) " +
            "ORDER BY v.reportingDate DESC")
    List<VehicleReport> findLastReportsWithAnyFail(@Param("vehicleId") Integer vehicleId);

    /*
     * Últimos reportes en estado INDISPONIBLE con ubicación TALLER.
     */
    @Query("""
                SELECT r FROM VehicleReport r
                WHERE r.reportingDate IN (
                    SELECT MAX(vr.reportingDate) FROM VehicleReport vr
                    GROUP BY vr.vehicle.id
                )
                AND r.newStatus = 'INDISPONIBLE'
                AND r.locationUnavailable = 'TALLER'
            """)
    List<VehicleReport> findLatestReportsInWorkshop();

    /*
     * Últimos reportes en estado INDISPONIBLE con ubicación PATIO.
     */
    @Query("""
                SELECT r FROM VehicleReport r
                WHERE r.reportingDate IN (
                    SELECT MAX(vr.reportingDate) FROM VehicleReport vr
                    GROUP BY vr.vehicle.id
                )
                AND r.newStatus = 'INDISPONIBLE'
                AND r.locationUnavailable = 'PATIO'
            """)
    List<VehicleReport> findLatestReportsInYard();

    // ✅ ÚLTIMO reporte de UN vehículo (el más reciente por fecha)
    Optional<VehicleReport> findTopByVehicleIdOrderByReportingDateDesc(Integer vehicleId);

    // ✅ TODOS los reportes, ordenados por fecha desc (si lo necesitas para Excel)
    List<VehicleReport> findAllByOrderByReportingDateDesc();
}
