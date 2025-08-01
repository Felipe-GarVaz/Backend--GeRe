package com.demo.GeVi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.demo.GeVi.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle> {

    @Query("SELECT DISTINCT v.workCenter.name FROM Vehicle v")
    List<String> findDistinctWorkCenterNames();

    @Query("SELECT DISTINCT v.process.name FROM Vehicle v")
    List<String> findDistinctProcessNames();

    @Query("SELECT DISTINCT v.status FROM Vehicle v")
    List<String> findDistinctStatuses();

    @Query("SELECT DISTINCT v.property FROM Vehicle v")
    List<String> findDistinctProperties();

    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.economical) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.badge) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Vehicle> searchByEconomicalOrBadge(String query);

}
