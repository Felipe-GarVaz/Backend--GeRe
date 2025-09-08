package com.demo.GeVi.service;

import java.util.List;

import com.demo.GeVi.dto.ProcessResponseDTO;

public interface ProcessService {
    List<ProcessResponseDTO> findAll();
}
