package com.taqueria.sigavt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Ruta de la página principal
    @GetMapping("/")
    public String mostrarInicio() {
        return "inicio";
    }

    // Ruta para el módulo de ventas
    @GetMapping("/ventas")
    public String mostrarVentas() {
        return "ventas/ventas";
    }

    // Ruta para el catálogo de productos
    @GetMapping("/productos")
    public String mostrarProductos() {
        return "productos/productos";
    }

    // Ruta para las métricas
    @GetMapping("/reportes")
    public String mostrarReportes() {
        return "reportes/reportes";
    }

    // Ruta para la gestión de operadores
    @GetMapping("/usuarios")
    public String mostrarUsuarios() {
        return "usuarios/usuarios";
    }
}
