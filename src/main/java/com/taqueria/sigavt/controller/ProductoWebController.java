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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public ProductoWebController(ProductoService productoService, CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

    private void cargarDatosModelo(Model model) {
        var listaCategorias = categoriaRepository.findAll();
        var listaProductos = productoService.obtenerTodos();

        long activos = listaProductos.stream().filter(Producto::getActivo).count();
        long inactivos = listaProductos.size() - activos;

        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("totalProductos", listaProductos.size());
        model.addAttribute("totalCategorias", listaCategorias.size());
        model.addAttribute("totalActivos", activos);
        model.addAttribute("totalInactivos", inactivos);
    }

    @GetMapping
    public String mostrarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer idCategoria,
            Model model) {

        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new Producto());
        }

        // se cargan las métricas superiores y el catálogo del formulario
        cargarDatosModelo(model);

        // consulta paginada (10 registros por página)
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("idProducto").ascending());
        Page<Producto> paginaProductos = productoService.obtenerProductosPaginadosYFiltrados(keyword, idCategoria, pageRequest);

        // se envia la tabla y el estado de los filtros a la vista
        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("keywordActual", keyword);
        model.addAttribute("idCategoriaActual", idCategoria);

        return "productos/productos";
    }

    @PostMapping
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Si falta el precio o el nombre, se recarga la vista completa
            cargarDatosModelo(model);

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("idProducto").ascending());
            model.addAttribute("paginaProductos", productoService.obtenerProductosPaginadosYFiltrados(null, null, pageRequest));

            return "productos/productos";
        }

        productoService.guardarProducto(producto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto registrado correctamente.");
        return "redirect:/productos";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam("idProducto") Integer idProducto, RedirectAttributes redirectAttributes) {
        productoService.desactivarProducto(idProducto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto dado de baja correctamente.");
        return "redirect:/productos";
    }
}