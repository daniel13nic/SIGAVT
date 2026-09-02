package com.taqueria.sigavt.api;

import com.taqueria.sigavt.dto.ProductoDTO;
import com.taqueria.sigavt.mapper.ProductoMapper;
import com.taqueria.sigavt.model.Categoria;
import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public CategoriaRestController(CategoriaRepository categoriaRepository,
                                   ProductoRepository productoRepository,
                                   ProductoMapper productoMapper) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    // Consulta todos los productos asociados a una categoría
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorCategoria(@PathVariable Integer id) {

        // Se buscamos la Categoría, si no existe, GlobalExceptionHandler lanzará un 404
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La categoría con ID " + id + " no existe."));

        List<Producto> productosAsociados = productoRepository.findByCategoria(categoria);

        // entidades a DTOs para proteger la información en la salida JSON
        List<ProductoDTO> respuesta = productosAsociados.stream()
                .map(productoMapper::aDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }
}