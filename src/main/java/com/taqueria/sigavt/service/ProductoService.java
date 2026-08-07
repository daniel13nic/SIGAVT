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

    // catálogo completo (para el administrador)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Obtener solo los productos activos (pantalla de ventas)
    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Dar de alta o modificar un producto
    public Producto guardarProducto(Producto producto) {
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

    // Desactivar producto
    public void desactivarProducto(Integer idProducto) {
        Producto productoDb = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Error: El producto especificado no existe."));

        productoDb.setActivo(false);
        productoRepository.save(productoDb);
    }
}