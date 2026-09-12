package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Proveedor;
import com.taqueria.sigavt.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

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

        proveedorRepository.delete(existente);
    }
}