package com.demo.GeVi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.GeVi.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    /*
     * Busca un usuario por su RPE.
     */
    Optional<User> findByRpe(String rpe);

    boolean existsByRpe(String rpe);

    /** Devuelve cuántos registros se borraron */
    int deleteByRpeIgnoreCase(String rpe);

    /** Sugerencias por RPE (máximo 10, sin pageable) */
    List<User> findTop10ByRpeContainingIgnoreCase(String rpe);
}
