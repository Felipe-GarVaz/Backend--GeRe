package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.dto.ProcessResponseDTO;
import com.demo.GeVi.service.ProcessService;

@RestController
@RequestMapping("/api/process")
@CrossOrigin(origins = "*")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * Obtiene la lista completa de procesos.
     */
    @GetMapping
    public ResponseEntity<List<ProcessResponseDTO>> getAll() {
        return ResponseEntity.ok(processService.findAll());
    }

}
