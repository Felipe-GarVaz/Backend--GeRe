package com.demo.GeVi.service;

import com.demo.GeVi.dto.VehicleDTO;
import com.demo.GeVi.exception.ResourceNotFoundException;
import com.demo.GeVi.model.Process;
import com.demo.GeVi.model.Property;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Vehicle;
import com.demo.GeVi.model.WorkCenter;
import com.demo.GeVi.repository.ProcessRepository;
import com.demo.GeVi.repository.VehicleRepository;
import com.demo.GeVi.repository.WorkCenterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

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

    @Override
    public List<VehicleDTO> findAll(Integer workCenterId, Integer processId, String status, String property,
            String economical) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vehicle> query = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = query.from(Vehicle.class);

        List<Predicate> predicates = new ArrayList<>();

        if (workCenterId != null) {
            predicates.add(cb.equal(root.get("workCenter").get("id"), workCenterId));
        }
        if (processId != null) {
            predicates.add(cb.equal(root.get("process").get("id"), processId));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(cb.equal(root.get("status"), Status.valueOf(status)));
        }
        if (property != null && !property.isEmpty()) {
            predicates.add(cb.equal(root.get("property"), Property.valueOf(property)));
        }
        if (economical != null && !economical.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("economical")), "%" + economical.toLowerCase() + "%"));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Vehicle> vehicles = entityManager.createQuery(query).getResultList();

        return vehicles.stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO save(VehicleDTO dto) {
        Vehicle vehicle = toEntity(dto);
        Vehicle saved = vehicleRepository.save(vehicle);
        return fromEntity(saved);
    }

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

    @Override
    public void delete(Integer id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", "id", id);
        }
        vehicleRepository.deleteById(id);
    }

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

    @Override
    public Map<String, List<Map<String, Object>>> getFilterOptions() {
        // Centro de trabajo
        List<Map<String, Object>> workCenters = workCenterRepository.findAll()
                .stream()
                .map(wc -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", wc.getId());
                    m.put("name", wc.getName());
                    return m;
                })
                .collect(Collectors.toList());

        // Procesos
        List<Map<String, Object>> processes = processRepository.findAll()
                .stream()
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    return m;
                })
                .collect(Collectors.toList());

        // Estados (enum)
        List<Map<String, Object>> statuses = Arrays.stream(Status.values())
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.name());
                    m.put("name", s.name().replace("_", " "));
                    return m;
                })
                .collect(Collectors.toList());

        // Propiedades (enum)
        List<Map<String, Object>> properties = Arrays.stream(Property.values())
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.name());
                    m.put("name", p.name());
                    return m;
                })
                .collect(Collectors.toList());

        // Empaquetamos todo
        Map<String, List<Map<String, Object>>> options = new HashMap<>();
        options.put("centroTrabajo", workCenters);
        options.put("proceso", processes);
        options.put("estado", statuses);
        options.put("propiedad", properties);

        return options;
    }

    @Override
    public List<VehicleDTO> searchVehicles(String query) {
        List<Vehicle> vehicles = vehicleRepository.searchByEconomicalOrBadge(query);
        return vehicles.stream().map(VehicleDTO::fromEntity).toList();
    }

    @Override
    public List<Vehicle> getAllVehicles(){
        return vehicleRepository.findAll();
    }
}
