package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    // Retorna una lista de arreglos donde [0] es el nombre del producto y [1] es la cantidad total
    public List<Object[]> obtenerProductosMasVendidos() {
        return ventaRepository.findProductosMasVendidos();
    }



    public Map<String, Object> calcularMetricasDashboard(List<Venta> listaVentas) {
        Map<String, Object> metricas = new HashMap<>();

        // Registros del día
        long registrosHoy = listaVentas.stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().isEqual(LocalDate.now()))
                .count();

        // Total vendido
        BigDecimal totalVendido = listaVentas.stream()
                .map(Venta::getTotalVenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Venta promedio
        BigDecimal ventaPromedio = BigDecimal.ZERO;
        if (!listaVentas.isEmpty()) {
            ventaPromedio = totalVendido.divide(BigDecimal.valueOf(listaVentas.size()), 2, RoundingMode.HALF_UP);
        }

        // Producto Top
        String productoTop = "N/A";
        if (!listaVentas.isEmpty()) {
            productoTop = listaVentas.stream()
                    .collect(Collectors.groupingBy(v -> v.getProducto().getNombre(), Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
        }

        metricas.put("registrosHoy", registrosHoy);
        metricas.put("totalVendido", totalVendido);
        metricas.put("ventaPromedio", ventaPromedio);
        metricas.put("productoTop", productoTop);

        return metricas;
    }

}