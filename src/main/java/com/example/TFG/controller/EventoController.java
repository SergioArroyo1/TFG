package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final AsistenteService service;

    public EventoController(AsistenteService service) {
        this.service = service;
    }

    // ======================
    // CALENDARIO HTML
    // ======================
    @GetMapping
    public String calendario(@CurrentUser Usuario u,
                             Model model) {

        model.addAttribute("eventos",
                service.obtenerEventos(u.getIdUsuario()));

        model.addAttribute("tareas",
                service.obtenerTareas(u.getIdUsuario()));

        model.addAttribute("habitos",
                service.obtenerHabitos(u.getIdUsuario()));

        return "eventos/calendario";
    }
}