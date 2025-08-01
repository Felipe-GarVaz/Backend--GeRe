package com.demo.GeVi.model;

import java.time.LocalDateTime;
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
@Table(name = "reporteDispositivo")
public class DeviceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispositivo", nullable = false)
    private DeviceType device;

    @ManyToOne
    @JoinColumn(name = "centroTrabajoId", nullable = false)
    private WorkCenter workCenter;

    @ManyToOne
    @JoinColumn(name = "tipoFallaDispositivoId")
    private FailTypeDevice failTypeDevice;

    @Column(name = "fallapersonalizada")
    private String personalizedFailure;

    @Column(name = "fechaReporte", nullable = false)
    private LocalDateTime reportingDate = LocalDateTime.now();

}
