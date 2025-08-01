package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.demo.GeVi.model.VehicleReport;

public interface VehicleReportRepository extends JpaRepository<VehicleReport, Integer> {

    @Query("SELECT v FROM VehicleReport v WHERE v.vehicle.id = :vehicleId AND (v.failType IS NOT NULL OR v.personalizedFailure IS NOT NULL) ORDER BY v.reportingDate DESC")
    List<VehicleReport> findLastReportsWithAnyFail(@Param("vehicleId") Integer vehicleId);

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

    Optional<VehicleReport> findTopByVehicleIdOrderByReportingDateDesc(Integer vehicleId);

}
