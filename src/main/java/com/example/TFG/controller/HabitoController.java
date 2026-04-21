package com.example.TFG.controller;

import com.example.TFG.modelo.Habito;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/habitos")
public class HabitoController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;

    public HabitoController(AsistenteService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // ======================
    // LISTAR
    // ======================
    @GetMapping
    public String listar(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        model.addAttribute("habitos",
                service.obtenerHabitos(u.getIdUsuario()));

        return "habitos/lista";
    }

    // ======================
    // CREAR
    // ======================
    @PostMapping("/crear")
    public String crear(Habito h, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        h.setUsuario(u);
        service.guardarHabito(h);

        return "redirect:/habitos";
    }

    // ======================
    // EDITAR
    // ======================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        model.addAttribute("habito", service.buscarHabito(id));
        return "habitos/editar";
    }

    // ======================
    // ACTUALIZAR
    // ======================
    @PostMapping("/actualizar")
    public String actualizar(Habito h, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        h.setUsuario(u);
        service.guardarHabito(h);

        return "redirect:/habitos";
    }

    // ======================
    // ELIMINAR
    // ======================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        service.eliminarHabito(id);
        return "redirect:/habitos";
    }

    // ======================
    // COMPLETAR HÁBITO DESDE CALENDARIO
    // ======================
    @PostMapping("/completar")
    @ResponseBody
    public String completarHabito(@RequestBody Map<String, String> data) {

        Long idHabito = Long.parseLong(data.get("idHabito"));
        LocalDate fecha = LocalDate.parse(data.get("fecha"));

        service.marcarHabito(idHabito, fecha);

        return "ok";
    }
}