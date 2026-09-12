package com.taqueria.sigavt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProveedor;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @ManyToMany(mappedBy = "proveedores")
    @JsonIgnore
    private List<Producto> productos;
}