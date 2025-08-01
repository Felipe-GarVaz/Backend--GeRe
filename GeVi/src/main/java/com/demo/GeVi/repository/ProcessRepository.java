package com.demo.GeVi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.GeVi.model.Process;

public interface ProcessRepository extends JpaRepository<Process, Integer> {
    
}
