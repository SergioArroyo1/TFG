package com.example.TFG.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================================================
    // 404
    // ==================================================
    @ExceptionHandler(EntityNotFoundException.class)
    public String notFound(EntityNotFoundException e,
                           Model model) {

        model.addAttribute("error",
                "Recurso no encontrado");

        return "error/404";
    }

    // ==================================================
    // 403
    // ==================================================
    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(AccessDeniedException e,
                               Model model) {

        model.addAttribute("error",
                "Acceso denegado");

        return "error/403";
    }

    // ==================================================
    // ERROR GENERAL
    // ==================================================
    @ExceptionHandler(Exception.class)
    public String genericError(Exception e,
                               Model model) {

        log.error("Error no controlado", e);

        model.addAttribute("error",
                "Ha ocurrido un error inesperado");

        return "error/500";
    }
}