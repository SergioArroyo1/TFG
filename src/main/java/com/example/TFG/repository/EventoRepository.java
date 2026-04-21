package com.example.TFG.repository;

import com.example.TFG.modelo.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByUsuarioIdUsuario(Long idUsuario);
}