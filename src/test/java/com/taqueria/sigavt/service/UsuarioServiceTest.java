package com.taqueria.sigavt.service;

import com.taqueria.sigavt.model.Usuario;
import com.taqueria.sigavt.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void obtenerPorUsername() {

        Usuario operador = new Usuario();
        operador.setIdUsuario(1);
        operador.setUsername("operador_tacos");
        operador.setNombre("Juan Pérez");

        when(usuarioRepository.findByUsername("operador_tacos")).thenReturn(Optional.of(operador));

        Optional<Usuario> resultado = usuarioService.obtenerPorUsername("operador_tacos");

        assertTrue(resultado.isPresent(), "El usuario debería ser encontrado");
        assertEquals("Juan Pérez", resultado.get().getNombre());
        verify(usuarioRepository, times(1)).findByUsername("operador_tacos");
    }

    @Test
    void obtenerPorUsername_UsuarioNoExiste() {

        when(usuarioRepository.findByUsername("usuario_inventado")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorUsername("usuario_inventado");

        assertFalse(resultado.isPresent(), "No debería encontrar un usuario que no existe");
        verify(usuarioRepository, times(1)).findByUsername("usuario_inventado");
    }

    @Test
    void guardarUsuario() {

        Usuario nuevoAdmin = new Usuario();
        nuevoAdmin.setUsername("admin_taqueria");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario guardado = usuarioService.guardarUsuario(nuevoAdmin);

        assertNotNull(guardado);
        assertEquals("admin_taqueria", guardado.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_LlamaAlRepositorio() {

        usuarioService.eliminarUsuario(1);

        // Se verificamos que el servicio delegó la tarea de borrado al repositorio exactamente 1 vez
        verify(usuarioRepository, times(1)).deleteById(1);
    }
}