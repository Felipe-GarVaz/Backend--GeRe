package com.demo.GeVi.service.Implements;

import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.dto.VehicleRequestDTO;
import com.demo.GeVi.exception.ResourceNotFoundException;
import com.demo.GeVi.model.Process;
import com.demo.GeVi.model.Property;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.ProcessRepository;
import com.demo.GeVi.repository.VehicleRepository;
import com.demo.GeVi.repository.WorkCenterRepository;
import com.demo.GeVi.service.VehicleService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private WorkCenterRepository workCenterRepository;
    @Autowired
    private ProcessRepository processRepository;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Filtra vehículos dinámicamente por centro, proceso, estado, propiedad o
     * económico.
     */
    @Override
    public List<VehicleDTO> findAll(Integer workCenterId, Integer processId, String status, String property,
            String economical) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vehicle> query = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = query.from(Vehicle.class);

        List<Predicate> predicates = new ArrayList<>();

        if (workCenterId != null)
            predicates.add(cb.equal(root.get("workCenter").get("id"), workCenterId));
        if (processId != null)
            predicates.add(cb.equal(root.get("process").get("id"), processId));
        if (status != null && !status.isEmpty())
            predicates.add(cb.equal(root.get("status"), Status.valueOf(status)));
        if (property != null && !property.isEmpty())
            predicates.add(cb.equal(root.get("property"), Property.valueOf(property)));
        if (economical != null && !economical.isEmpty())
            predicates.add(cb.like(cb.lower(root.get("economical")), "%" + economical.toLowerCase() + "%"));

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Vehicle> vehicles = entityManager.createQuery(query).getResultList();

        return vehicles.stream().map(this::fromEntity).collect(Collectors.toList());
    }

    /**
     * Guarda un nuevo vehículo.
     */
    @Override
    public VehicleDTO save(VehicleDTO dto) {
        Vehicle vehicle = toEntity(dto);
        Vehicle saved = vehicleRepository.save(vehicle);
        return fromEntity(saved);
    }

    /**
     * Actualiza un vehículo existente por ID.
     */
    @Override
    public VehicleDTO update(Integer id, VehicleDTO dto) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        existing.setEconomical(dto.getEconomical());
        existing.setBadge(dto.getBadge());
        existing.setProperty(Property.valueOf(dto.getProperty().toUpperCase()));
        existing.setMileage(dto.getMileage());
        existing.setBrand(dto.getBrand());
        existing.setModel(dto.getModel());
        existing.setYear(dto.getYear());

        WorkCenter wc = workCenterRepository.findById(dto.getWorkCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkCenter", "id", dto.getWorkCenterId()));
        existing.setWorkCenter(wc);

        Process pr = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new ResourceNotFoundException("Process", "id", dto.getProcessId()));
        existing.setProcess(pr);

        existing.setStatus(Status.valueOf(dto.getStatus().toUpperCase().replace(' ', '_')));

        Vehicle updated = vehicleRepository.save(existing);
        return fromEntity(updated);
    }

    /**
     * Devuelve opciones de filtro dinámico para el frontend.
     */
    @Override
    public Map<String, List<Map<String, Object>>> getFilterOptions() {
        List<Map<String, Object>> workCenters = workCenterRepository.findAll().stream().map(wc -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", wc.getId());
            m.put("name", wc.getName());
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> processes = processRepository.findAll().stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("name", p.getName());
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> statuses = Arrays.stream(Status.values()).map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", s.name());
            m.put("name", s.name().replace("_", " "));
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> properties = Arrays.stream(Property.values()).map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.name());
            m.put("name", p.name());
            return m;
        }).collect(Collectors.toList());

        Map<String, List<Map<String, Object>>> options = new HashMap<>();
        options.put("workCenter", workCenters);
        options.put("process", processes);
        options.put("status", statuses);
        options.put("property", properties);

        return options;
    }

    /**
     * Busca vehículos por número económico o placa.
     */
    @Override
    public List<VehicleDTO> searchVehicles(String query) {
        List<Vehicle> vehicles = vehicleRepository.searchByEconomicalOrBadge(query);
        return vehicles.stream().map(this::fromEntity).collect(Collectors.toList());
    }

    /**
     * Obtiene todos los vehículos registrados.
     */
    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional
    @Override
    public Vehicle saveVehicle(VehicleRequestDTO request) {
        // Validaciones de unicidad
        if (vehicleRepository.existsByEconomical(request.getEconomical())) {
            throw new IllegalArgumentException("El número económico ya existe");
        }
        if (vehicleRepository.existsByBadge(request.getBadge())) {
            throw new IllegalArgumentException("La placa ya existe");
        }

        // FK: WorkCenter y Process
        WorkCenter wc = workCenterRepository.findById(request.getWorkCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkCenter", "id", request.getWorkCenterId()));

        Process pr = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> new ResourceNotFoundException("Process", "id", request.getProcessId()));

        // Mapear DTO -> Entity
        Vehicle v = new Vehicle();
        v.setEconomical(request.getEconomical());
        v.setBadge(request.getBadge());
        v.setProperty(Property.valueOf(request.getProperty().toUpperCase()));
        v.setMileage(request.getMileage());
        v.setBrand(request.getBrand());
        v.setModel(request.getModel());
        v.setYear(Year.of(request.getYear()));
        v.setWorkCenter(wc);
        v.setProcess(pr);

        v.setStatus(Status.DISPONIBLE);

        return vehicleRepository.save(v);
    }

    /**
     * Devuelve 1 vehículo por económico o placa (coincidencia exacta,
     * case-insensitive).
     * Útil si quieres precargar el preview de forma determinística.
     */
    @Override
    public VehicleDTO getOneByEconomicalOrBadge(String query) {
        if (query == null || query.isBlank()) {
            throw new ResourceNotFoundException("Vehicle", "query", null);
        }
        String q = query.trim();

        Vehicle v = vehicleRepository.findByEconomicalIgnoreCase(q)
                .or(() -> vehicleRepository.findByBadgeIgnoreCase(q))
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "economicalOrBadge", q));

        return fromEntity(v);
    }

    /**
     * Elimina un vehículo por número económico (case-insensitive).
     */
    @Transactional
    @Override
    public void deleteByEconomical(String economical) {
        if (economical == null || economical.isBlank()) {
            throw new IllegalArgumentException("El número económico es requerido");
        }
        String eco = economical.trim();

        Vehicle v = vehicleRepository.findByEconomicalIgnoreCase(eco)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "economical", eco));

        // (opcional) validaciones de negocio previas aquí

        vehicleRepository.delete(v);
        // alternativamente: vehicleRepository.deleteById(v.getId());
    }
    // ===== Métodos auxiliares =====

    private VehicleDTO fromEntity(Vehicle v) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(v.getId());
        dto.setEconomical(v.getEconomical());
        dto.setBadge(v.getBadge());
        dto.setProperty(v.getProperty().name());
        dto.setMileage(v.getMileage());
        dto.setBrand(v.getBrand());
        dto.setModel(v.getModel());
        dto.setYear(v.getYear());
        dto.setStatus(v.getStatus().name());

        if (v.getWorkCenter() != null) {
            dto.setWorkCenterId(v.getWorkCenter().getId());
            dto.setWorkCenterName(v.getWorkCenter().getName());
        }
        if (v.getProcess() != null) {
            dto.setProcessId(v.getProcess().getId());
            dto.setProcessName(v.getProcess().getName());
        }
        return dto;
    }

    private Vehicle toEntity(VehicleDTO dto) {
        Vehicle v = new Vehicle();
        v.setEconomical(dto.getEconomical());
        v.setBadge(dto.getBadge());
        v.setProperty(Property.valueOf(dto.getProperty().toUpperCase()));
        v.setMileage(dto.getMileage());
        v.setBrand(dto.getBrand());
        v.setModel(dto.getModel());
        v.setYear(dto.getYear());

        WorkCenter wc = workCenterRepository.findById(dto.getWorkCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkCenter", "id", dto.getWorkCenterId()));
        v.setWorkCenter(wc);

        Process pr = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new ResourceNotFoundException("Process", "id", dto.getProcessId()));
        v.setProcess(pr);

        v.setStatus(Status.valueOf(dto.getStatus().toUpperCase().replace(' ', '_')));
        return v;
    }
}