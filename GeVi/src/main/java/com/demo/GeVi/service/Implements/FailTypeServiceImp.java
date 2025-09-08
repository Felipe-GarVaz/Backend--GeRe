package com.demo.GeVi.service.Implements;

import com.demo.GeVi.dto.FailTypeResponseDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.repository.FailTypeRepository;
import com.demo.GeVi.service.FailTypeService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class FailTypeServiceImp implements FailTypeService {

    private final FailTypeRepository repository;

    public FailTypeServiceImp(FailTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FailTypeResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FailTypeResponseDTO findById(Integer id) {
        FailType entity = repository.findById(id)
                .orElse(null);
        return toResponse(entity);
    }

    // ===== Helpers =====
    private FailTypeResponseDTO toResponse(FailType e) {
        return new FailTypeResponseDTO(e.getId(), e.getName());
    }
}
