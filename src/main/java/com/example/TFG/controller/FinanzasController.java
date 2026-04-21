package com.example.TFG.controller;

import com.example.TFG.modelo.*;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/finanzas")
public class FinanzasController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;

    // Inyección de servicios: lógica de negocio y usuario autenticado
    public FinanzasController(AsistenteService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // ==================================================
    // VISTA PRINCIPAL DE FINANZAS
    // ==================================================
    @GetMapping
    public String finanzas(Authentication auth, Model model) {

        // Usuario actual obtenido desde sesión
        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // ============================
        // DATOS PRINCIPALES
        // ============================
        model.addAttribute("transacciones",
                service.obtenerTransacciones(u.getIdUsuario()));

        model.addAttribute("categorias",
                service.obtenerCategorias(u.getIdUsuario()));

        // Totales de ingresos y gastos (se usan en dashboard)
        model.addAttribute("totalIngresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("totalGastos",
                service.totalGastos(u.getIdUsuario()));

        // usados distintas partes de la vista
        model.addAttribute("ingresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("gastos",
                service.totalGastos(u.getIdUsuario()));

        // ============================
        // ESTADÍSTICAS
        // ============================
        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(u.getIdUsuario()));

        model.addAttribute("gastoCategoria",
                service.gastoPorCategoria(u.getIdUsuario()));

        return "finanzas/finanzas";
    }

    // ==================================================
    // GUARDAR TRANSACCIÓN
    // ==================================================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Transaccion t,
                          Authentication auth) {

        // Se asigna el usuario autenticado a la transacción
        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        t.setUsuario(u);

        service.guardarTransaccion(t);

        return "redirect:/finanzas";
    }

    // ==================================================
    // CREAR CATEGORÍA
    // ==================================================
    @PostMapping("/categoria")
    public String categoria(@ModelAttribute Categoria c,
                            Authentication auth) {

        // Asignación de usuario propietario de la categoría
        Usuario u = usuarioService.buscarPorEmail(auth.getName());
        c.setUsuario(u);

        service.guardarCategoria(c);

        return "redirect:/finanzas";
    }

    // ==================================================
    // EDITAR CATEGORÍA
    // ==================================================
    @PostMapping("/categoria/editar")
    public String editarCategoria(@ModelAttribute Categoria c, Authentication auth) {

        // Usuario actual
        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // Se recupera la categoría original desde BD
        Categoria existente = service.buscarCategoria(c.getIdCategoria());

        // Se actualizan solo los campos modificables
        existente.setNombre(c.getNombre());
        existente.setLimiteMensual(c.getLimiteMensual());
        existente.setUsuario(u);

        service.guardarCategoria(existente);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR CATEGORÍA
    // ==================================================
    @GetMapping("/categoria/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {

        service.eliminarCategoria(id);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR TRANSACCIÓN
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        service.eliminarTransaccion(id);
        return "redirect:/finanzas";
    }
}