package com.demo.GeVi.repository;

import com.demo.GeVi.model.FailType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailTypeRepository extends JpaRepository<FailType, Integer> {
    
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}
