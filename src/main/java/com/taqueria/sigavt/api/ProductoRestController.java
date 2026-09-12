package com.taqueria.sigavt.api;

import com.taqueria.sigavt.dto.ProductoDTO;
import com.taqueria.sigavt.mapper.ProductoMapper;
import com.taqueria.sigavt.model.Categoria;
import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository; // Inyectamos el repositorio

    public ProductoRestController(ProductoService productoService,
                                  ProductoMapper productoMapper,
                                  ProductoRepository productoRepository,
                                  CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> listaDTO = productoService.obtenerTodos()
                .stream()
                .map(productoMapper::aDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Producto entidad = productoMapper.aEntidad(productoDTO);

        // Se verifica que la categoría exista antes de guardar
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new EntityNotFoundException("La categoría con ID " + productoDTO.getIdCategoria() + " no existe en la base de datos."));
        entidad.setCategoria(categoria);

        Producto guardado = productoService.guardarProducto(entidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.aDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Integer id, @Valid @RequestBody ProductoDTO productoDTO) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + id + " no existe."));

        Producto entidadActualizada = productoMapper.aEntidad(productoDTO);
        entidadActualizada.setIdProducto(id);
        entidadActualizada.setActivo(existente.getActivo());

        // Se verifica la categoría en la actualización
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new EntityNotFoundException("La categoría con ID " + productoDTO.getIdCategoria() + " no existe."));
        entidadActualizada.setCategoria(categoria);

        Producto guardado = productoService.guardarProducto(entidadActualizada);
        return ResponseEntity.ok(productoMapper.aDTO(guardado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + id + " no existe."));

        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
    //
}