package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.GeVi.model.FailTypeDevice;
import com.demo.GeVi.repository.FailTypeDeviceRepository;

@RestController
@RequestMapping("/api/failTypeDevice")
public class FailTypeDeviceController {
    
        @Autowired
    private FailTypeDeviceRepository failTypeDeviceRepository;

    @GetMapping
    public ResponseEntity<List<FailTypeDevice>> getAll() {
        return ResponseEntity.ok(failTypeDeviceRepository.findAll());
    }
}
