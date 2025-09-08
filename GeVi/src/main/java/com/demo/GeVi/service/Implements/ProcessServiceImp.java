package com.demo.GeVi.service.Implements;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.GeVi.dto.ProcessResponseDTO;
import com.demo.GeVi.model.Process;
import com.demo.GeVi.repository.ProcessRepository;
import com.demo.GeVi.service.ProcessService;

@Service
@Transactional
public class ProcessServiceImp implements ProcessService {

    private final ProcessRepository processRepository;

    public ProcessServiceImp(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public List<ProcessResponseDTO> findAll() {
        // Orden opcional por nombre para respuesta consistente
        List<Process> entities = processRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return entities.stream()
                .map(p -> new ProcessResponseDTO(p.getId(), p.getName()))
                .collect(Collectors.toList());
    }
}