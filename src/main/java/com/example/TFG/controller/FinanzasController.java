package com.example.TFG.controller;

import com.example.TFG.modelo.*;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/finanzas")
public class FinanzasController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;

    private final IAService iaService;

    // Inyección de servicios
    public FinanzasController(AsistenteService service, UsuarioService usuarioService, IAService iaService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.iaService = iaService;
    }

    // ==================================================
    // VISTA PRINCIPAL DE FINANZAS
    // ==================================================
    @GetMapping
    public String finanzas(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        model.addAttribute("transacciones",
                service.obtenerTransacciones(u.getIdUsuario()));

        model.addAttribute("categorias",
                service.obtenerCategorias(u.getIdUsuario()));

        model.addAttribute("totalIngresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("totalGastos",
                service.totalGastos(u.getIdUsuario()));

        model.addAttribute("ingresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("gastos",
                service.totalGastos(u.getIdUsuario()));

        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(u.getIdUsuario()));

        model.addAttribute("gastoCategoria",
                service.gastoPorCategoria(u.getIdUsuario()));

        return "finanzas/finanzas";
    }

    // ==================================================
    // 🧠 ANALIZAR CON IA (NUEVO)
    // ==================================================
    @GetMapping("/analizar")
    public String analizarIA(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        List<Categoria> categorias =
                service.obtenerCategorias(u.getIdUsuario());

        Map<Long, Double> gastoCategoria =
                service.gastoPorCategoria(u.getIdUsuario());

        // llamada a IA
        String respuestaIA = iaService.analizarFinanzas(gastoCategoria, categorias);

        // 🔹 reutilizamos TODO lo que ya usas
        model.addAttribute("transacciones",
                service.obtenerTransacciones(u.getIdUsuario()));

        model.addAttribute("categorias", categorias);

        model.addAttribute("totalIngresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("totalGastos",
                service.totalGastos(u.getIdUsuario()));

        model.addAttribute("ingresos",
                service.totalIngresos(u.getIdUsuario()));

        model.addAttribute("gastos",
                service.totalGastos(u.getIdUsuario()));

        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(u.getIdUsuario()));

        model.addAttribute("gastoCategoria", gastoCategoria);

        // resultado IA
        model.addAttribute("iaFinanzas", respuestaIA);

        return "finanzas/finanzas";
    }

    // ==================================================
    // GUARDAR TRANSACCIÓN
    // ==================================================
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Transaccion t,
                          Authentication auth) {

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

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        Categoria existente = service.buscarCategoria(c.getIdCategoria());

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