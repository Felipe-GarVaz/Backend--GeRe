package com.demo.GeVi.controller;

import com.demo.GeVi.dto.FailTypeResponseDTO;
import com.demo.GeVi.service.FailTypeService;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/failTypes")
@CrossOrigin(origins = "*")
public class FailTypeController {

    private final FailTypeService failTypeService;

    public FailTypeController(FailTypeService failTypeService) {
        this.failTypeService = failTypeService;
    }

    @GetMapping
    public ResponseEntity<List<FailTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(failTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FailTypeResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(failTypeService.findById(id));
    }
}