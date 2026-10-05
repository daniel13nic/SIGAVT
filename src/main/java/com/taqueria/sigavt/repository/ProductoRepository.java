package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Categoria;
import com.taqueria.sigavt.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // SELECT * FROM Producto WHERE activo = true
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoria(Categoria categoria);

    // automáticamente se hace: SELECT COUNT(*) FROM Producto WHERE activo = true
    long countByActivoTrue();


    // opcionalmente busca por keyword (ID o nombre) y opcionalmente por categoría
    @Query("SELECT p FROM Producto p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR CONCAT(p.idProducto, '') LIKE CONCAT('%', :keyword, '%') OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria)")
    Page<Producto> buscarProductosConFiltros(@Param("keyword") String keyword,
                                             @Param("idCategoria") Integer idCategoria,
                                             Pageable pageable);

}