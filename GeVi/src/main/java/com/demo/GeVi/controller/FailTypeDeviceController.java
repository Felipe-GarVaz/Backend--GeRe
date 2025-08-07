package com.demo.GeVi.controller;

import com.demo.GeVi.model.FailTypeDevice;
import com.demo.GeVi.repository.FailTypeDeviceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/failTypeDevice")
@CrossOrigin(origins = "*")
public class FailTypeDeviceController {

    private FailTypeDeviceRepository failTypeDeviceRepository;

    public FailTypeDeviceController(FailTypeDeviceRepository failTypeDeviceRepository) {
        this.failTypeDeviceRepository = failTypeDeviceRepository;
    }

    /*
     * Obtiene todos los tipos de fallas predefinidas registradas para dispositivos
     */
    @GetMapping
    public ResponseEntity<List<FailTypeDevice>> getAllFailTypesForDevices() {
        List<FailTypeDevice> failTypes = failTypeDeviceRepository.findAll();
        return ResponseEntity.ok(failTypes);
    }
}
