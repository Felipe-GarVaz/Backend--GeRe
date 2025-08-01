package com.demo.GeVi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "dispositivo")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numeroSerie", nullable = false, unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private DeviceStatus status;

    @ManyToOne
    @JoinColumn(name = "centroTrabajoId", nullable = false)
    private WorkCenter workCenter;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoDispositivo", nullable = false)
    private DeviceType deviceType;

    public enum DeviceStatus {
        ACTIVO,
        DEFECTUOSO
    }

}
