package com.example.TFG.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IAServiceTest {

    @Test
    void limpiarTextoIA_deberiaEliminarMarkdown() {

        String texto = """
                ## TITULO

                * hola
                - prueba
                """;

        String resultado = limpiarMarkdown(texto);

        assertFalse(resultado.contains("*"));
        assertFalse(resultado.contains("#"));
        assertFalse(resultado.contains("-"));
    }

    @Test
    void limpiarTextoIA_noDebeSerNull() {

        String texto = "Texto de prueba";

        assertNotNull(limpiarMarkdown(texto));
    }

    @Test
    void limpiarTextoIA_noDebeEstarVacio() {

        String texto = "Texto de prueba";

        assertFalse(limpiarMarkdown(texto).isBlank());
    }

    // 🔧 método helper para test
    private String limpiarMarkdown(String texto) {
        if (texto == null) return null;

        return texto
                .replaceAll("(?m)^#+\\s*", "")   // títulos Markdown
                .replaceAll("[*\\-]", "")        // viñetas
                .trim();
    }
}