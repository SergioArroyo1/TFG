package com.example.TFG.controller;

import com.example.TFG.modelo.*;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import com.example.TFG.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/finanzas")
public class FinanzasController {

    private final AsistenteService service;
    private final UsuarioService usuarioService;
    private final IAService iaService;

    public FinanzasController(AsistenteService service,
                              UsuarioService usuarioService,
                              IAService iaService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.iaService = iaService;
    }

    // ==================================================
    // VISTA PRINCIPAL
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
    // IA ANALISIS
    // ==================================================
    @GetMapping("/analizar")
    public String analizarIA(Authentication auth, Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        List<Categoria> categorias =
                service.obtenerCategorias(u.getIdUsuario());

        Map<Long, Double> gastoCategoria =
                service.gastoPorCategoria(u.getIdUsuario());

        String respuestaIA =
                iaService.analizarFinanzas(gastoCategoria, categorias);

        model.addAttribute("transacciones",
                service.obtenerTransacciones(u.getIdUsuario()));

        model.addAttribute("categorias", categorias);
        model.addAttribute("totalIngresos", service.totalIngresos(u.getIdUsuario()));
        model.addAttribute("totalGastos", service.totalGastos(u.getIdUsuario()));
        model.addAttribute("ingresos", service.totalIngresos(u.getIdUsuario()));
        model.addAttribute("gastos", service.totalGastos(u.getIdUsuario()));
        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(u.getIdUsuario()));
        model.addAttribute("gastoCategoria", gastoCategoria);

        model.addAttribute("iaFinanzas", respuestaIA);

        return "finanzas/finanzas";
    }

    // ==================================================
    // GUARDAR TRANSACCION
    // ==================================================
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Transaccion t,
                          BindingResult br,
                          Authentication auth,
                          Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        if (br.hasErrors()) {
            model.addAttribute("transacciones",
                    service.obtenerTransacciones(u.getIdUsuario()));
            model.addAttribute("categorias",
                    service.obtenerCategorias(u.getIdUsuario()));
            return "finanzas/finanzas";
        }

        t.setUsuario(u);
        service.guardarTransaccion(t);

        return "redirect:/finanzas";
    }

    // ==================================================
    // CREAR CATEGORIA
    // ==================================================
    @PostMapping("/categoria")
    public String categoria(@Valid @ModelAttribute Categoria c,
                            BindingResult br,
                            Authentication auth,
                            Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        if (br.hasErrors()) {
            model.addAttribute("transacciones",
                    service.obtenerTransacciones(u.getIdUsuario()));
            model.addAttribute("categorias",
                    service.obtenerCategorias(u.getIdUsuario()));
            return "finanzas/finanzas";
        }

        c.setUsuario(u);
        service.guardarCategoria(c);

        return "redirect:/finanzas";
    }

    // ==================================================
    // EDITAR CATEGORIA (SEGURIDAD CENTRALIZADA)
    // ==================================================
    @PostMapping("/categoria/editar")
    public String editarCategoria(@Valid @ModelAttribute Categoria c,
                                  BindingResult br,
                                  Authentication auth,
                                  Model model) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA
        service.validarCategoria(c.getIdCategoria(), u.getIdUsuario());

        if (br.hasErrors()) {
            model.addAttribute("transacciones",
                    service.obtenerTransacciones(u.getIdUsuario()));
            model.addAttribute("categorias",
                    service.obtenerCategorias(u.getIdUsuario()));
            return "finanzas/finanzas";
        }

        Categoria existente = service.buscarCategoria(c.getIdCategoria());

        existente.setNombre(c.getNombre());
        existente.setLimiteMensual(c.getLimiteMensual());
        existente.setUsuario(u);

        service.guardarCategoria(existente);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR CATEGORIA (SEGURIDAD CENTRALIZADA)
    // ==================================================
    @GetMapping("/categoria/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id,
                                    Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA
        service.validarCategoria(id, u.getIdUsuario());

        service.eliminarCategoria(id);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR TRANSACCION (SEGURIDAD CENTRALIZADA)
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           Authentication auth) {

        Usuario u = usuarioService.buscarPorEmail(auth.getName());

        // VALIDACIÓN CENTRALIZADA
        service.validarTransaccion(id, u.getIdUsuario());

        service.eliminarTransaccion(id);

        return "redirect:/finanzas";
    }
}