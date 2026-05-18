package com.example.TFG.service;

import com.example.TFG.modelo.Usuario;
import com.example.TFG.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void buscarUsuarioExistente() {

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@gmail.com");

        when(usuarioRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorEmail("admin@gmail.com");

        assertNotNull(resultado);
        assertEquals("admin@gmail.com", resultado.getEmail());
    }

    @Test
    void buscarUsuarioInexistente() {

        when(usuarioRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> usuarioService.buscarPorEmail("test@gmail.com")
        );

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }
}