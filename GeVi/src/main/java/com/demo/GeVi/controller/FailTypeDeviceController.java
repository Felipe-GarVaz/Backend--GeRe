// src/main/java/com/demo/GeVi/controller/FailTypeDeviceController.java
package com.demo.GeVi.controller;

import com.demo.GeVi.dto.FailTypeDeviceResponseDTO;
import com.demo.GeVi.service.FailTypeDeviceService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/failTypeDevice", produces = MediaType.APPLICATION_JSON_VALUE)
public class FailTypeDeviceController {

    private final FailTypeDeviceService failTypeDeviceService;

    public FailTypeDeviceController(FailTypeDeviceService failTypeDeviceService) {
        this.failTypeDeviceService = failTypeDeviceService;
    }

    /**
     * Obtiene todos los tipos de fallas predefinidas registradas para dispositivos.
     */
    @GetMapping
    public ResponseEntity<List<FailTypeDeviceResponseDTO>> getAllFailTypesForDevices() {
        return ResponseEntity.ok(failTypeDeviceService.getAll());
    }
}
