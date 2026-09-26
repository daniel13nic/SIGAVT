package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.repository.UsuarioRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/ventas")
public class VentaWebController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final CategoriaRepository categoriaRepository;

    public VentaWebController(VentaService ventaService, ProductoService productoService,
                              UsuarioRepository usuarioRepository, VentaRepository ventaRepository,
                              CategoriaRepository categoriaRepository) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.categoriaRepository = categoriaRepository;
    }


    private void cargarDatosModelo(Model model) {
        model.addAttribute("listaProductos", productoService.obtenerActivos());
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());

        model.addAttribute("listaCategorias", categoriaRepository.findAll());

        LocalDate hoy = LocalDate.now();
        model.addAttribute("registrosHoy", ventaService.contarVentasDelDia(hoy));
        model.addAttribute("totalVendido", ventaService.obtenerIngresosDelDia(hoy));
        model.addAttribute("ventaPromedio", ventaService.obtenerTicketPromedioDelDia(hoy));
        model.addAttribute("productoTop", ventaService.obtenerProductoTopDelDia(hoy));

        // model.addAttribute("listaVentas", ventaRepository.findAll());
    }


    @GetMapping
    public String mostrarVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "hoy") String filtroFecha,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer idCategoria,
            Model model) {

        if (!model.containsAttribute("venta")) {
            model.addAttribute("venta", new Venta());
        }

        cargarDatosModelo(model);


        // petición de paginación (página actual, 10 registros, orden descendente)
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("idVenta").descending());

        // Ejecuta la consulta paginada
        Page<Venta> paginaVentas = ventaService.obtenerVentasPaginadasYFiltradas(filtroFecha, keyword, idCategoria, pageRequest);


        model.addAttribute("paginaVentas", paginaVentas);

        model.addAttribute("filtroFechaActual", filtroFecha);
        model.addAttribute("keywordActual", keyword);
        model.addAttribute("idCategoriaActual", idCategoria);

        return "ventas/ventas";
    }


    @PostMapping("/guardar")
    public String guardarVenta(@Valid @ModelAttribute("venta") Venta venta,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Si hay error, se recarga todo el modelo
            cargarDatosModelo(model);
            return "ventas/ventas";
        }

        try {
            ventaService.registrarVenta(venta);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Venta registrada!.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/ventas";
    }
}