package com.example.TFG.controller;

import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService service;

    public AdminController(UsuarioService service) {
        this.service = service;
    }

    // ======================
    // PANEL
    // ======================
    @GetMapping
    public String panel(
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        var usuariosPage = service.obtenerTodos(pagina);

        model.addAttribute("usuarios", usuariosPage.getContent());

        model.addAttribute("paginaActual", pagina);

        model.addAttribute("totalPaginas",
                usuariosPage.getTotalPages());

        return "admin/panel";
    }

    // ======================
    // CREAR USUARIO
    // ======================
    @GetMapping("/crear")
    public String crearForm(Model model) {

        model.addAttribute("usuario", new Usuario());
        return "admin/crear";
    }

    @PostMapping("/crear")
    public String crear(Usuario u) {

        service.guardar(u); // cifra la contraseña
        return "redirect:/admin";
    }

    // ======================
    // EDITAR
    // ======================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        model.addAttribute("usuario", service.buscarPorId(id));
        return "admin/editar";
    }

    // ======================
    // ACTUALIZAR CON O SIN PASSWORD
    // ======================
    @PostMapping("/actualizar")
    public String actualizar(Usuario u,
                             @RequestParam(value = "nuevaContrasena", required = false) String nuevaContrasena) {

        // si el admin escribe nueva contraseña → se usa
        if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            service.actualizarConContrasena(u.getIdUsuario(), nuevaContrasena);
        } else {
            service.guardar(u); // se mantiene contraseña antigua
        }

        return "redirect:/admin";
    }

    // ======================
    // ELIMINAR
    // ======================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        service.eliminar(id);
        return "redirect:/admin";
    }
}