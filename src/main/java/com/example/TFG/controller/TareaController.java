package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.modelo.enums.EstadoTarea;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final AsistenteService service;
    // private final UsuarioService usuarioService;
    private final IAService iaService;

    public TareaController(AsistenteService service,
                           IAService iaService) {
        this.service = service;
        this.iaService = iaService;
    }

    // LISTAR
    @GetMapping
    public String listar(@CurrentUser Usuario u, Model model) {

        model.addAttribute("tareas",
                service.obtenerTareas(u.getIdUsuario()));

        return "tareas/lista";
    }

    // IA
    @GetMapping("/analizar")
    public String analizar(@CurrentUser Usuario u, Model model) {

        var tareas = service.obtenerTareas(u.getIdUsuario());
        String analisis = iaService.analizarTareas(tareas);

        model.addAttribute("tareas", tareas);
        model.addAttribute("analisisTareas", analisis);

        return "tareas/lista";
    }

    // CREAR
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute Tarea t,
                        BindingResult br,
                        @CurrentUser Usuario u,
                        Model model) {

        if (br.hasErrors()) {
            model.addAttribute("tareas",
                    service.obtenerTareas(u.getIdUsuario()));
            return "tareas/lista";
        }

        t.setUsuario(u);

        if (t.getEstado() == null) {
            t.setEstado(EstadoTarea.PENDIENTE);
        }

        if (t.getPrioridad() == null) {
            t.setPrioridad(0);
        }

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @CurrentUser Usuario u,
                         Model model) {

        service.validarTarea(id, u.getIdUsuario());

        Tarea t = service.buscarTarea(id);

        model.addAttribute("tarea", t);
        model.addAttribute("estados", EstadoTarea.values());

        return "tareas/editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Tarea t,
                             BindingResult br,
                             @CurrentUser Usuario u,
                             Model model) {

        service.validarTarea(t.getIdTarea(), u.getIdUsuario());

        if (br.hasErrors()) {
            model.addAttribute("tarea", t);
            model.addAttribute("estados", EstadoTarea.values());
            return "tareas/editar";
        }

        t.setUsuario(u);

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @CurrentUser Usuario u) {

        service.validarTarea(id, u.getIdUsuario());

        service.eliminarTarea(id);

        return "redirect:/tareas";
    }
}