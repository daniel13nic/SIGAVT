package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Proveedor;
import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController {

    private final ProveedorService proveedorService;
    private final ProductoService productoService;

    public ProveedorWebController(ProveedorService proveedorService, ProductoService productoService) {
        this.proveedorService = proveedorService;
        this.productoService = productoService;
    }

    @GetMapping
    public String mostrarModuloProveedores(Model model) {
        model.addAttribute("listaProveedores", proveedorService.obtenerTodos());

        model.addAttribute("listaProductos", productoService.obtenerTodos());

        model.addAttribute("nuevoProveedor", new Proveedor());

        return "proveedores/lista-proveedores";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(Proveedor proveedor) {
        proveedorService.guardar(proveedor);

        return "redirect:/proveedores";
    }
}