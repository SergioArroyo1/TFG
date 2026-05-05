package com.example.TFG.controller;

import com.example.TFG.modelo.*;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
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
    private final IAService iaService;

    public EventoController(AsistenteService service,
                            UsuarioService usuarioService,
                            IAService iaService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.iaService = iaService;
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
    // ANALISIS IA
    // ======================
    @GetMapping("/analizar")
    @ResponseBody
    public String analizarEventosIA(Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        List<Evento> eventos = service.obtenerEventos(u.getIdUsuario());

        return iaService.analizarEventos(eventos);
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

            if (Boolean.TRUE.equals(e.getCompletado())) return;

            Map<String, Object> map = new HashMap<>();

            map.put("id", e.getIdEvento());
            map.put("title", e.getTitulo());
            map.put("start", e.getFecha());

            Map<String, Object> props = new HashMap<>();

            props.put("completado", e.getCompletado());
            props.put("descripcion", e.getDescripcion());

            if (e.getTarea() != null) {
                props.put("idTarea", e.getTarea().getIdTarea());
            }

            if (e.getHabito() != null) {
                props.put("idHabito", e.getHabito().getIdHabito());
            }

            map.put("extendedProps", props);

            if (e.getTarea() != null) {
                map.put("color", "#0d6efd");
            } else if (e.getHabito() != null) {
                map.put("color", "#198754");
            } else {
                map.put("color", "#6c757d");
            }

            lista.add(map);
        });

        return lista;
    }

    // ======================
    // CREAR / EDITAR EVENTO (SEGURIZADO)
    // ======================
    @PostMapping("/guardar-json")
    @ResponseBody
    public String guardar(@RequestBody Map<String, String> data,
                          Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Evento e;

        // SEGURIDAD: validar ownership en edición
        if (data.get("idEvento") != null && !data.get("idEvento").isEmpty()) {

            e = service.buscarEvento(Long.parseLong(data.get("idEvento")));

            if (e == null || !e.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
                return "forbidden";
            }

        } else {
            e = new Evento();
            e.setUsuario(u);
        }

        e.setTitulo(data.get("titulo"));
        e.setDescripcion(data.get("descripcion"));
        e.setFecha(LocalDate.parse(data.get("fecha")));

        e.setCompletado("true".equals(data.get("completado")));

        e.setTarea(null);
        e.setHabito(null);

        if (data.get("idTarea") != null && !data.get("idTarea").isEmpty()) {

            Tarea t = service.buscarTarea(Long.parseLong(data.get("idTarea")));

            // seguridad: tarea debe pertenecer al usuario
            if (t != null && t.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
                e.setTarea(t);
            }
        }

        if (data.get("idHabito") != null && !data.get("idHabito").isEmpty()) {

            Habito h = service.buscarHabito(Long.parseLong(data.get("idHabito")));

            // seguridad: hábito debe pertenecer al usuario
            if (h != null && h.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
                e.setHabito(h);
            }
        }

        service.guardarEvento(e);

        return "ok";
    }

    // ======================
    // ELIMINAR (SEGURIZADO)
    // ======================
    @DeleteMapping("/eliminar-json/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id,
                           Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Evento e = service.buscarEvento(id);

        // SEGURIDAD CRÍTICA
        if (e == null || !e.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "forbidden";
        }

        service.eliminarEvento(id);

        return "ok";
    }
}