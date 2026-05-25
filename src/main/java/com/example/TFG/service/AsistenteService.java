package com.example.TFG.service;

import com.example.TFG.modelo.*;
import com.example.TFG.modelo.enums.TipoTransaccion;
import com.example.TFG.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsistenteService {

    // ============================
    // REPOSITORIOS PRINCIPALES
    // ============================
    private final TareaRepository tareaRepo;
    private final HabitoRepository habitoRepo;
    private final EventoRepository eventoRepo;
    private final HabitoHistorialRepository historialRepo;

    // FINANZAS
    private final CategoriaRepository categoriaRepo;
    private final TransaccionRepository transaccionRepo;

    // Inyección de dependencias
    public AsistenteService(TareaRepository tareaRepo,
                            HabitoRepository habitoRepo,
                            EventoRepository eventoRepo,
                            HabitoHistorialRepository historialRepo,
                            CategoriaRepository categoriaRepo,
                            TransaccionRepository transaccionRepo) {

        this.tareaRepo = tareaRepo;
        this.habitoRepo = habitoRepo;
        this.eventoRepo = eventoRepo;
        this.historialRepo = historialRepo;
        this.categoriaRepo = categoriaRepo;
        this.transaccionRepo = transaccionRepo;
    }

    // ==================================================
    // TAREAS
    // ==================================================
    public Page<Tarea> obtenerTareas(Long idUsuario, int pagina) {

        Pageable pageable = PageRequest.of(pagina, 5);

        return tareaRepo.findByUsuarioIdUsuario(idUsuario, pageable);
    }

    public void guardarTarea(Tarea t) {
        tareaRepo.save(t);
    }

    public Tarea buscarTarea(Long id) {
        return tareaRepo.findById(id).orElse(null);
    }

    public void eliminarTarea(Long id) {
        tareaRepo.deleteById(id);
    }

    public void validarTarea(Long idTarea, Long idUsuario) {

        Tarea t = buscarTarea(idTarea);

        if (t == null || !t.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("No tienes permiso para esta tarea");
        }
    }
    public List<Tarea> obtenerTodasTareas(Long idUsuario) {
        return tareaRepo.findByUsuario_IdUsuario(idUsuario);
    }

    // ==================================================
    // HÁBITOS
    // ==================================================
    public Page<Habito> obtenerHabitos(Long idUsuario, int pagina) {

        Pageable pageable = PageRequest.of(pagina, 5);

        return habitoRepo.findByUsuarioIdUsuario(idUsuario, pageable);
    }

    public void guardarHabito(Habito h) {
        habitoRepo.save(h);
    }

    public Habito buscarHabito(Long id) {
        return habitoRepo.findById(id).orElse(null);
    }

    @Transactional
    public void eliminarHabito(Long idHabito) {

        // quitar referencia en eventos
        List<Evento> eventos =
                eventoRepo.findByHabito_IdHabito(idHabito);

        for (Evento e : eventos) {
            e.setHabito(null);
        }

        eventoRepo.saveAll(eventos);

        // borrar hábito
        habitoRepo.deleteById(idHabito);
    }

    public void validarHabito(Long idHabito, Long idUsuario) {

        Habito h = buscarHabito(idHabito);

        if (h == null || !h.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("No tienes permiso para este hábito");
        }
    }

    public List<Habito> obtenerTodosHabitos(Long idUsuario) {
        return habitoRepo.findByUsuario_IdUsuario(idUsuario);
    }

    // ==================================================
    // EVENTOS
    // ==================================================
    public List<Evento> obtenerEventos(Long idUsuario) {
        return eventoRepo.findByUsuarioIdUsuario(idUsuario);
    }

    public void guardarEvento(Evento e) {
        eventoRepo.save(e);
    }

    public void eliminarEvento(Long id) {
        eventoRepo.deleteById(id);
    }

    public Evento buscarEvento(Long id) {
        return eventoRepo.findById(id).orElse(null);
    }

    public void validarEvento(Long idEvento, Long idUsuario) {

        Evento e = buscarEvento(idEvento);

        if (e == null || !e.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("No tienes permiso para este evento");
        }
    }

    // ==================================================
    // HÁBITO HISTORIAL
    // ==================================================

    // Marca un hábito como cumplido en una fecha concreta
    @Transactional
    public void marcarHabito(Long idHabito, LocalDate fecha) {

        // Busca el hábito o lanza error si no existe
        Habito h = habitoRepo.findById(idHabito)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        // Se crea el registro de cumplimiento del hábito
        HabitoHistorial historial = new HabitoHistorial();
        historial.setHabito(h);
        historial.setFecha(fecha);
        historial.setCumplimiento(true);

        historialRepo.save(historial);
    }

    // ============================
    // FINANZAS - TRANSACCIONES
    // ============================
    public Page<Transaccion> obtenerTransacciones(Long idUsuario,
                                                  int pagina) {

        Pageable pageable = PageRequest.of(pagina, 5);

        return transaccionRepo.findByUsuario_IdUsuario(idUsuario, pageable);
    }

    public void guardarTransaccion(Transaccion t) {
        transaccionRepo.save(t);
    }

    public void eliminarTransaccion(Long id) {
        transaccionRepo.deleteById(id);
    }

    public Transaccion buscarTransaccion(Long id) {
        return transaccionRepo.findById(id).orElse(null);
    }

    public void validarTransaccion(Long idTransaccion, Long idUsuario) {

        Transaccion t = buscarTransaccion(idTransaccion);

        if (t == null || !t.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("No tienes permiso para esta transacción");
        }
    }

    // ============================
    // CATEGORÍAS
    // ============================
    public List<Categoria> obtenerCategorias(Long idUsuario) {
        return categoriaRepo.findByUsuarioIdUsuario(idUsuario);
    }

    public void guardarCategoria(Categoria c) {
        categoriaRepo.save(c);
    }

    public Categoria buscarCategoria(Long id) {
        return categoriaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    public void eliminarCategoria(Long id) {
        categoriaRepo.deleteById(id);
    }

    public void validarCategoria(Long idCategoria, Long idUsuario) {

        Categoria c = buscarCategoria(idCategoria);

        if (c == null || !c.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new AccessDeniedException("No tienes permiso para esta categoría");
        }
    }

    // ==================================================
    // ESTADÍSTICAS FINANCIERAS
    // ==================================================

    // Calcula el total de ingresos del usuario
    public double totalIngresos(Long idUsuario) {

        return transaccionRepo
                .findByUsuario_IdUsuario(idUsuario, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                .mapToDouble(Transaccion::getCantidad)
                .sum();
    }

    // Calcula el total de gastos del usuario
    public double totalGastos(Long idUsuario) {

        return transaccionRepo
                .findByUsuario_IdUsuario(idUsuario, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(t -> t.getTipo() == TipoTransaccion.GASTO)
                .mapToDouble(Transaccion::getCantidad)
                .sum();
    }

    // Porcentaje de gasto por categoría (reparto del gasto total)
    public Map<String, Double> porcentajeGastoPorCategoria(Long idUsuario) {

        // Filtra solo transacciones de tipo GASTO
        List<Transaccion> gastos = transaccionRepo
                .findByUsuario_IdUsuario(idUsuario, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(t -> t.getTipo() == TipoTransaccion.GASTO)
                .toList();

        // Suma total de gastos
        double total = gastos.stream()
                .mapToDouble(Transaccion::getCantidad)
                .sum();

        // Si no hay gastos, devuelve mapa vacío
        if (total == 0) return new HashMap<>();

        Map<String, Double> resultado = new HashMap<>();

        // Agrupa los gastos por categoría
        for (Transaccion t : gastos) {

            String categoria = (t.getCategoria() != null)
                    ? t.getCategoria().getNombre()
                    : "Sin categoría";

            resultado.put(
                    categoria,
                    resultado.getOrDefault(categoria, 0.0)
                            + t.getCantidad()
            );
        }

        // Convierte los valores a porcentaje respecto al total
        for (String key : resultado.keySet()) {
            resultado.put(key, (resultado.get(key) / total) * 100);
        }

        return resultado;
    }

    // Gasto total agrupado por ID de categoría
    public Map<Long, Double> gastoPorCategoria(Long idUsuario) {

        List<Transaccion> transacciones =
                transaccionRepo
                        .findByUsuario_IdUsuario(idUsuario, Pageable.unpaged())
                        .getContent();

        Map<Long, Double> resultado = new HashMap<>();

        for (Transaccion t : transacciones) {

            if (t.getCategoria() == null) {
                continue;
            }

            Long idCategoria =
                    t.getCategoria().getIdCategoria();

            String nombreCategoria =
                    t.getCategoria().getNombre();

            double valorActual =
                    resultado.getOrDefault(idCategoria, 0.0);

            // =========================
            // AHORRO → SUMA INGRESOS
            // =========================
            if ("Ahorro".equalsIgnoreCase(nombreCategoria)) {

                if (t.getTipo() == TipoTransaccion.INGRESO) {

                    resultado.put(
                            idCategoria,
                            valorActual + t.getCantidad()
                    );
                }
            }

            // =========================
            // RESTO → SUMA GASTOS
            // =========================
            else {

                if (t.getTipo() == TipoTransaccion.GASTO) {

                    resultado.put(
                            idCategoria,
                            valorActual + t.getCantidad()
                    );
                }
            }
        }

        return resultado;
    }
}