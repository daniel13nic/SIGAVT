package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public WebController(ProductoService productoService, CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

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


    //   Módulo de Productos
    @GetMapping("/productos")
    public String mostrarProductos(Model model) {

        // objeto vacío para el formulario
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new Producto());
        }

        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        model.addAttribute("listaProductos", productoService.obtenerTodos());

        return "productos/productos";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Si las validaciones del modelo fallan
        if (result.hasErrors()) {
            // Se recargan las listas para que la vista no falle
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            model.addAttribute("listaProductos", productoService.obtenerTodos());

            return "productos/productos";
        }

        productoService.guardarProducto(producto);

        redirectAttributes.addFlashAttribute("mensajeExito", "Producto registrado correctamente");
        return "redirect:/productos";
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