package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}