package com.demo.GeVi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Carga el usuario por RPE desde la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String rpe) throws UsernameNotFoundException {
        User user = userRepository.findByRpe(rpe)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con RPE: " + rpe));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getRpe())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
