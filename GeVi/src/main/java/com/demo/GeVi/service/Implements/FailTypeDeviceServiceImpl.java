// src/main/java/com/demo/GeVi/service/impl/FailTypeDeviceServiceImpl.java
package com.demo.GeVi.service.Implements;

import com.demo.GeVi.dto.FailTypeDeviceResponseDTO;
import com.demo.GeVi.model.FailTypeDevice;
import com.demo.GeVi.repository.FailTypeDeviceRepository;
import com.demo.GeVi.service.FailTypeDeviceService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FailTypeDeviceServiceImpl implements FailTypeDeviceService {

    private final FailTypeDeviceRepository repository;

    public FailTypeDeviceServiceImpl(FailTypeDeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FailTypeDeviceResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Mapper simple sin MapStruct (evita dependencia extra)
    private FailTypeDeviceResponseDTO toDTO(FailTypeDevice f) {
        return new FailTypeDeviceResponseDTO(
                f.getId(),
                f.getName());
    }
}
