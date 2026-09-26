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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public ProductoWebController(ProductoService productoService, CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String mostrarProductos(Model model) {
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new Producto());
        }

        // Se obtienen las listas completas
        var listaCategorias = categoriaRepository.findAll();
        var listaProductos = productoService.obtenerTodos();

        long activos = listaProductos.stream().filter(Producto::getActivo).count();
        long inactivos = listaProductos.size() - activos;


        // Se envian las listas para la tabla y el formulario
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaProductos", listaProductos);

        // métricas para las tarjetas superiores de la vista de productos
        model.addAttribute("totalProductos", listaProductos.size());
        model.addAttribute("totalCategorias", listaCategorias.size());
        model.addAttribute("totalActivos", activos);
        model.addAttribute("totalInactivos", inactivos);

        return "productos/productos";
    }

    @PostMapping("/guardar")
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

    @GetMapping("/eliminar")
    public String eliminarProducto(Integer idProducto, RedirectAttributes redirectAttributes) {
        // borrado lógico
        productoService.desactivarProducto(idProducto);

        redirectAttributes.addFlashAttribute("mensajeExito", "Producto dado de baja correctamente.");
        return "redirect:/productos";
    }
}