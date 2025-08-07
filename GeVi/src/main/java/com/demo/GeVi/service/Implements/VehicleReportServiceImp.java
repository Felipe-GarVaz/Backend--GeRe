package com.demo.GeVi.service.Implements;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.GeVi.dto.VehicleReportRequestDTO;
import com.demo.GeVi.dto.VehicleLocalitationDTO;
import com.demo.GeVi.model.FailType;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Ubication;
import com.demo.GeVi.model.User;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.VehicleReport;
import com.demo.GeVi.repository.FailTypeRepository;
import com.demo.GeVi.repository.VehicleReportRepository;
import com.demo.GeVi.repository.VehicleRepository;
import com.demo.GeVi.service.VehicleReportService;

/**
 * Implementación del servicio de reportes de vehículos.
 */
@Service
public class VehicleReportServiceImp implements VehicleReportService {

    @Autowired
    private VehicleReportRepository vehicleReportRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private FailTypeRepository failTypeRepository;

    /**
     * Registra un nuevo reporte con validaciones y actualiza el vehículo.
     */
    public void recordReport(VehicleReportRequestDTO request, User user) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        Status newStatus = request.getNewStatus();
        Integer failTypeId = request.getFailTypeId();
        String personalizedFailure = request.getPersonalizedFailure();
        Ubication locationUnavailable = request.getLocationUnavailable();

        // Validaciones de negocio
        if (newStatus == Status.DISPONIBLE) {
            if (failTypeId != null || (personalizedFailure != null && !personalizedFailure.isBlank())) {
                throw new IllegalArgumentException("No debe registrar una falla cuando el estado es DISPONIBLE.");
            }
            if (locationUnavailable != null) {
                throw new IllegalArgumentException("No debe registrar ubicación cuando el estado es DISPONIBLE.");
            }
        }

        if (newStatus == Status.OPERANDO_CON_FALLA || newStatus == Status.INDISPONIBLE) {
            if (failTypeId == null && (personalizedFailure == null || personalizedFailure.isBlank())) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un tipo de falla o escribir una falla personalizada.");
            }
        }

        if (newStatus == Status.INDISPONIBLE && locationUnavailable == null) {
            throw new IllegalArgumentException("Debe registrar una ubicación cuando el estado es INDISPONIBLE.");
        } else if (newStatus != Status.INDISPONIBLE && locationUnavailable != null) {
            throw new IllegalArgumentException("Solo puede registrar ubicación si el estado es INDISPONIBLE.");
        }

        if (request.getMileage() < vehicle.getMileage()) {
            throw new IllegalArgumentException("El nuevo kilometraje no puede ser menor al actual.");
        }

        // Obtener tipo de falla (si aplica)
        FailType failType = null;
        if (failTypeId != null) {
            failType = failTypeRepository.findById(failTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de falla no válido"));
        }

        // Cierra cronómetro anterior
        Optional<VehicleReport> lastReportOpt = vehicleReportRepository
                .findTopByVehicleIdOrderByReportingDateDesc(request.getVehicleId());

        if (lastReportOpt.isPresent()) {
            VehicleReport lastReport = lastReportOpt.get();
            long elapsed = Duration.between(lastReport.getReportingDate(), LocalDateTime.now()).getSeconds();
            lastReport.setTimeElapsed(elapsed);
            vehicleReportRepository.save(lastReport);
        }

        // Crear nuevo reporte
        VehicleReport report = new VehicleReport();
        report.setVehicle(vehicle);
        report.setUser(user);
        report.setNewStatus(newStatus);
        report.setFailType(failType);
        report.setPersonalizedFailure(personalizedFailure);
        report.setMileage(request.getMileage());
        report.setLocationUnavailable(locationUnavailable);
        report.setReportingDate(LocalDateTime.now());
        report.setTimeElapsed(null);

        vehicleReportRepository.save(report);

        // Actualizar estado del vehículo
        vehicle.setStatus(newStatus);
        vehicle.setMileage(request.getMileage());
        vehicleRepository.save(vehicle);
    }

    /**
     * Obtiene últimos reportes en TALLER.
     */
    @Override
    public List<VehicleLocalitationDTO> getVehiclesInWorkshop() {
        List<VehicleReport> latestReports = vehicleReportRepository.findLatestReportsInWorkshop();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return latestReports.stream().map(report -> {
            VehicleLocalitationDTO dto = new VehicleLocalitationDTO();
            dto.setId(report.getId());
            dto.setEconomical(report.getVehicle().getEconomical());
            dto.setBadge(report.getVehicle().getBadge());
            dto.setFail(
                    report.getFailType() != null ? report.getFailType().getName() : report.getPersonalizedFailure());
            dto.setReportDate(report.getReportingDate().format(formatter));
            return dto;
        }).toList();
    }

    /**
     * Obtiene últimos reportes en PATIO.
     */
    @Override
    public List<VehicleLocalitationDTO> getVehiclesInYard() {
        List<VehicleReport> latestReports = vehicleReportRepository.findLatestReportsInYard();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return latestReports.stream().map(report -> {
            VehicleLocalitationDTO dto = new VehicleLocalitationDTO();
            dto.setId(report.getId());
            dto.setEconomical(report.getVehicle().getEconomical());
            dto.setBadge(report.getVehicle().getBadge());
            dto.setFail(
                    report.getFailType() != null ? report.getFailType().getName() : report.getPersonalizedFailure());
            dto.setReportDate(report.getReportingDate().format(formatter));
            return dto;
        }).toList();
    }

    /**
     * Devuelve todos los reportes registrados.
     */
    @Override
    public List<VehicleReport> getAllReports() {
        return vehicleReportRepository.findAll();
    }
}
