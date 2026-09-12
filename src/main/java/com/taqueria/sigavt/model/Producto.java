package com.taqueria.sigavt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    // Se valida que siempre se asigne una categoría
    @NotNull(message = "Debe seleccionar una categoría obligatoriamente.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // Se valida que no esté vacío y no pase del límite de la BD (100 caracteres)
    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nombre;

    // Se valida que la unidad de medida exista y no exceda 20 caracteres
    @NotBlank(message = "Debe especificar la unidad de medida (ej. kg, pza).")
    @Size(max = 20, message = "La unidad de medida no puede exceder los 20 caracteres.")
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    // Se valida que el precio se ingrese y sea mayor a 0
    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio debe ser un valor mayor a cero.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El estado del producto no puede ser nulo.")
    @Column(nullable = false)
    private Boolean activo = true;


    @ManyToMany
    @JoinTable(
            name = "producto_proveedor", // tabla intermedia en BD
            joinColumns = @JoinColumn(name = "id_producto"),
            inverseJoinColumns = @JoinColumn(name = "id_proveedor")
    )
    private List<Proveedor> proveedores;

}