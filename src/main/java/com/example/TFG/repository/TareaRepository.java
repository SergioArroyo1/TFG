package com.example.TFG.repository;


import com.example.TFG.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByUsuarioIdUsuario(Long idUsuario);
}