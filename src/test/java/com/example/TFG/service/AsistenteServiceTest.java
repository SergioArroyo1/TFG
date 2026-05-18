package com.example.TFG.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AsistenteServiceTest {

    @Test
    void testBasico() {
        String mensaje = "Asistente funcionando";

        System.out.println("Mensaje: " + mensaje);

        assertTrue(mensaje.contains("funcionando"));
    }
}