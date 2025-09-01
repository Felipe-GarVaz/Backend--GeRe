package com.demo.GeVi.controller;

import com.demo.GeVi.service.WorkCenterResponseDTO;
import com.demo.GeVi.service.WorkCenterService;

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

    private final WorkCenterService workCenterService;

    public WorkCenterController(WorkCenterService workCenterService) {
        this.workCenterService = workCenterService;
    }

    /** Lista todos los centros de trabajo */
    @GetMapping
    public ResponseEntity<List<WorkCenterResponseDTO>> getAll() {
        return ResponseEntity.ok(workCenterService.findAll());
    }
}
