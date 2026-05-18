package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Evento;
import com.example.TFG.modelo.Habito;
import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoRestController {

    private final AsistenteService service;
    private final IAService iaService;

    public EventoRestController(AsistenteService service,
                                IAService iaService) {

        this.service = service;
        this.iaService = iaService;
    }

    // ======================
    // ANALISIS IA
    // ======================
    @GetMapping("/analizar")
    public String analizarEventosIA(@CurrentUser Usuario u) {

        List<Evento> eventos =
                service.obtenerEventos(u.getIdUsuario());

        return iaService.analizarEventos(eventos);
    }

    // ======================
    // JSON CALENDARIO
    // ======================
    @GetMapping("/json")
    public List<Map<String, Object>> json(@CurrentUser Usuario u) {

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
    // CREAR / EDITAR EVENTO
    // ======================
    @PostMapping("/guardar")
    public String guardar(@RequestBody Map<String, String> data,
                          @CurrentUser Usuario u) {

        Evento e;

        if (data.get("idEvento") != null &&
                !data.get("idEvento").isEmpty()) {

            Long idEvento =
                    Long.parseLong(data.get("idEvento"));

            // VALIDACIÓN CENTRALIZADA
            service.validarEvento(idEvento,
                    u.getIdUsuario());

            e = service.buscarEvento(idEvento);

        } else {

            e = new Evento();
            e.setUsuario(u);
        }

        e.setTitulo(data.get("titulo"));
        e.setDescripcion(data.get("descripcion"));
        e.setFecha(LocalDate.parse(data.get("fecha")));

        e.setCompletado(
                "true".equals(data.get("completado"))
        );

        e.setTarea(null);
        e.setHabito(null);

        if (data.get("idTarea") != null &&
                !data.get("idTarea").isEmpty()) {

            Tarea t = service.buscarTarea(
                    Long.parseLong(data.get("idTarea"))
            );

            if (t != null &&
                    t.getUsuario().getIdUsuario()
                            .equals(u.getIdUsuario())) {

                e.setTarea(t);
            }
        }

        if (data.get("idHabito") != null &&
                !data.get("idHabito").isEmpty()) {

            Habito h = service.buscarHabito(
                    Long.parseLong(data.get("idHabito"))
            );

            if (h != null &&
                    h.getUsuario().getIdUsuario()
                            .equals(u.getIdUsuario())) {

                e.setHabito(h);
            }
        }

        service.guardarEvento(e);

        return "ok";
    }

    // ======================
    // ELIMINAR
    // ======================
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @CurrentUser Usuario u) {

        // VALIDACIÓN CENTRALIZADA
        service.validarEvento(id, u.getIdUsuario());

        service.eliminarEvento(id);

        return "ok";
    }
}