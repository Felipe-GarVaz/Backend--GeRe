package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.WorkCenterRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/workCenter")
public class WorkCenterController {

    @Autowired
    private WorkCenterRepository workCenterRepository;

    @GetMapping()
    public ResponseEntity <List<WorkCenter>>getAll() {
        return ResponseEntity.ok(workCenterRepository.findAll());
    }
}
