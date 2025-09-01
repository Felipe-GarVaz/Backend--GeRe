package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceType;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    /*
     * Busca por número de serie.
     */
    Optional<Device> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    Optional<Device> findBySerialNumberIgnoreCase(String serialNumber);

    @Query("SELECT d FROM Device d LEFT JOIN FETCH d.workCenter wc " +
            "WHERE LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Device> searchBySerial(@Param("query") String query);

    /*
     * Lista dispositivos por centro de trabajo
     */
    List<Device> findByWorkCenterId(Integer workCenterId);

    /**
     * Lista por tipo y nombre de centro de trabajo.
     */
    List<Device> findByDeviceTypeAndWorkCenterName(DeviceType deviceType, String workCenter);

    /**
     * Lista por estado con carga de centro.
     */
    @EntityGraph(attributePaths = "workCenter")
    List<Device> findByStatus(DeviceStatus status);

    @Query("""
                select d from Device d
                join fetch d.workCenter wc
                where d.status = :status
            """)
    List<Device> findAllByStatusFetchWorkCenter(@Param("status") DeviceStatus status);
}
