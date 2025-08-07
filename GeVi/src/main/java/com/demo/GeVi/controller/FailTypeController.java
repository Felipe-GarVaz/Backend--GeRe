package com.demo.GeVi.controller;

import com.demo.GeVi.model.FailType;
import com.demo.GeVi.repository.FailTypeRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/failTypes")
@CrossOrigin(origins = "*")
public class FailTypeController {

    private FailTypeRepository failTypeRepository;

    public FailTypeController(FailTypeRepository failTypeRepository) {
        this.failTypeRepository = failTypeRepository;
    }

    /*
     * Obtiene todos los tipos de falla disponibles.
     */
    @GetMapping
    public ResponseEntity<List<FailType>> getAllFailTypes() {
        List<FailType> failTypes = failTypeRepository.findAll();
        return ResponseEntity.ok(failTypes);
    }
}
