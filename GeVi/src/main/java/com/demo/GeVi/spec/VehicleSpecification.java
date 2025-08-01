package com.demo.GeVi.spec;

import org.springframework.data.jpa.domain.Specification;

import com.demo.GeVi.model.Property;
import com.demo.GeVi.model.Status;
import com.demo.GeVi.model.Vehicle;

public class VehicleSpecification {

    public static Specification<Vehicle> hasWorkCenter(Integer id) {
        return (root, query, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("centroTrabajo").get("id"), id);
    }

    public static Specification<Vehicle> hasProcess(Integer id) {
        return (root, query, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("proceso").get("id"), id);
    }

    public static Specification<Vehicle> hasStatus(String status) {
        return (root, query, cb) -> (status == null || status.isEmpty())
                ? cb.conjunction()
                : cb.equal(root.get("estado"), Status.valueOf(status.toUpperCase().replace(' ', '_')));
    }

    public static Specification<Vehicle> hasProperty(String property) {
        return (root, query, cb) -> (property == null || property.isEmpty())
                ? cb.conjunction()
                : cb.equal(root.get("propiedad"), Property.valueOf(property.toUpperCase()));
    }

    public static Specification<Vehicle> economicalContains(String economical) {
        return (root, query, cb) -> (economical == null || economical.isEmpty())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("economico")), "%" + economical.toLowerCase() + "%");
    }
}
