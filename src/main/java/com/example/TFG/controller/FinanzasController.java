package com.example.TFG.controller;

import com.example.TFG.config.CurrentUser;
import com.example.TFG.modelo.Categoria;
import com.example.TFG.modelo.Transaccion;
import com.example.TFG.modelo.Usuario;
import com.example.TFG.service.AsistenteService;
import com.example.TFG.service.IAService;
import jakarta.validation.Valid;
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
    private final IAService iaService;

    public FinanzasController(AsistenteService service,
                              IAService iaService) {
        this.service = service;
        this.iaService = iaService;
    }

    // ==================================================
    // VISTA PRINCIPAL
    // ==================================================
    @GetMapping
    public String finanzas(@CurrentUser Usuario usuario,
                           @RequestParam(defaultValue = "0") int pagina,
                           Model model) {

        Long usuarioId = usuario.getIdUsuario();

        var transaccionesPage =
                service.obtenerTransacciones(usuarioId, pagina);

        model.addAttribute("transacciones",
                transaccionesPage.getContent());

        model.addAttribute("paginaActual", pagina);

        model.addAttribute("totalPaginas",
                transaccionesPage.getTotalPages());

        model.addAttribute("categorias",
                service.obtenerCategorias(usuarioId));

        model.addAttribute("totalIngresos",
                service.totalIngresos(usuarioId));

        model.addAttribute("totalGastos",
                service.totalGastos(usuarioId));

        model.addAttribute("ingresos",
                service.totalIngresos(usuarioId));

        model.addAttribute("gastos",
                service.totalGastos(usuarioId));

        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(usuarioId));

        model.addAttribute("gastoCategoria",
                service.gastoPorCategoria(usuarioId));

        return "finanzas/finanzas";
    }

    // ==================================================
    // IA ANALISIS
    // ==================================================
    @GetMapping("/analizar")
    public String analizarIA(@CurrentUser Usuario usuario,
                             @RequestParam(defaultValue = "0") int pagina,
                             Model model) {

        Long usuarioId = usuario.getIdUsuario();

        List<Categoria> categorias =
                service.obtenerCategorias(usuarioId);

        Map<Long, Double> gastoCategoria =
                service.gastoPorCategoria(usuarioId);

        String respuestaIA =
                iaService.analizarFinanzas(gastoCategoria, categorias);

        var transaccionesPage =
                service.obtenerTransacciones(usuarioId, pagina);

        model.addAttribute("transacciones",
                transaccionesPage.getContent());

        model.addAttribute("paginaActual", pagina);

        model.addAttribute("totalPaginas",
                transaccionesPage.getTotalPages());

        model.addAttribute("categorias", categorias);

        model.addAttribute("totalIngresos",
                service.totalIngresos(usuarioId));

        model.addAttribute("totalGastos",
                service.totalGastos(usuarioId));

        model.addAttribute("ingresos",
                service.totalIngresos(usuarioId));

        model.addAttribute("gastos",
                service.totalGastos(usuarioId));

        model.addAttribute("porcentajeCategorias",
                service.porcentajeGastoPorCategoria(usuarioId));

        model.addAttribute("gastoCategoria", gastoCategoria);

        model.addAttribute("iaFinanzas", respuestaIA);

        return "finanzas/finanzas";
    }

    // ==================================================
    // GUARDAR TRANSACCION
    // ==================================================
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Transaccion transaccion,
                          BindingResult br,
                          @CurrentUser Usuario usuario,
                          Model model) {

        Long usuarioId = usuario.getIdUsuario();

        if (br.hasErrors()) {

            var transaccionesPage =
                    service.obtenerTransacciones(usuarioId, 0);

            model.addAttribute("transacciones",
                    transaccionesPage.getContent());

            model.addAttribute("paginaActual", 0);

            model.addAttribute("totalPaginas",
                    transaccionesPage.getTotalPages());

            model.addAttribute("categorias",
                    service.obtenerCategorias(usuarioId));

            return "finanzas/finanzas";
        }

        transaccion.setUsuario(usuario);

        service.guardarTransaccion(transaccion);

        return "redirect:/finanzas";
    }

    // ==================================================
    // CREAR CATEGORIA
    // ==================================================
    @PostMapping("/categoria")
    public String categoria(@Valid @ModelAttribute Categoria categoria,
                            BindingResult br,
                            @CurrentUser Usuario usuario,
                            Model model) {

        Long usuarioId = usuario.getIdUsuario();

        if (br.hasErrors()) {

            var transaccionesPage =
                    service.obtenerTransacciones(usuarioId, 0);

            model.addAttribute("transacciones",
                    transaccionesPage.getContent());

            model.addAttribute("paginaActual", 0);

            model.addAttribute("totalPaginas",
                    transaccionesPage.getTotalPages());

            model.addAttribute("categorias",
                    service.obtenerCategorias(usuarioId));

            return "finanzas/finanzas";
        }

        categoria.setUsuario(usuario);

        service.guardarCategoria(categoria);

        return "redirect:/finanzas";
    }

    // ==================================================
    // EDITAR CATEGORIA
    // ==================================================
    @PostMapping("/categoria/editar")
    public String editarCategoria(
            @Valid @ModelAttribute Categoria categoria,
            BindingResult br,
            @CurrentUser Usuario usuario,
            Model model) {

        Long usuarioId = usuario.getIdUsuario();

        service.validarCategoria(
                categoria.getIdCategoria(),
                usuarioId
        );

        if (br.hasErrors()) {

            var transaccionesPage =
                    service.obtenerTransacciones(usuarioId, 0);

            model.addAttribute("transacciones",
                    transaccionesPage.getContent());

            model.addAttribute("paginaActual", 0);

            model.addAttribute("totalPaginas",
                    transaccionesPage.getTotalPages());

            model.addAttribute("categorias",
                    service.obtenerCategorias(usuarioId));

            return "finanzas/finanzas";
        }

        Categoria existente =
                service.buscarCategoria(categoria.getIdCategoria());

        existente.setNombre(categoria.getNombre());
        existente.setLimiteMensual(categoria.getLimiteMensual());
        existente.setUsuario(usuario);

        service.guardarCategoria(existente);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR CATEGORIA
    // ==================================================
    @GetMapping("/categoria/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id,
                                    @CurrentUser Usuario usuario) {

        service.validarCategoria(id, usuario.getIdUsuario());

        service.eliminarCategoria(id);

        return "redirect:/finanzas";
    }

    // ==================================================
    // ELIMINAR TRANSACCION
    // ==================================================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           @CurrentUser Usuario usuario) {

        service.validarTransaccion(id, usuario.getIdUsuario());

        service.eliminarTransaccion(id);

        return "redirect:/finanzas";
    }
}