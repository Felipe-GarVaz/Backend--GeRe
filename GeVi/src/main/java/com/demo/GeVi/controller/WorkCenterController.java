package com.demo.GeVi.controller;

import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.WorkCenterRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workCenter")
@CrossOrigin(origins = "*")
public class WorkCenterController {

    private WorkCenterRepository workCenterRepository;

    public WorkCenterController(WorkCenterRepository workCenterRepository) {
        this.workCenterRepository = workCenterRepository;
    }

    /*
     * Obtiene la lista completa de centros de trabajo.
     */
    @GetMapping
    public ResponseEntity<List<WorkCenter>> getAll() {
        List<WorkCenter> workCenters = workCenterRepository.findAll();
        return ResponseEntity.ok(workCenters);
    }
}
