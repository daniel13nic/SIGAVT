package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    // Búsqueda por nombre o teléfono ignorando mayúsculas/minúsculas
    @Query("SELECT p FROM Proveedor p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.telefono) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Proveedor> buscarPorNombreOTelefono(@Param("keyword") String keyword, Pageable pageable);
}