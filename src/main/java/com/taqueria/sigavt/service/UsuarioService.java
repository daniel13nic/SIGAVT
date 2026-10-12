package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.taqueria.sigavt.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios (para el panel del Administrador)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> obtenerPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    // Buscar usuario por "username"
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Crear o actualizar un usuario de forma segura
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            if (!usuario.getPassword().startsWith("$2a$")) {
                String hashSeguro = passwordEncoder.encode(usuario.getPassword());
                usuario.setPassword(hashSeguro);
            }
        }
        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario
    public void eliminarUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }


    public Map<String, Object> calcularMetricasUsuarios(List<Usuario> listaUsuarios) {
        Map<String, Object> metricas = new HashMap<>();

        // Total general
        long totalUsuarios = listaUsuarios.size();

        // Conteo por perfiles
        long totalAdmins = listaUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().getNombre().toLowerCase().contains("administrador"))
                .count();

        long totalOperadores = listaUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().getNombre().toLowerCase().contains("operador"))
                .count();

        long totalConsultores = listaUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().getNombre().toLowerCase().contains("consultor"))
                .count();


        metricas.put("totalUsuarios", totalUsuarios);
        metricas.put("totalAdmins", totalAdmins);
        metricas.put("totalOperadores", totalOperadores);
        metricas.put("totalConsultores", totalConsultores);

        return metricas;
    }
}