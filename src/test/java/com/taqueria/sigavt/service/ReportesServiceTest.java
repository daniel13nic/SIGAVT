package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportesServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void obtenerVentasPorDia() {

        LocalDate hoy = LocalDate.now();
        Venta venta1 = new Venta();
        venta1.setTotalVenta(new BigDecimal("150.00"));

        when(ventaRepository.findByFechaVenta(hoy)).thenReturn(Arrays.asList(venta1));

        List<Venta> resultado = reporteService.obtenerVentasPorDia(hoy);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByFechaVenta(hoy);
    }

    @Test
    void obtenerVentasPorPeriodo() {

        LocalDate inicio = LocalDate.of(2026, 8, 1);
        LocalDate fin = LocalDate.of(2026, 8, 31);
        Venta ventaAgosto = new Venta();

        when(ventaRepository.findByFechaVentaBetween(inicio, fin)).thenReturn(Arrays.asList(ventaAgosto));

        List<Venta> resultado = reporteService.obtenerVentasPorPeriodo(inicio, fin);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findByFechaVentaBetween(inicio, fin);
    }

    @Test
    void obtenerVentasPorPeriodo_FechaInicioMayorQueFin() {

        LocalDate inicioInvalido = LocalDate.of(2026, 9, 1);
        LocalDate fin = LocalDate.of(2026, 8, 31);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            reporteService.obtenerVentasPorPeriodo(inicioInvalido, fin);
        });

        assertEquals("Error: La fecha de inicio no puede ser posterior a la fecha final.", excepcion.getMessage());
        // Verificamos que al fallar la validación, nunca consultó la base de datos
        verify(ventaRepository, never()).findByFechaVentaBetween(any(), any());
    }

    @Test
    void obtenerProductosMasVendidos() {

        Object[] pastor = new Object[]{"Carne al Pastor", new BigDecimal("3.750")};
        Object[] bistec = new Object[]{"Bistec", new BigDecimal("1.250")};

        when(ventaRepository.findProductosMasVendidos()).thenReturn(Arrays.asList(pastor, bistec));

        List<Object[]> resultado = reporteService.obtenerProductosMasVendidos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        // Se verifica el primer producto (el más vendido)
        assertEquals("Carne al Pastor", resultado.get(0)[0]);
        assertEquals(new BigDecimal("3.750"), resultado.get(0)[1]);

        verify(ventaRepository, times(1)).findProductosMasVendidos();
    }
}