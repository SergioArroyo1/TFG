package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Habito;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    // ==================================================
// LISTAR HÁBITOS
// ==================================================
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(required = false) String buscar,
                         @CurrentUser Usuario usuario,
                         Model model) {

        Page<Habito> habitosPage = service.buscarHabitosPorNombre(
                        usuario.getIdUsuario(),
                        buscar,
                        pagina
                );

        model.addAttribute("habitos",
                habitosPage.getContent());

        model.addAttribute("paginaActual",
                pagina);

        model.addAttribute("totalPaginas",
                habitosPage.getTotalPages());

        model.addAttribute("buscar",
                buscar);

        return "habitos/lista";
    }

    // ==================================================
    // IA ANALIZAR HÁBITOS
    // ==================================================
    @GetMapping("/analizar")
    public String analizar(@RequestParam(defaultValue = "0") int pagina,
                           @CurrentUser Usuario usuario,
                           Model model) {

        Page<Habito> habitosPage =
                service.obtenerHabitos(usuario.getIdUsuario(), pagina);

        String analisis =
                iaService.analizarHabitos(
                        service.obtenerTodosHabitos(
                                usuario.getIdUsuario()
                        )
                );

        model.addAttribute("habitos", habitosPage.getContent());

        model.addAttribute("analisisHabitos", analisis);

        model.addAttribute("paginaActual", pagina);

        model.addAttribute("totalPaginas",
                habitosPage.getTotalPages());

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

            Page<Habito> habitosPage =
                    service.obtenerHabitos(usuario.getIdUsuario(), 0);

            model.addAttribute("habitos",
                    habitosPage.getContent());

            model.addAttribute("paginaActual", 0);

            model.addAttribute("totalPaginas",
                    habitosPage.getTotalPages());

            return "habitos/lista";
        }

        habito.setUsuario(usuario);

        try {

            service.guardarHabito(habito);

        } catch (RuntimeException e) {

            Page<Habito> habitosPage =
                    service.obtenerHabitos(usuario.getIdUsuario(), 0);

            model.addAttribute("habitos",
                    habitosPage.getContent());

            model.addAttribute("paginaActual", 0);

            model.addAttribute("totalPaginas",
                    habitosPage.getTotalPages());

            model.addAttribute("error",
                    e.getMessage());

            return "habitos/lista";
        }

        return "redirect:/habitos";
    }

    // ==================================================
    // EDITAR
    // ==================================================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @CurrentUser Usuario usuario,
                         Model model) {

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

        service.validarHabito(id, usuario.getIdUsuario());

        service.eliminarHabito(id);

        return "redirect:/habitos";
    }
}