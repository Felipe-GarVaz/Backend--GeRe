package com.demo.GeVi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.demo.GeVi.dto.VehicleReportRequestDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.model.User;
import com.demo.GeVi.repository.FailTypeRepository;
import com.demo.GeVi.repository.UserRepository;
import com.demo.GeVi.service.VehicleReportServiceImp;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reportes")
public class VehicleReportController {

    @Autowired
    private VehicleReportServiceImp vehicleReportService;

    @Autowired
    private FailTypeRepository failTypeRepository;

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/fallas")
    public List<FailType> getFailTypes() {
        return failTypeRepository.findAll();
    }

}
