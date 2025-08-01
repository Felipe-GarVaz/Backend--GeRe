package com.demo.GeVi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

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
