package com.example.TFG.service;

import com.example.TFG.modelo.Categoria;
import com.example.TFG.modelo.Evento;
import com.example.TFG.modelo.Tarea;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class IAService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final String URL ="https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    // =========================
    // LIMPIADOR GLOBAL IA
    // =========================
    private String limpiarRespuesta(String text) {

        if (text == null) return "";

        return text
                .replaceAll("\\*", "")   // quita asteriscos
                .replaceAll("#+", "")    // quita hashtags ### ##
                .replaceAll("\\s+", " ") // limpia espacios dobles
                .trim();
    }

    // =========================
    // FINANZAS
    // =========================
    public String analizarFinanzas(Map<Long, Double> gastoCategoria,
                                   List<Categoria> categorias) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estas finanzas personales. ")
                .append("Da alertas claras y consejos prácticos:\n\n");

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

        return limpiarRespuesta(llamarGemini(prompt.toString()));
    }

    // =========================
    // TAREAS
    // =========================
    public String analizarTareas(List<Tarea> tareas) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estas tareas y dime cuáles son más prioritarias.\n")
                .append("Devuelve un ranking claro y breve.\n\n");

        for (Tarea t : tareas) {
            prompt.append("- ")
                    .append(t.getTitulo())
                    .append(" | Fecha límite: ")
                    .append(t.getFechaLimite())
                    .append(" | Días restantes: ")
                    .append(ChronoUnit.DAYS.between(LocalDate.now(), t.getFechaLimite()))
                    .append("\n");
        }

        return limpiarRespuesta(llamarGemini(prompt.toString()));
    }

    // =========================
    // HÁBITOS
    // =========================
    public String analizarHabitos(List<com.example.TFG.modelo.Habito> habitos) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estos hábitos de un usuario y responde de forma clara:\n\n");

        for (var h : habitos) {

            prompt.append("- Hábito: ")
                    .append(h.getNombre())
                    .append(" | Frecuencia: ")
                    .append(h.getFrecuencia())
                    .append("\n");
        }

        prompt.append("\nDime:\n")
                .append("- cuáles son buenos\n")
                .append("- cuáles son inconsistentes\n")
                .append("- recomendaciones para mejorar\n");

        return limpiarRespuesta(llamarGemini(prompt.toString()));
    }

    // =========================
    // EVENTOS
    // =========================
    public String analizarEventos(List<Evento> eventos) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Analiza estos eventos personales y dame:\n");
        prompt.append("- Nivel de productividad\n");
        prompt.append("- Eventos completados vs pendientes\n");
        prompt.append("- Recomendaciones claras\n\n");

        for (Evento e : eventos) {
            prompt.append("- ")
                    .append(e.getTitulo())
                    .append(" | Fecha: ").append(e.getFecha())
                    .append(" | Completado: ").append(e.getCompletado())
                    .append("\n");
        }

        return limpiarRespuesta(llamarGemini(prompt.toString()));
    }

    // =========================
    // GEMINI CORE
    // =========================
    private String llamarGemini(String prompt) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textPart = Map.of("text", prompt);

            Map<String, Object> content = Map.of(
                    "parts", List.of(textPart)
            );

            Map<String, Object> body = Map.of(
                    "contents", List.of(content)
            );

            String urlFinal = URL + "?key=" + apiKey;

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(urlFinal, request, Map.class);

            Map responseBody = response.getBody();

            if (responseBody == null) {
                return "Sin respuesta de Gemini";
            }

            List candidates = (List) responseBody.get("candidates");
            Map first = (Map) candidates.get(0);
            Map contentResp = (Map) first.get("content");
            List parts = (List) contentResp.get("parts");
            Map part = (Map) parts.get(0);

            return part.get("text").toString();

        } catch (Exception e) {
            return "Error IA (Gemini): " + e.getMessage();
        }
    }
}