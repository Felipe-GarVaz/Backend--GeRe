package com.demo.GeVi.service.Implements;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

import com.demo.GeVi.dto.VehicleHistoryDTO;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.VehicleReport;
import com.demo.GeVi.repository.VehicleHistoryRepository;
import com.demo.GeVi.service.VehicleHistoryService;

/**
 * Implementación del servicio de historial de reportes de vehículos.
 */
@Service
public class VehicleHistoryServiceImp implements VehicleHistoryService {

    private final VehicleHistoryRepository vehicleHistoryRepository;

    public VehicleHistoryServiceImp(VehicleHistoryRepository vehicleHistoryRepository) {
        this.vehicleHistoryRepository = vehicleHistoryRepository;
    }

    /**
     * Convierte segundos a formato legible con días y hh:mm:ss.
     */
    private String formatSeconds(long seconds) {
        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return (d > 0 ? d + "d " : "") + String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * Devuelve historial de reportes para un vehículo buscado.
     */
    @Override
    public List<VehicleHistoryDTO> getVehicleHistory(String searchTerm) {
        List<VehicleReport> reports = vehicleHistoryRepository.searchByEconomicalOrBadge(searchTerm);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        reports.sort(Comparator
                .comparing((VehicleReport r) -> r.getVehicle().getId())
                .thenComparing(VehicleReport::getReportingDate));

        Map<Integer, Status> lastStatusByVehicle = new HashMap<>();
        List<VehicleHistoryDTO> historyList = new ArrayList<>();

        for (VehicleReport report : reports) {
            Integer vehicleId = report.getVehicle().getId();
            Status previousStatus = lastStatusByVehicle.getOrDefault(vehicleId, Status.DISPONIBLE);

            VehicleHistoryDTO dto = new VehicleHistoryDTO();
            dto.setId(report.getId());
            dto.setEconomical(report.getVehicle().getEconomical());
            dto.setBadge(report.getVehicle().getBadge());
            dto.setDate(report.getReportingDate().format(dateFormatter));
            dto.setHour(report.getReportingDate().format(timeFormatter));
            dto.setPreviousState(previousStatus.toString());
            dto.setNewState(report.getNewStatus().toString());
            dto.setFailType(
                    report.getFailType() != null ? report.getFailType().getName() : report.getPersonalizedFailure());
            dto.setLocalitation(
                    report.getLocationUnavailable() != null ? report.getLocationUnavailable().name() : null);
            dto.setReportedBy(report.getUser().getName() + " " + report.getUser().getLastName());
            dto.setRpe(report.getUser().getRpe());
            dto.setMileage(report.getMileage());
            dto.setTimeElapsed(report.getTimeElapsed());

            if (report.getTimeElapsed() != null) {
                dto.setFormattedElapsedTime(formatSeconds(report.getTimeElapsed()));
            }

            lastStatusByVehicle.put(vehicleId, report.getNewStatus());
            historyList.add(dto);
        }

        return historyList;
    }

    /**
     * Devuelve sugerencias de búsqueda (económico - placa).
     */
    @Override
    public List<String> getSuggestions() {
        List<VehicleReport> reports = vehicleHistoryRepository.findAll();

        return reports.stream()
                .map(r -> r.getVehicle().getEconomical() + " - " + r.getVehicle().getBadge())
                .distinct()
                .toList();
    }
}
