package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // SELECT * FROM Producto WHERE activo = true
    List<Producto> findByActivoTrue();
}