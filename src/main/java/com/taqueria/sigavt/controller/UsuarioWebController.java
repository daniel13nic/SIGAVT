package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.PerfilRepository;
import com.taqueria.sigavt.repository.UsuarioRepository;
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

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioWebController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    @GetMapping
    public String mostrarUsuarios(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }

        var listaPerfiles = perfilRepository.findAll();
        var listaUsuarios = usuarioRepository.findAll();

        model.addAttribute("listaPerfiles", listaPerfiles);
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAllAttributes(usuarioService.calcularMetricasUsuarios(listaUsuarios));

        return "usuarios/usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("listaPerfiles", perfilRepository.findAll());
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            return "usuarios/usuarios";
        }

        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Usuario registrado correctamente!");
        return "redirect:/usuarios";
    }
}