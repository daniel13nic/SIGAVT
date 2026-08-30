package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.UsuarioRepository;
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

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    // Crear o actualizar un usuario
    public Usuario guardarUsuario(Usuario usuario) {
        // contraseña plana
        // se encriptaría con BCryptPasswordEncoder antes de mandarla a la BD.
        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario
    public void eliminarUsuario(Integer idUsuario) {
        /*
         * NOTA:
         * Si un Operador ya registró ventas en la base de datos, intentar borrarlo
         * con este método arrojará un error de llave foránea (para proteger el historial
         * de la taquería). Se agregaría un campo 'activo' a la tabla Usuario para hacer
         * una baja lógica, como con Producto.
         */
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