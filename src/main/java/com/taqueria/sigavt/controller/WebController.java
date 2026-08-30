package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Producto;
import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.ReporteService;
import com.taqueria.sigavt.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.service.VentaService;
import com.taqueria.sigavt.repository.UsuarioRepository;
import com.taqueria.sigavt.repository.VentaRepository;


import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.PerfilRepository;

@Controller
public class WebController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final ReporteService reporteService;
    private final PerfilRepository perfilRepository;
    private final UsuarioService usuarioService;



    public WebController(ProductoService productoService,
                         CategoriaRepository categoriaRepository,
                         VentaService ventaService,
                         UsuarioRepository usuarioRepository,
                         VentaRepository ventaRepository,
                         ReporteService reporteService,
                         PerfilRepository perfilRepository,
                         UsuarioService usuarioService) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.reporteService = reporteService;
        this.perfilRepository = perfilRepository;
        this.usuarioService = usuarioService;
    }


    // Ruta de la página principal
    @GetMapping("/")
    public String mostrarInicio() {
        return "inicio";
    }

    @GetMapping("/ventas")
    public String mostrarVentas(Model model) {
        if (!model.containsAttribute("venta")) {
            model.addAttribute("venta", new Venta());
        }

        var listaProductos = productoService.obtenerActivos();
        var listaUsuarios = usuarioRepository.findAll();
        var listaVentas = ventaRepository.findAll();

        model.addAttribute("listaProductos", listaProductos);
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAttribute("listaVentas", listaVentas);

        model.addAllAttributes(reporteService.calcularMetricasDashboard(listaVentas));

        return "ventas/ventas";
    }


    @PostMapping("/ventas/guardar")
    public String guardarVenta(@Valid @ModelAttribute("venta") Venta venta,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("listaProductos", productoService.obtenerActivos());
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            model.addAttribute("listaVentas", ventaRepository.findAll());
            return "ventas/ventas";
        }

        try {
            // lógica de negocio
            ventaService.registrarVenta(venta);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Venta registrada! El total se calculó automáticamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/ventas";
    }



    //   Módulo de Productos
    @GetMapping("/productos")
    public String mostrarProductos(Model model) {
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new Producto());
        }

        // Se obtienen las listas completas
        var listaCategorias = categoriaRepository.findAll();
        var listaProductos = productoService.obtenerTodos();

        // cálculo de las métricas usando
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

    @GetMapping("/productos/eliminar")
    public String eliminarProducto(Integer idProducto, RedirectAttributes redirectAttributes) {
        // borrado lógico
        productoService.desactivarProducto(idProducto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto dado de baja correctamente.");
        return "redirect:/productos";
    }



    // Ruta para las métricas
    @GetMapping("/reportes")
    public String mostrarReportes() {
        return "reportes/reportes";
    }



    // Módulo de usuarios
    @GetMapping("/usuarios")
    public String mostrarUsuarios(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }

        var listaPerfiles = perfilRepository.findAll();
        var listaUsuarios = usuarioRepository.findAll();

        model.addAttribute("listaPerfiles", listaPerfiles);
        model.addAttribute("listaUsuarios", listaUsuarios);

        // DELEGAMOS LA LÓGICA AL SERVICIO (Mejor práctica)
        model.addAllAttributes(usuarioService.calcularMetricasUsuarios(listaUsuarios));

        return "usuarios/usuarios";
    }


    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("listaPerfiles", perfilRepository.findAll());
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            return "usuarios/usuarios";
        }

        // Se guarda el usuario
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Usuario registrado correctamente!");
        return "redirect:/usuarios";
    }

}