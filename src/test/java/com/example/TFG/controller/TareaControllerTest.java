package com.example.TFG.controller;

import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TareaController.class)
class TareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsistenteService asistenteService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private IAService iaService;

    @Test
    @WithMockUser
    void deberiaCargarPaginaTareas() throws Exception {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Page<Tarea> pagina = new PageImpl<>(List.of());

        when(asistenteService.obtenerTareas(anyLong(), anyInt()))
                .thenReturn(pagina);

        mockMvc.perform(get("/tareas")
                        .param("pagina", "0")
                        .requestAttr("ADMIN", usuario))
                .andExpect(status().isOk());
    }
}