package com.example.TFG.repository;

import com.example.TFG.modelo.Tarea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    Page<Tarea> findByUsuarioIdUsuario(Long idUsuario, Pageable pageable);

}