package com.example.TFG.service;

import com.example.TFG.modelo.*;
import com.example.TFG.repository.*;
import org.springframework.stereotype.Service;

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
    public List<Tarea> obtenerTareas(Long idUsuario) {
        return tareaRepo.findByUsuarioIdUsuario(idUsuario);
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

    // ==================================================
    // HÁBITOS
    // ==================================================
    public List<Habito> obtenerHabitos(Long idUsuario) {
        return habitoRepo.findByUsuarioIdUsuario(idUsuario);
    }

    public void guardarHabito(Habito h) {
        habitoRepo.save(h);
    }

    public Habito buscarHabito(Long id) {
        return habitoRepo.findById(id).orElse(null);
    }

    public void eliminarHabito(Long id) {
        habitoRepo.deleteById(id);
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

    // ==================================================
    // HÁBITO HISTORIAL
    // ==================================================
    // Marca un hábito como cumplido en una fecha concreta
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
    public List<Transaccion> obtenerTransacciones(Long idUsuario) {
        return transaccionRepo.findByUsuario_IdUsuario(idUsuario);
    }

    public void guardarTransaccion(Transaccion t) {
        transaccionRepo.save(t);
    }

    public void eliminarTransaccion(Long id) {
        transaccionRepo.deleteById(id);
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

    // ==================================================
    // ESTADÍSTICAS FINANCIERAS
    // ==================================================

    // Calcula el total de ingresos del usuario
    public double totalIngresos(Long idUsuario) {
        return transaccionRepo.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .filter(t -> "INGRESO".equalsIgnoreCase(t.getTipo()))
                .mapToDouble(Transaccion::getCantidad)
                .sum();
    }

    // Calcula el total de gastos del usuario
    public double totalGastos(Long idUsuario) {
        return transaccionRepo.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .filter(t -> "GASTO".equalsIgnoreCase(t.getTipo()))
                .mapToDouble(Transaccion::getCantidad)
                .sum();
    }

    // Porcentaje de gasto por categoría (reparto del gasto total)
    public Map<String, Double> porcentajeGastoPorCategoria(Long idUsuario) {

        // Filtra solo transacciones de tipo GASTO
        List<Transaccion> gastos = transaccionRepo.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .filter(t -> "GASTO".equalsIgnoreCase(t.getTipo()))
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
                    resultado.getOrDefault(categoria, 0.0) + t.getCantidad()
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
                transaccionRepo.findByUsuario_IdUsuario(idUsuario);

        return transacciones.stream()
                .filter(t -> "GASTO".equalsIgnoreCase(t.getTipo()))
                .filter(t -> t.getCategoria() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        t -> t.getCategoria().getIdCategoria(),
                        java.util.stream.Collectors.summingDouble(Transaccion::getCantidad)
                ));
    }

}