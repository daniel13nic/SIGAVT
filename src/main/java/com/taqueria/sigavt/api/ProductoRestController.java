package com.taqueria.sigavt.api;

import com.taqueria.sigavt.dto.ProductoDTO;
import com.taqueria.sigavt.mapper.ProductoMapper;
import com.taqueria.sigavt.model.Producto;
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

    public ProductoRestController(ProductoService productoService,
                                  ProductoMapper productoMapper,
                                  ProductoRepository productoRepository) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
        this.productoRepository = productoRepository;
    }

    // Consulta exitosa 200 OK
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> listaDTO = productoService.obtenerTodos()
                .stream()
                .map(productoMapper::aDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaDTO);
    }

    // 2. Creación de un recurso 201 Created
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        // Se convierte el DTO a Entidad para que el Service lo entienda
        Producto entidad = productoMapper.aEntidad(productoDTO);

        Producto guardado = productoService.guardarProducto(entidad);

        // Se convierte de vuelta a DTO para responderle al cliente
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.aDTO(guardado));
    }

    // Actualización de un recurso 200 OK / 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Integer id, @Valid @RequestBody ProductoDTO productoDTO) {
        // Validamos existencia. Si falla, el GlobalExceptionHandler lanza un 404
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + id + " no existe."));

        Producto entidadActualizada = productoMapper.aEntidad(productoDTO);
        entidadActualizada.setIdProducto(id);
        entidadActualizada.setActivo(existente.getActivo());

        Producto guardado = productoService.guardarProducto(entidadActualizada);
        return ResponseEntity.ok(productoMapper.aDTO(guardado));
    }

    // Eliminación exitosa (borrado lógico) 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + id + " no existe."));

        productoService.desactivarProducto(id);

        // 204 No Content indica éxito pero sin cuerpo en la respuesta
        return ResponseEntity.noContent().build();
    }
}