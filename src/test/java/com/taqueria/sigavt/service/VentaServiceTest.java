package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.ProductoRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;


    @InjectMocks
    private VentaService ventaService;

    @Test
    void registrarVentaValida() {

        Producto carnePastor = new Producto();

        carnePastor.setIdProducto(1);
        carnePastor.setNombre("Carne al Pastor");
        carnePastor.setUnidadMedida("kg");        // unidad
        carnePastor.setActivo(true);
        carnePastor.setPrecio(new BigDecimal("250.00")); // Precio por 1 kg

        Venta ventaEntrada = new Venta();
        ventaEntrada.setProducto(carnePastor);

        ventaEntrada.setCantidad(new BigDecimal("2.5"));

        when(productoRepository.findById(1)).thenReturn(Optional.of(carnePastor));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Venta ventaResultado = ventaService.registrarVenta(ventaEntrada);

        assertNotNull(ventaResultado);
        // total: 2.5 kg * $250.00 = $625.000
        assertEquals(new BigDecimal("625.000"), ventaResultado.getTotalVenta());
        assertEquals(LocalDate.now(), ventaResultado.getFechaVenta());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void registrarVenta_ProductoInactivo_LanzaExcepcion() {

        Producto refrescoDescontinuado = new Producto();
        refrescoDescontinuado.setIdProducto(2);
        refrescoDescontinuado.setNombre("Manzanita 600ml");
        refrescoDescontinuado.setUnidadMedida("pieza");
        refrescoDescontinuado.setActivo(false); // inactivo

        Venta ventaEntrada = new Venta();
        ventaEntrada.setProducto(refrescoDescontinuado);
        ventaEntrada.setCantidad(new BigDecimal("5.000"));

        when(productoRepository.findById(2)).thenReturn(Optional.of(refrescoDescontinuado));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ventaService.registrarVenta(ventaEntrada);
        });

        // Verificamos que lanza el mensaje de error exacto
        assertEquals("Operación denegada: No se puede registrar una venta para un producto inactivo.", excepcion.getMessage());

        // Al dar error, la venta NUNCA se intentó guardar en BD
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void registrarVenta_ProductoInexistente_LanzaExcepcion() {
        Producto productoFantasma = new Producto();
        productoFantasma.setIdProducto(99);

        Venta ventaEntrada = new Venta();
        ventaEntrada.setProducto(productoFantasma);
        // Intento de vender 1.5 kg de un producto que no existe en el catálogo
        ventaEntrada.setCantidad(new BigDecimal("1.500"));

        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            ventaService.registrarVenta(ventaEntrada);
        });

        assertEquals("Error: El producto especificado no existe.", excepcion.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }
}