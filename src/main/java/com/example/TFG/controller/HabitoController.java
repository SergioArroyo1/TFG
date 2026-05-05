package com.example.TFG.controller;

import com.example.TFG.modelo.Habito;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/habitos")
public class HabitoController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;
    private final IAService iaService;

    public HabitoController(AsistenteService service,
                            UsuarioService usuarioService,
                            IAService iaService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.iaService = iaService;
    }

    // ==================================================
    // LISTAR HÁBITOS
    // ==================================================
    @GetMapping
    public String listar(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        model.addAttribute("habitos",
                service.obtenerHabitos(u.getIdUsuario()));

        return "habitos/lista";
    }

    // ==================================================
    // IA ANALIZAR HÁBITOS
    // ==================================================
    @GetMapping("/analizar")
    public String analizar(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        var habitos = service.obtenerHabitos(u.getIdUsuario());

        String analisis = iaService.analizarHabitos(habitos);

        model.addAttribute("habitos", habitos);
        model.addAttribute("analisisHabitos", analisis);

        return "habitos/lista";
    }

    // ==================================================
    // CREAR
    // ==================================================
    @PostMapping("/crear")
    public String crear(Habito h, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        h.setUsuario(u);

        service.guardarHabito(h);

        return "redirect:/habitos";
    }

    // ==================================================
    // EDITAR (PROTEGIDO)
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Authentication auth,
                         Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        Habito h = service.buscarHabito(id);

        // PROTECCIÓN
        if (h == null || !h.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/habitos?error=acceso-denegado";
        }

        model.addAttribute("habito", h);
        return "habitos/editar";
    }

    // ==================================================
    // ACTUALIZAR (PROTEGIDO)
    // ==================================================
    @PostMapping("/actualizar")
    public String actualizar(Habito h, Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Habito original = service.buscarHabito(h.getIdHabito());

        // PROTECCIÓN
        if (original == null ||
                !original.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/habitos?error=acceso-denegado";
        }

        h.setUsuario(u);

        service.guardarHabito(h);

        return "redirect:/habitos";
    }

    // ==================================================
    // ELIMINAR (PROTEGIDO)
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        Habito h = service.buscarHabito(id);

        // PROTECCIÓN
        if (h == null || !h.getUsuario().getIdUsuario().equals(u.getIdUsuario())) {
            return "redirect:/habitos?error=acceso-denegado";
        }

        service.eliminarHabito(id);

        return "redirect:/habitos";
    }
}