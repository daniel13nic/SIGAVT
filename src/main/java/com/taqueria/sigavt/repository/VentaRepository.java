package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {


    List<Venta> findByFechaVenta(LocalDate fechaVenta);

    List<Venta> findByFechaVentaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Page<Venta> findByFechaVentaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    @Query("SELECT v.producto.nombre, SUM(v.cantidad) AS totalVendido " +
            "FROM Venta v GROUP BY v.producto.nombre ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaVenta = :fecha")
    BigDecimal sumarIngresosPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(v.cantidad) FROM Venta v WHERE v.fechaVenta = :fecha")
    BigDecimal sumarArticulosPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta = :fecha AND v.anulada = false")
    long contarVentasPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT AVG(v.totalVenta) FROM Venta v WHERE v.fechaVenta = :fecha AND v.anulada = false")
    BigDecimal promedioVentasPorFecha(@Param("fecha") LocalDate fecha);

    @Query(value = "SELECT p.nombre FROM venta v JOIN producto p ON v.id_producto = p.id_producto WHERE v.fecha_venta = :fecha AND v.anulada = false GROUP BY p.id_producto, p.nombre ORDER BY SUM(v.total_venta) DESC LIMIT 1", nativeQuery = true)
    String obtenerProductoTopDelDia(@Param("fecha") LocalDate fecha);

    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin " +
            "AND (:keyword IS NULL OR :keyword = '' OR CONCAT(v.idVenta, '') LIKE CONCAT('%', :keyword, '%') OR LOWER(v.producto.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:idCategoria IS NULL OR v.producto.categoria.idCategoria = :idCategoria)")
    Page<Venta> buscarVentasConFiltros(@Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin,
                                       @Param("keyword") String keyword,
                                       @Param("idCategoria") Integer idCategoria,
                                       Pageable pageable);


    // Tarjetas Superiores
    @Query("SELECT SUM(v.totalVenta) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria)")
    BigDecimal sumarIngresosPorRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

    @Query("SELECT COUNT(v) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria)")
    Long contarVentasPorRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

    @Query("SELECT AVG(v.totalVenta) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria)")
    BigDecimal obtenerTicketPromedioPorRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

    // 2. Gráficas
    @Query("SELECT v.fechaVenta, SUM(v.totalVenta) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria) " +
            "GROUP BY v.fechaVenta ORDER BY v.fechaVenta ASC")
    List<Object[]> agruparVentasPorDia(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

    @Query("SELECT c.nombre, SUM(v.totalVenta) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria) " +
            "GROUP BY c.nombre ORDER BY SUM(v.totalVenta) DESC")
    List<Object[]> agruparVentasPorCategoria(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

    // 3. Rankings Inferiores
    @Query("SELECT p.nombre, SUM(v.cantidad), SUM(v.totalVenta) FROM Venta v JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria) " +
            "GROUP BY p.nombre ORDER BY SUM(v.totalVenta) DESC")
    List<Object[]> obtenerTopProductos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria, Pageable pageable);

    @Query("SELECT u.nombre, COUNT(v), SUM(v.totalVenta) FROM Venta v JOIN v.usuario u JOIN v.producto p JOIN p.categoria c " +
            "WHERE v.anulada = false AND v.fechaVenta BETWEEN :inicio AND :fin " +
            "AND (:idCategoria IS NULL OR c.idCategoria = :idCategoria) " +
            "GROUP BY u.nombre ORDER BY SUM(v.totalVenta) DESC")
    List<Object[]> obtenerTopUsuarios(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("idCategoria") Integer idCategoria);

}