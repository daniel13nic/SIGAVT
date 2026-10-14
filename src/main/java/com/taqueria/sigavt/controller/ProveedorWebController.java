package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Proveedor;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController {

    private final ProveedorService proveedorService;
    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public ProveedorWebController(ProveedorService proveedorService, ProductoService productoService,
                                  CategoriaRepository categoriaRepository) {
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

    private void cargarDatosModelo(Model model) {
        // Listas para los formularios y modales
        model.addAttribute("listaProductos", productoService.obtenerActivos());
        model.addAttribute("listaCategorias", categoriaRepository.findAll());

        // Métricas de las tarjetas superiores
        model.addAttribute("totalProveedores", proveedorService.contarTotalProveedores());
        model.addAttribute("productosConProveedor", proveedorService.contarProductosConProveedor());
        model.addAttribute("productosSinProveedor", proveedorService.contarProductosSinProveedor());
    }

    @GetMapping
    public String mostrarModuloProveedores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        cargarDatosModelo(model);

        if (!model.containsAttribute("proveedor")) {
            model.addAttribute("proveedor", new Proveedor());
        }

        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("nombre").ascending());

        Page<Proveedor> paginaProveedores = proveedorService.obtenerProveedoresPaginados(keyword, pageRequest);

        model.addAttribute("paginaProveedores", paginaProveedores);
        model.addAttribute("keywordActual", keyword);

        return "proveedores/lista-proveedores";
    }

    @PostMapping
    public String guardarProveedor(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Si el formulario llega con erroresm se recarga la vista
        if (result.hasErrors()) {
            cargarDatosModelo(model);
            return "proveedores/lista-proveedores";
        }

        try {
            proveedorService.guardar(proveedor);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Proveedor guardado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar el proveedor: " + e.getMessage());
        }

        return "redirect:/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String cargarProveedorParaEdicion(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Proveedor proveedorExistente = proveedorService.obtenerPorId(id);
            model.addAttribute("proveedor", proveedorExistente);
            cargarDatosModelo(model);
            return "proveedores/lista-proveedores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "El proveedor no existe.");
            return "redirect:/proveedores";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarProveedor(@RequestParam("idProveedor") Integer idProveedor, RedirectAttributes redirectAttributes) {
        try {
            proveedorService.eliminar(idProveedor);
            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor eliminado permanentemente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar el proveedor: " + e.getMessage());
        }
        return "redirect:/proveedores";
    }
}