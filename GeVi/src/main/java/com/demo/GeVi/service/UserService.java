package com.demo.GeVi.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.GeVi.dto.UserCreateRequestDTO;
import com.demo.GeVi.dto.UserResponseDTO;
import com.demo.GeVi.exception.UserAlreadyExistsException;
import com.demo.GeVi.model.Role;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.RoleRepository;
import com.demo.GeVi.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Autenticación: carga por RPE (normalizado a MAYÚSCULAS).
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String rpe) throws UsernameNotFoundException {
        final String rpeUpper = rpe == null ? "" : rpe.trim().toUpperCase();

        User user = userRepository.findByRpe(rpeUpper)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con RPE: " + rpeUpper));

        var authorities = user.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getName())) // p.ej. ROLE_ADMIN
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getRpe())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    /*
     * Alta de usuario + asignación de roles.
     */
    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO request) throws Exception {
        final String rpeUpper = request.rpe().trim().toUpperCase();

        if (userRepository.existsByRpe(rpeUpper)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con RPE: " + rpeUpper);
        }

        // Cargar roles por ID y validar que todos existan
        var roles = roleRepository.findAllById(request.roleIds());
        if (roles.size() != request.roleIds().size()) {
            throw new IllegalArgumentException("Uno o más roles no existen.");
        }

        var user = User.builder()
                .rpe(rpeUpper)
                .name(request.name().trim())
                .lastName(request.lastName().trim())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.copyOf(roles))
                .build();

        var saved = userRepository.save(user);

        return new UserResponseDTO(
                saved.getId(),
                saved.getRpe(),
                saved.getName() + " " + saved.getLastName(),
                saved.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()));
    }

    @Transactional
    public List<UserResponseDTO> searchByRpe(String query) throws Exception {
        final int MIN_QUERY_LEN = 2;
        String q = query == null ? "" : query.trim();
        if (q.length() < MIN_QUERY_LEN) {
            throw new Exception("El parámetro 'query' debe tener al menos " + MIN_QUERY_LEN + " caracteres.");
        }

        // No forzamos mayúsculas aquí, usamos ContainingIgnoreCase en el repo
        List<User> users = userRepository.findTop10ByRpeContainingIgnoreCase(q);

        return users.stream()
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getRpe(),
                        (u.getName() == null ? "" : u.getName()) + " "
                                + (u.getLastName() == null ? "" : u.getLastName()),
                        u.getRoles().stream().map(Role::getName).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    // ===================== NUEVO: Eliminación por RPE =====================
    @Transactional
    public void deleteByRpe(String rpe) throws Exception {
        String key = rpe == null ? "" : rpe.trim().toUpperCase();
        if (key.isBlank()) {
            throw new Exception("RPE no puede ser vacío.");
        }
        try {
            int deleted = userRepository.deleteByRpeIgnoreCase(key);
            if (deleted == 0) {
                throw new Exception("Usuario no encontrado (rpe=" + key + ")");
            }
        } catch (DataIntegrityViolationException e) {
            // Bloqueado por FKs
            throw new Exception("No se puede eliminar porque existen registros relacionados.");
        }
    }
}
