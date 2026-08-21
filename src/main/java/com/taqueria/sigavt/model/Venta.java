package com.taqueria.sigavt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    // Se valida que la venta siempre tenga un producto asociado
    @NotNull(message = "Debe seleccionar un producto para registrar la venta.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Se valida que usuario hizo el registro
    @NotNull(message = "El usuario responsable del registro es obligatorio.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    // Se valida que la cantidad sea obligatoria y mayor a cero
    @NotNull(message = "Debe ingresar la cantidad vendida.")
    @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a cero.")
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario no puede estar vacío.")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero.")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "total_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVenta;


    @PrePersist
    @PreUpdate
    public void ejecutarLogicaAntesDeGuardar() {

        // Si la venta es nueva y no tiene fecha se asigna la fecha de hoy
        if (this.fechaVenta == null) {
            this.fechaVenta = LocalDate.now();
        }

        // Se calcula automáticamente el Total (Cantidad * Precio)
        if (this.cantidad != null && this.precioUnitario != null) {
            this.totalVenta = this.cantidad.multiply(this.precioUnitario);
        }
    }

}