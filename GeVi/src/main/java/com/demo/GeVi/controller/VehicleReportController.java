package com.demo.GeVi.controller;

import com.demo.GeVi.dto.VehicleReportRequestDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.FailTypeRepository;
import com.demo.GeVi.repository.UserRepository;
import com.demo.GeVi.service.Implements.VehicleReportServiceImp;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class VehicleReportController {

    private VehicleReportServiceImp vehicleReportService;
    private FailTypeRepository failTypeRepository;
    private UserRepository userRepository;

    public VehicleReportController(
            VehicleReportServiceImp vehicleReportService,
            FailTypeRepository failTypeRepository,
            UserRepository userRepository) {
        this.vehicleReportService = vehicleReportService;
        this.failTypeRepository = failTypeRepository;
        this.userRepository = userRepository;
    }

    /*
     * Registra un nuevo reporte de vehículo con el usuario autenticado.
     */
    @PostMapping
    public ResponseEntity<String> createReport(
            @Valid @RequestBody VehicleReportRequestDTO dto,
            Authentication authentication) {

        String rpe = authentication.getName();

        User user = userRepository.findByRpe(rpe)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        vehicleReportService.recordReport(dto, user);
        return ResponseEntity.ok("Reporte creado exitosamente");
    }

    /**
     * Obtiene todos los tipos de fallas predefinidas disponibles para los
     * vehículos.
     */
    @GetMapping("/fail")
    public ResponseEntity<List<FailType>> getFailTypes() {
        List<FailType> failTypes = failTypeRepository.findAll();
        return ResponseEntity.ok(failTypes);
    }
}
