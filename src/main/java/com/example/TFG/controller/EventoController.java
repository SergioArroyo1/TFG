package com.example.TFG.controller;

import com.example.TFG.modelo.*;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;

    public EventoController(AsistenteService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // ======================
    // CALENDARIO HTML
    // ======================
    @GetMapping
    public String calendario(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        model.addAttribute("eventos", service.obtenerEventos(u.getIdUsuario()));
        model.addAttribute("tareas", service.obtenerTareas(u.getIdUsuario()));
        model.addAttribute("habitos", service.obtenerHabitos(u.getIdUsuario()));

        return "eventos/calendario";
    }

    // ======================
    // JSON CALENDARIO
    // ======================
    @GetMapping("/json")
    @ResponseBody
    public List<Map<String, Object>> json(Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        List<Map<String, Object>> lista = new ArrayList<>();

        service.obtenerEventos(u.getIdUsuario()).forEach(e -> {

            Map<String, Object> map = new HashMap<>();

            map.put("id", e.getIdEvento());
            map.put("title", e.getTitulo());
            map.put("start", e.getFecha());

            // color según tipo
            if (e.getTarea() != null) {
                map.put("color", "#0d6efd"); // azul
            } else if (e.getHabito() != null) {
                map.put("color", "#198754"); // verde
            } else {
                map.put("color", "#6c757d");
            }

            lista.add(map);
        });

        return lista;
    }

    // ======================
    // CREAR / EDITAR EVENTO
    // ======================
    @PostMapping("/guardar-json")
    @ResponseBody
    public String guardar(@RequestBody Map<String, String> data,
                          Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Evento e;

        if (data.get("idEvento") != null && !data.get("idEvento").isEmpty()) {
            e = service.buscarEvento(Long.parseLong(data.get("idEvento")));
        } else {
            e = new Evento();
        }

        e.setTitulo(data.get("titulo"));
        e.setDescripcion(data.get("descripcion"));
        e.setFecha(LocalDate.parse(data.get("fecha")));
        e.setUsuario(u);

        // SOLO UNO: tarea o hábito

        e.setTarea(null);
        e.setHabito(null);

        if (data.get("idTarea") != null && !data.get("idTarea").isEmpty()) {
            Tarea t = service.buscarTarea(Long.parseLong(data.get("idTarea")));
            e.setTarea(t);
        }

        if (data.get("idHabito") != null && !data.get("idHabito").isEmpty()) {
            Habito h = service.buscarHabito(Long.parseLong(data.get("idHabito")));
            e.setHabito(h);
        }

        service.guardarEvento(e);

        return "ok";
    }

    // ======================
    // ELIMINAR
    // ======================
    @DeleteMapping("/eliminar-json/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {

        service.eliminarEvento(id);
        return "ok";
    }
}