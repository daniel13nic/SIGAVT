package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Catálogo completo (para el administrador)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Obtener solo los productos activos (pantalla de ventas)
    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Dar de alta o modificar un producto
    public Producto guardarProducto(Producto producto) {
        // espacios accidentales y se guarda todo en mayúsculas para estandarizar
        if (producto.getNombre() != null) {
            producto.setNombre(producto.getNombre().trim().toUpperCase());
        }

        if (producto.getIdProducto() == null) {
            producto.setActivo(true);
        }
        return productoRepository.save(producto);
    }

    // Actualización de precio
    public Producto actualizarPrecio(Integer idProducto, BigDecimal nuevoPrecio) {
        Producto productoDb = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Error: El producto especificado no existe."));

        productoDb.setPrecio(nuevoPrecio);
        return productoRepository.save(productoDb);
    }

    // orrado lógico para no corromper la BD
    public void desactivarProducto(Integer idProducto) {
        Producto productoDb = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Error: El producto especificado no existe."));

        productoDb.setActivo(false); // Se desactiva  en lugar de hacer DELETE
        productoRepository.save(productoDb);
    }
}