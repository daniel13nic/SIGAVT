package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Encontrar ventas de un día específico
    List<Venta> findByFechaVenta(LocalDate fechaVenta);

    // Encontrar ventas en un rango de fechas (Periodo)
    List<Venta> findByFechaVentaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Agrupa por nombre del producto y suma las cantidades
    @Query("SELECT v.producto.nombre, SUM(v.cantidad) AS totalVendido " +
            "FROM Venta v GROUP BY v.producto.nombre ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();


    // Se suma el total de dinero cobrado en una fecha específica
    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaVenta = :fecha")
    BigDecimal sumarIngresosPorFecha(@Param("fecha") LocalDate fecha);

    // Se suma la cantidad de productos (kilos, piezas, etc.) vendidos en una fecha
    @Query("SELECT SUM(v.cantidad) FROM Venta v WHERE v.fechaVenta = :fecha")
    BigDecimal sumarArticulosPorFecha(@Param("fecha") LocalDate fecha);

    // cuántos registros de venta van en el día
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta = :fecha")
    long contarVentasPorFecha(@Param("fecha") LocalDate fecha);

    // Promedio de dinero por venta
    @Query("SELECT AVG(v.totalVenta) FROM Venta v WHERE v.fechaVenta = :fecha")
    BigDecimal promedioVentasPorFecha(@Param("fecha") LocalDate fecha);

    // Agrupa por producto, suma las cantidades y devuelve el nombre del más vendido hoy
    @Query(value = "SELECT p.nombre FROM venta v JOIN producto p ON v.id_producto = p.id_producto WHERE v.fecha_venta = :fecha GROUP BY p.id_producto, p.nombre ORDER BY SUM(v.cantidad) DESC LIMIT 1", nativeQuery = true)
    String obtenerProductoTopDelDia(@Param("fecha") LocalDate fecha);

}