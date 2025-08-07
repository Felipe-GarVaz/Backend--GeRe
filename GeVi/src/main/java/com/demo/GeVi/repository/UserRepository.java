package com.demo.GeVi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.GeVi.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    /*
     * Busca un usuario por su RPE.
     */
    Optional<User> findByRpe(String rpe);
}
