package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class VentaService {

    // Inyección de dependencias a través del constructor
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public Venta registrarVenta(Venta venta) {

        // Se busca el producto en la base de datos usando el ID que viene en la venta
        Producto productoDb = productoRepository.findById(venta.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Error: El producto especificado no existe."));

        // Se verifica que el producto esté activo
        if (!productoDb.getActivo()) {
            throw new RuntimeException("Operación denegada: No se puede registrar una venta para un producto inactivo.");
        }

        // Total de la venta
        BigDecimal total = venta.getCantidad().multiply(productoDb.getPrecio());

        venta.setTotalVenta(total);

        // Se asigna la fecha actual si la venta no trae una
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDate.now());
        }
        return ventaRepository.save(venta);
    }
}