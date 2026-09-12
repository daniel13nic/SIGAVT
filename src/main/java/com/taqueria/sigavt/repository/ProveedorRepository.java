package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
}