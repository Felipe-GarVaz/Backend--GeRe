package com.demo.GeVi.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@Table(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rpe", nullable = false, unique = true)
    private String rpe;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "apellidos", nullable = false)
    private String lastName;

    @Column(name = "contrasenia", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarioRol", joinColumns = 
    @JoinColumn(name = "usuarioId"), inverseJoinColumns = 
    @JoinColumn(name = "rolId"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

}