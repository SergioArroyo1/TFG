package com.example.TFG.service;

import com.example.TFG.modelo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class IAService {

    // LOGGER AÑADIDO
    private static final Logger log = LoggerFactory.getLogger(IAService.class);

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final WebClient webClient;

    private final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    public IAService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // ==================================================
    // FINANZAS
    // ==================================================
    public String analizarFinanzas(Map<Long, Double> gastoCategoria,
                                   List<Categoria> categorias) {

        log.info("IA Finanzas -> categorías: {}", categorias.size());

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estas finanzas personales. Da alertas claras y consejos prácticos:\n\n");

        for (Categoria c : categorias) {

            Double gastado = gastoCategoria.getOrDefault(c.getIdCategoria(), 0.0);

            prompt.append("- ")
                    .append(c.getNombre())
                    .append(": gastado ")
                    .append(gastado)
                    .append("€, límite ")
                    .append(c.getLimiteMensual())
                    .append("€\n");
        }

        return llamarGemini(prompt.toString());
    }

    // ==================================================
    // TAREAS
    // ==================================================
    public String analizarTareas(List<Tarea> tareas) {

        log.info("IA Tareas -> total: {}", tareas.size());

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estas tareas y priorízalas:\n\n");

        for (Tarea t : tareas) {

            prompt.append("- ")
                    .append(t.getTitulo())
                    .append(" | Fecha límite: ")
                    .append(t.getFechaLimite())
                    .append(" | Días restantes: ")
                    .append(
                            ChronoUnit.DAYS.between(LocalDate.now(), t.getFechaLimite())
                    )
                    .append("\n");
        }

        return llamarGemini(prompt.toString());
    }

    // ==================================================
    // HÁBITOS
    // ==================================================
    public String analizarHabitos(List<Habito> habitos) {

        log.info("IA Hábitos -> total: {}", habitos.size());

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estos hábitos:\n\n");

        for (Habito h : habitos) {

            prompt.append("- ")
                    .append(h.getNombre())
                    .append(" | Frecuencia: ")
                    .append(h.getFrecuencia())
                    .append("\n");
        }

        return llamarGemini(prompt.toString());
    }

    // ==================================================
    // EVENTOS
    // ==================================================
    public String analizarEventos(List<Evento> eventos) {

        log.info("IA Eventos -> total: {}", eventos.size());

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estos eventos personales:\n\n");

        for (Evento e : eventos) {

            prompt.append("- ")
                    .append(e.getTitulo())
                    .append(" | Fecha: ")
                    .append(e.getFecha())
                    .append(" | Completado: ")
                    .append(e.getCompletado())
                    .append("\n");
        }

        return llamarGemini(prompt.toString());
    }

    // ==================================================
    // GEMINI
    // ==================================================
    private String llamarGemini(String prompt) {

        log.info("Enviando prompt a Gemini ({} caracteres)", prompt.length());

        try {

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            var response = webClient.post()
                    .uri(URL + "?key=" + apiKey)
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .block();

            if (response == null ||
                    response.candidates == null ||
                    response.candidates.isEmpty()) {

                log.error("Respuesta Gemini vacía");
                return "No se pudo generar un análisis en este momento.";
            }

            GeminiCandidate candidate = response.candidates.get(0);

            if (candidate.content == null ||
                    candidate.content.parts == null ||
                    candidate.content.parts.isEmpty()) {

                log.error("Respuesta Gemini sin contenido válido");
                return "No se pudo generar un análisis en este momento.";
            }

            String texto = candidate.content.parts.get(0).text;

            log.info("Respuesta Gemini OK");

            return limpiarTextoIA(texto);

        } catch (Exception e) {

            log.error("Error llamando a Gemini", e);

            return "No se pudo generar el análisis en este momento.";
        }
    }

    // ==================================================
    // LIMPIEZA REAL
    // ==================================================
    private String limpiarTextoIA(String texto) {

        if (texto == null || texto.isBlank()) {
            return "No hay análisis disponible.";
        }

        return texto
                .replaceAll("\\*+", "")
                .replaceAll("#+", "")
                .replaceAll("(?m)^\\s*-\\s*", "")
                .replaceAll("(?m)^\\s*\\d+\\.\\s*", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    // ==================================================
    // DTOs
    // ==================================================
    public static class GeminiResponse {
        public List<GeminiCandidate> candidates;
    }

    public static class GeminiCandidate {
        public GeminiContent content;
    }

    public static class GeminiContent {
        public List<GeminiPart> parts;
    }

    public static class GeminiPart {
        public String text;
    }
}