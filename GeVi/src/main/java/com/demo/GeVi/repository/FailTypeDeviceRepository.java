package com.demo.GeVi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.demo.GeVi.model.FailTypeDevice;

@Repository
public interface FailTypeDeviceRepository extends JpaRepository<FailTypeDevice, Integer> {
}
