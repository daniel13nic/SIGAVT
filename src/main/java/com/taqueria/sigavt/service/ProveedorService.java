package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.model.Proveedor;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    public ProveedorService(ProveedorRepository proveedorRepository, ProductoRepository productoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
    }

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    @Transactional
    public Proveedor guardar(Proveedor proveedor) {
        boolean esActualizacion = proveedor.getIdProveedor() != null;

        if (esActualizacion) {
            Proveedor existente = proveedorRepository.findById(proveedor.getIdProveedor()).orElseThrow();
            if (existente.getProductos() != null) {
                for (Producto p : existente.getProductos()) {
                    p.getProveedores().remove(existente);
                }
                productoRepository.saveAll(existente.getProductos());
            }
        }

        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);

        if (proveedor.getProductosIds() != null && !proveedor.getProductosIds().isEmpty()) {
            List<Producto> productosSeleccionados = productoRepository.findAllById(proveedor.getProductosIds());
            for (Producto producto : productosSeleccionados) {
                if (!producto.getProveedores().contains(proveedorGuardado)) {
                    producto.getProveedores().add(proveedorGuardado);
                }
            }
            productoRepository.saveAll(productosSeleccionados);
        }

        return proveedorGuardado;
    }

    @Transactional
    public Proveedor actualizar(Integer id, Proveedor proveedorActualizado) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El proveedor con ID " + id + " no existe."));

        existente.setNombre(proveedorActualizado.getNombre());
        existente.setTelefono(proveedorActualizado.getTelefono());


        return proveedorRepository.save(existente);
    }

    public void eliminar(Integer id) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El proveedor con ID " + id + " no existe."));

        if (existente.getProductos() != null) {
            for (Producto p : existente.getProductos()) {
                p.getProveedores().remove(existente);
            }
            productoRepository.saveAll(existente.getProductos());
        }

        proveedorRepository.delete(existente);
    }

    public long contarTotalProveedores() {
        return proveedorRepository.count();
    }

    public Proveedor obtenerPorId(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        if (proveedor.getProductos() != null) {
            List<Integer> ids = proveedor.getProductos().stream()
                    .map(Producto::getIdProducto)
                    .collect(Collectors.toList());
            proveedor.setProductosIds(ids);
        }
        return proveedor;
    }


    public Page<Proveedor> obtenerProveedoresPaginados(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return proveedorRepository.buscarPorNombreOTelefono(keyword.trim(), pageable);
        }
        return proveedorRepository.findAll(pageable);
    }


    public long contarProductosConProveedor() {
        List<Producto> todosLosProductos = productoRepository.findAll();
        return todosLosProductos.stream()
                .filter(p -> p.getProveedores() != null && !p.getProveedores().isEmpty())
                .count();
    }

    public long contarProductosSinProveedor() {
        List<Producto> todosLosProductos = productoRepository.findAll();
        return todosLosProductos.stream()
                .filter(p -> p.getProveedores() == null || p.getProveedores().isEmpty())
                .count();
    }

    public double calcularPromedioProductosPorProveedor() {
        long totalProveedores = contarTotalProveedores();
        if (totalProveedores == 0) return 0.0;

        List<Producto> productosConAsignacion = productoRepository.findAll().stream()
                .filter(p -> p.getProveedores() != null && !p.getProveedores().isEmpty())
                .toList();

        return (double) productosConAsignacion.size() / totalProveedores;
    }

}