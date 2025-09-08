package com.demo.GeVi.service;
import com.demo.GeVi.dto.FailTypeResponseDTO;
import java.util.List;

public interface FailTypeService {

    List<FailTypeResponseDTO> findAll();

    FailTypeResponseDTO findById(Integer id);
}
