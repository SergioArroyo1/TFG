package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Habito;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/habitos")
public class HabitoController {

    private final AsistenteService service;
    private final IAService iaService;

    public HabitoController(AsistenteService service,
                            IAService iaService) {
        this.service = service;
        this.iaService = iaService;
    }

    // ==================================================
    // LISTAR HÁBITOS
    // ==================================================
    @GetMapping
    public String listar(@CurrentUser Usuario usuario,
                         Model model) {

        model.addAttribute("habitos",
                service.obtenerHabitos(usuario.getIdUsuario()));

        return "habitos/lista";
    }

    // ==================================================
    // IA ANALIZAR HÁBITOS
    // ==================================================
    @GetMapping("/analizar")
    public String analizar(@CurrentUser Usuario usuario,
                           Model model) {

        var habitos = service.obtenerHabitos(usuario.getIdUsuario());

        String analisis = iaService.analizarHabitos(habitos);

        model.addAttribute("habitos", habitos);
        model.addAttribute("analisisHabitos", analisis);

        return "habitos/lista";
    }

    // ==================================================
    // CREAR
    // ==================================================
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute Habito habito,
                        BindingResult br,
                        @CurrentUser Usuario usuario,
                        Model model) {

        if (br.hasErrors()) {

            model.addAttribute("habitos",
                    service.obtenerHabitos(usuario.getIdUsuario()));

            return "habitos/lista";
        }

        habito.setUsuario(usuario);

        service.guardarHabito(habito);

        return "redirect:/habitos";
    }

    // ==================================================
    // EDITAR
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @CurrentUser Usuario usuario,
                         Model model) {

        // VALIDACIÓN CENTRALIZADA
        service.validarHabito(id, usuario.getIdUsuario());

        Habito habito = service.buscarHabito(id);

        model.addAttribute("habito", habito);

        return "habitos/editar";
    }

    // ==================================================
    // ACTUALIZAR
    // ==================================================
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Habito habito,
                             BindingResult br,
                             @CurrentUser Usuario usuario,
                             Model model) {

        // VALIDACIÓN CENTRALIZADA
        service.validarHabito(
                habito.getIdHabito(),
                usuario.getIdUsuario()
        );

        if (br.hasErrors()) {
            model.addAttribute("habito", habito);
            return "habitos/editar";
        }

        habito.setUsuario(usuario);

        service.guardarHabito(habito);

        return "redirect:/habitos";
    }

    // ==================================================
    // ELIMINAR
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @CurrentUser Usuario usuario) {

        // VALIDACIÓN CENTRALIZADA
        service.validarHabito(id, usuario.getIdUsuario());

        service.eliminarHabito(id);

        return "redirect:/habitos";
    }
}