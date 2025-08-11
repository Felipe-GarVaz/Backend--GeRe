package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.repository.ProcessRepository;

@RestController
@RequestMapping("/api/process")
@CrossOrigin(origins = "*")
public class ProcessController {

    private ProcessRepository processRepository;

    public ProcessController(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    /*
     * Obtiene la lista completa de procesos.
     */
    @GetMapping
    public ResponseEntity<List<com.demo.GeVi.model.Process>> getAll() {
        List<com.demo.GeVi.model.Process> process = processRepository.findAll();
        return ResponseEntity.ok(process);
    }

}
