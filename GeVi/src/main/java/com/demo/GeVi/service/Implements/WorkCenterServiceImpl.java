package com.demo.GeVi.service.Implements;

import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.WorkCenterRepository;
import com.demo.GeVi.service.WorkCenterResponseDTO;
import com.demo.GeVi.service.WorkCenterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkCenterServiceImpl implements WorkCenterService {

    private final WorkCenterRepository repository;

    public WorkCenterServiceImpl(WorkCenterRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<WorkCenterResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private WorkCenterResponseDTO toResponse(WorkCenter wc) {
        return new WorkCenterResponseDTO(
                wc.getId(),
                wc.getName()

        );
    }
}