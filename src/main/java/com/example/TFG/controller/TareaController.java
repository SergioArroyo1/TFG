package com.example.TFG.controller;

import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;
    private final IAService iaService;

    public TareaController(AsistenteService service, UsuarioService usuarioService, IAService iaService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.iaService = iaService;
    }

    // ==================================================
    // LISTAR TAREAS
    // ==================================================
    @GetMapping
    public String listar(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        model.addAttribute("tareas",
                service.obtenerTareas(u.getIdUsuario()));

        return "tareas/lista";
    }

    // ==================================================
    // IA ANALIZAR
    // ==================================================
    @GetMapping("/analizar")
    public String analizar(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        var tareas = service.obtenerTareas(u.getIdUsuario());

        String analisis = iaService.analizarTareas(tareas);

        model.addAttribute("tareas", tareas);
        model.addAttribute("analisisTareas", analisis);

        return "tareas/lista";
    }

    // ==================================================
    // CREAR
    // ==================================================
    @PostMapping("/crear")
    public String crear(Tarea t, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        t.setUsuario(u);

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // EDITAR (PROTEGIDO)
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        Tarea t = service.buscarTarea(id);

        // VALIDACIÓN DE SEGURIDAD
        if (t == null || !t.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/tareas?error=acceso-denegado";
        }

        model.addAttribute("tarea", t);
        return "tareas/editar";
    }

    // ==================================================
    // ACTUALIZAR (PROTEGIDO)
    // ==================================================
    @PostMapping("/actualizar")
    public String actualizar(Tarea t, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Tarea original = service.buscarTarea(t.getIdTarea());

        // VALIDACIÓN
        if (original == null || !original.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/tareas?error=acceso-denegado";
        }

        t.setUsuario(u);

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // ELIMINAR (PROTEGIDO)
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        Tarea t = service.buscarTarea(id);

        // VALIDACIÓN
        if (t == null || !t.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/tareas?error=acceso-denegado";
        }

        service.eliminarTarea(id);

        return "redirect:/tareas";
    }
}