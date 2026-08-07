package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private final VentaRepository ventaRepository;

    public ReporteService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    // Ventas por día
    public List<Venta> obtenerVentasPorDia(LocalDate fecha) {
        return ventaRepository.findByFechaVenta(fecha);
    }

    // Ventas por periodo
    public List<Venta> obtenerVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Error: La fecha de inicio no puede ser posterior a la fecha final.");
        }
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
    }

    // Productos más vendidos
    // Retorna una lista de arreglos donde [0] es el nombre del producto y [1] es la cantidad total (BigDecimal)
    public List<Object[]> obtenerProductosMasVendidos() {
        return ventaRepository.findProductosMasVendidos();
    }
}