package com.taqueria.sigavt.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {
    private LocalDateTime fecha;
    private String mensaje;
    private String detalles;
    private int codigoEstado;
}