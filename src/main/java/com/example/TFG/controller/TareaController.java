package com.example.TFG.controller;

import com.example.TFG.modelo.Tarea;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
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

    // Inyección de servicios necesarios para lógica de negocio y usuario
    public TareaController(AsistenteService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // ==================================================
    // LISTAR TAREAS DEL USUARIO AUTENTICADO
    // ==================================================
    @GetMapping
    public String listar(Authentication auth, Model model) {

        // Se obtiene el usuario actual a partir del email de login
        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // Se cargan solo las tareas del usuario logueado
        model.addAttribute("tareas",
                service.obtenerTareas(u.getIdUsuario()));

        return "tareas/lista";
    }

    // ==================================================
    // CREAR NUEVA TAREA
    // ==================================================
    @PostMapping("/crear")
    public String crear(Tarea t, Authentication auth) {

        // Usuario autenticado que crea la tarea
        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // IMPORTANTE: se asigna el usuario para evitar tareas sin propietario
        t.setUsuario(u);

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // EDITAR TAREA
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        // Se envía la tarea al formulario de edición
        model.addAttribute("tarea", service.buscarTarea(id));
        return "tareas/editar";
    }

    // ==================================================
    // ACTUALIZAR TAREA
    // ==================================================
    @PostMapping("/actualizar")
    public String actualizar(Tarea t, Authentication auth) {

        // Usuario autenticado
        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        t.setUsuario(u); // asegura la relacion con el usuario

        service.guardarTarea(t);

        return "redirect:/tareas";
    }

    // ==================================================
    // ELIMINAR TAREA
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        service.eliminarTarea(id);
        return "redirect:/tareas";
    }
}