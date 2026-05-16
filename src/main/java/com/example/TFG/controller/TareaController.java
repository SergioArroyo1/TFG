package com.example.TFG.controller;

import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;
    private final IAService iaService;

    public TareaController(AsistenteService service,
                           UsuarioService usuarioService,
                           IAService iaService) {
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
    public String crear(@Valid @ModelAttribute Tarea t,
                        BindingResult br,
                        Authentication auth,
                        Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        if (br.hasErrors()) {
            model.addAttribute("tareas",
                    service.obtenerTareas(u.getIdUsuario()));
            return "tareas/lista";
        }

        t.setUsuario(u);
        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // EDITAR
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Authentication auth,
                         Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA EN SERVICE
        service.validarTarea(id, u.getIdUsuario());

        Tarea t = service.buscarTarea(id);

        model.addAttribute("tarea", t);
        return "tareas/editar";
    }

    // ==================================================
    // ACTUALIZAR
    // ==================================================
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Tarea t,
                             BindingResult br,
                             Authentication auth,
                             Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA EN SERVICE
        service.validarTarea(t.getIdTarea(), u.getIdUsuario());

        if (br.hasErrors()) {
            model.addAttribute("tarea", t);
            return "tareas/editar";
        }

        t.setUsuario(u);
        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // ELIMINAR
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA EN SERVICE
        service.validarTarea(id, u.getIdUsuario());

        service.eliminarTarea(id);

        return "redirect:/tareas";
    }
}