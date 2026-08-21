package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

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
}