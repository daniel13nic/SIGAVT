package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }



    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();

            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                usuario.setPassword(existente.getPassword());
            } else if (!usuario.getPassword().startsWith("$2a$")) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        } else {
            if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }
        return usuarioRepository.save(usuario);
    }



    public void eliminarUsuario(Integer idUsuario) {
        Usuario existente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        existente.setActivo(false);
        usuarioRepository.save(existente);
    }

    public Map<String, Object> calcularMetricasUsuarios(List<Usuario> listaUsuarios) {
        Map<String, Object> metricas = new HashMap<>();

        long totalUsuarios = listaUsuarios.size();

        long totalAdmins = listaUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().getNombre().toLowerCase().contains("administrador"))
                .count();

        long totalOperadores = listaUsuarios.stream()
                .filter(u -> u.getPerfil() != null && u.getPerfil().getNombre().toLowerCase().contains("operador"))
                .count();

        metricas.put("totalUsuarios", totalUsuarios);
        metricas.put("totalAdmins", totalAdmins);
        metricas.put("totalOperadores", totalOperadores);

        return metricas;
    }
}