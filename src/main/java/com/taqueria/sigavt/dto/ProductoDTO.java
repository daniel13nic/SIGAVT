package com.taqueria.sigavt.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoDTO {

    private Integer idProducto;

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "Debe especificar la unidad de medida (ej. kg, pza).")
    private String unidadMedida;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio debe ser un valor mayor a cero.")
    private BigDecimal precio;

    private Boolean activo;


    @NotNull(message = "Debe enviar el ID de la categoría.")
    private Integer idCategoria;

    private String nombreCategoria; // Solo de lectura, se manda al cliente pero no se pide al crear
}