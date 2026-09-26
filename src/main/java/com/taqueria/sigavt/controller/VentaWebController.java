package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.UsuarioRepository;
import com.taqueria.sigavt.repository.VentaRepository;
import com.taqueria.sigavt.service.ProductoService;
import com.taqueria.sigavt.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/ventas")
public class VentaWebController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;

    public VentaWebController(VentaService ventaService, ProductoService productoService,
                              UsuarioRepository usuarioRepository, VentaRepository ventaRepository) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping
    public String mostrarVentas(Model model) {
        if (!model.containsAttribute("venta")) {
            model.addAttribute("venta", new Venta());
        }

        model.addAttribute("listaProductos", productoService.obtenerActivos());
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());

        LocalDate hoy = LocalDate.now();
        model.addAttribute("registrosHoy", ventaService.contarVentasDelDia(hoy));
        model.addAttribute("totalVendido", ventaService.obtenerIngresosDelDia(hoy));
        model.addAttribute("ventaPromedio", ventaService.obtenerTicketPromedioDelDia(hoy));
        model.addAttribute("productoTop", ventaService.obtenerProductoTopDelDia(hoy));

        model.addAttribute("listaVentas", ventaRepository.findAll());

        return "ventas/ventas";
    }

    @PostMapping("/guardar")
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
            ventaService.registrarVenta(venta);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Venta registrada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/ventas";
    }
}
