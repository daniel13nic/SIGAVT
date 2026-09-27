package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public Venta registrarVenta(Venta ventaWeb) {
        // Se extrae el producto real de la Base de Datos
        Producto productoReal = productoRepository.findById(ventaWeb.getProducto().getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("El producto seleccionado no existe."));

        if (!productoReal.getActivo()) {
            throw new IllegalStateException("Operación rechazada: No se puede registrar una venta de un producto inactivo (" + productoReal.getNombre() + ").");
        }

        // Se asigna el precio unitario oficial se usa el de la BD
        ventaWeb.setPrecioUnitario(productoReal.getPrecio());

        // @PrePersist de la entidad hace la multiplicación automáticamente
        return ventaRepository.save(ventaWeb);

    }

    public BigDecimal obtenerIngresosDelDia(LocalDate fecha) {
        BigDecimal total = ventaRepository.sumarIngresosPorFecha(fecha);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal obtenerArticulosVendidosDelDia(LocalDate fecha) {
        BigDecimal total = ventaRepository.sumarArticulosPorFecha(fecha);
        return total != null ? total : BigDecimal.ZERO;
    }


    // Para las tarjetas de métricas
    public long contarVentasDelDia(LocalDate fecha) {
        return ventaRepository.contarVentasPorFecha(fecha);
    }

    public BigDecimal obtenerTicketPromedioDelDia(LocalDate fecha) {
        BigDecimal promedio = ventaRepository.promedioVentasPorFecha(fecha);
        return promedio != null ? promedio : BigDecimal.ZERO;
    }

    public String obtenerProductoTopDelDia(LocalDate fecha) {
        String producto = ventaRepository.obtenerProductoTopDelDia(fecha);
        return producto != null ? producto : "Sin ventas aún";
    }


    public Page<Venta> obtenerVentasPaginadasYFiltradas(String filtroFecha, String keyword, Integer idCategoria, Pageable pageable) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio;
        LocalDate fechaFin = hoy; // Por defecto, las búsquedas terminan el día actual

        if (filtroFecha == null) {
            filtroFecha = "hoy";
        }

        switch (filtroFecha) {
            case "ayer":
                fechaInicio = hoy.minusDays(1);
                fechaFin = hoy.minusDays(1); // inicio y el fin son exactamente ayer
                break;
            case "7dias":
                fechaInicio = hoy.minusDays(7);
                break;
            case "este_mes":
                fechaInicio = hoy.withDayOfMonth(1); // día 1 del mes actual
                break;
            case "hoy":
            default:
                fechaInicio = hoy;
                break;
        }

        String keywordClean = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;


        return ventaRepository.buscarVentasConFiltros(fechaInicio, fechaFin, keywordClean, idCategoria, pageable);
    }



    // Método auxiliar necesario para anular un ticket
    public Venta obtenerVentaPorId(Integer idVenta) {
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("El ticket con folio #" + idVenta + " no fue encontrado."));
    }

    public void anularVenta(Integer idVenta) {
        Venta venta = obtenerVentaPorId(idVenta);
        if (venta.getAnulada()) {
            throw new IllegalStateException("El ticket ya se encuentra anulado.");
        }
        venta.setAnulada(true);
        ventaRepository.save(venta);
    }

}