package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.ProveedorService;
import com.taqueria.sigavt.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class InicioWebController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    public InicioWebController(VentaService ventaService, ProductoService productoService, ProveedorService proveedorService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    // Ruta de la página principal
    @GetMapping({"/", "/inicio"})
    public String mostrarInicio(Model model) {
        LocalDate hoy = LocalDate.now();

        model.addAttribute("nombreUsuario", "Daniel Nicolás");
        model.addAttribute("fechaActual", hoy);

        // Datos para las tarjetas (las métricas)
        model.addAttribute("ventasHoy", ventaService.obtenerIngresosDelDia(hoy));
        model.addAttribute("articulosHoy", ventaService.obtenerArticulosVendidosDelDia(hoy));
        model.addAttribute("productosActivos", productoService.contarProductosActivos());
        model.addAttribute("totalProveedores", proveedorService.contarTotalProveedores());

        return "inicio";
    }
}