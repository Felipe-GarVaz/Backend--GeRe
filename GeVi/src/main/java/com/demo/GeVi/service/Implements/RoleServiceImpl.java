package com.demo.GeVi.service.Implements;

import com.demo.GeVi.dto.RoleResponseDTO;
import com.demo.GeVi.repository.RoleRepository;
import com.demo.GeVi.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponseDTO> findAll() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponseDTO(r.getId(), r.getName()))
                .toList();
    }
}
