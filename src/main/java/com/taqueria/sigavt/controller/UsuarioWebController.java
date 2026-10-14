package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.PerfilRepository;
import com.taqueria.sigavt.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final PerfilRepository perfilRepository;

    public UsuarioWebController(UsuarioService usuarioService, PerfilRepository perfilRepository) {
        this.usuarioService = usuarioService;
        this.perfilRepository = perfilRepository;
    }

    private void cargarDatosModelo(Model model) {
        List<Usuario> listaUsuarios = usuarioService.obtenerTodos();

        model.addAttribute("listaPerfiles", perfilRepository.findAll());
        model.addAttribute("listaUsuarios", listaUsuarios);

        // SOLUCIÓN AL ERROR: Empaquetamos todo bajo la llave "metricasUsuarios"
        model.addAttribute("metricasUsuarios", usuarioService.calcularMetricasUsuarios(listaUsuarios));
    }

    @GetMapping
    public String mostrarUsuarios(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }

        cargarDatosModelo(model);

        return "usuarios/usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (usuario.getIdUsuario() != null && result.hasFieldErrors("password")) {
            result = new org.springframework.validation.BeanPropertyBindingResult(usuario, "usuario");
        } else if (usuario.getIdUsuario() == null && (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty())) {
            result.rejectValue("password", "error.usuario", "La contraseña es obligatoria para nuevos usuarios.");
        }

        if (result.hasErrors()) {
            cargarDatosModelo(model);
            return "usuarios/usuarios";
        }

        try {
            usuarioService.guardarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Usuario guardado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar el usuario: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@org.springframework.web.bind.annotation.RequestParam("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(idUsuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado permanentemente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar este usuario porque ya tiene ventas registradas en el sistema.");
        }
        return "redirect:/usuarios";
    }

}