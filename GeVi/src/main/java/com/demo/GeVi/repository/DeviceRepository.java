package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.GeVi.model.Device;
import com.demo.GeVi.model.Device.DeviceStatus;
import com.demo.GeVi.model.DeviceType;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Optional<Device> findBySerialNumber(String serialNumber);

    List<Device> findByWorkCenterId(Integer workCenterId);

    List<Device> findByDeviceTypeAndWorkCenterName(DeviceType deviceType, String workCenter);

    @EntityGraph(attributePaths = "workCenter")
    List<Device> findByStatus(DeviceStatus status);
}
