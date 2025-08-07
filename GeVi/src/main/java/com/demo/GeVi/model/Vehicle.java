package com.demo.GeVi.model;

import java.time.Year;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vehiculo")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "economico", nullable = false, unique = true)
    private String economical;

    @Column(name = "placa", nullable = false, unique = true)
    private String badge;

    @Enumerated(EnumType.STRING)
    @Column(name = "propiedad", nullable = false)
    private Property property;

    @Column(name = "kilometraje", nullable = false)
    private Integer mileage;

    @Column(name = "marca", nullable = false)
    private String brand;

    @Column(name = "modelo", nullable = false)
    private String model;

    @Column(name = "anio", nullable = false)
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centroTrabajoId", nullable = false)
    private WorkCenter workCenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesoId", nullable = false)
    private Process process;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Status status;

}
