package com.demo.GeVi.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

@Entity
@Table(name = "reporteVehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehicleReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculoId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "nuevoEstado", nullable = false)
    private Status newStatus;

    @ManyToOne
    @JoinColumn(name = "tipoFallaId", nullable = true)
    private FailType failType;

    @Column(name = "fallaPersonalizada", nullable = false)
    private String personalizedFailure;

    @Column(name = "kilometraje", nullable = false)
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacionIndisponible", nullable = false)
    private Ubication locationUnavailable;

    @Column(name = "fechaReporte", nullable = false)
    private LocalDateTime reportingDate = LocalDateTime.now();

    @Column(name = "tiempoTranscurrido")
    private Long timeElapsed;

    

}
