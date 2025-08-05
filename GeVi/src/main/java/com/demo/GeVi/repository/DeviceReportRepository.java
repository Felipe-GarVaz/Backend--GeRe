package com.demo.GeVi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.GeVi.model.DeviceType;
import com.demo.GeVi.model.DeviceReport;

@Repository
public interface DeviceReportRepository extends JpaRepository<DeviceReport, Integer> {
    List<DeviceReport> findByWorkCenterId(Integer workCenterId);

    @Query("""
                SELECT r FROM DeviceReport r
                WHERE r.device = :type
                AND r.workCenter.id = :workCenterId
                ORDER BY r.reportingDate DESC
            """)
    List<DeviceReport> findLastReportsByTypeAndWorkCenter(
            @Param("type") DeviceType type,
            @Param("workCenterId") Integer workCenterId);

    List<DeviceReport> findAllByOrderByReportingDateDesc(); 
}
