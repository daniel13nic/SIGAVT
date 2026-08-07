package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerProductosActivos() {

        Producto bistec = new Producto();
        bistec.setNombre("Bistec");
        bistec.setActivo(true);

        // Simulamos que la base de datos devuelve una lista con el bistec
        when(productoRepository.findByActivoTrue()).thenReturn(Arrays.asList(bistec));

        List<Producto> resultado = productoService.obtenerActivos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(productoRepository, times(1)).findByActivoTrue();
    }

    @Test
    void guardarProductoNuevo() {

        Producto nuevoAguaSabor = new Producto();
        nuevoAguaSabor.setNombre("Agua de Horchata 1L");
        nuevoAguaSabor.setPrecio(new BigDecimal("40.50"));
        // No le asigna el ID ni el estado activo simulando un alta desde la web

        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto guardado = productoService.guardarProducto(nuevoAguaSabor);

        assertTrue(guardado.getActivo(), "El nuevo producto debería estar activo por defecto");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarPrecio() {

        Producto pastor = new Producto();
        pastor.setIdProducto(1);
        pastor.setNombre("Carne al Pastor");
        pastor.setPrecio(new BigDecimal("250.00")); // Precio original

        BigDecimal nuevoPrecio = new BigDecimal("265.50"); // Incremento de precio

        when(productoRepository.findById(1)).thenReturn(Optional.of(pastor));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto actualizado = productoService.actualizarPrecio(1, nuevoPrecio);

        assertEquals(nuevoPrecio, actualizado.getPrecio());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(pastor);
    }

    @Test
    void desactivarProductoExistente() {

        Producto refresco = new Producto();
        refresco.setIdProducto(2);
        refresco.setActivo(true);

        when(productoRepository.findById(2)).thenReturn(Optional.of(refresco));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        productoService.desactivarProducto(2);

        assertFalse(refresco.getActivo(), "El producto debería estar inactivo");
        verify(productoRepository, times(1)).findById(2);
        verify(productoRepository, times(1)).save(refresco);
    }
}