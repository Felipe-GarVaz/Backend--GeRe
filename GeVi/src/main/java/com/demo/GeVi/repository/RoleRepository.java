package com.demo.GeVi.repository;

import com.demo.GeVi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByName(String name);

}
