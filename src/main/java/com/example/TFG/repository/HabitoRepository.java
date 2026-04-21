package com.example.TFG.repository;


import com.example.TFG.modelo.Habito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoRepository extends JpaRepository<Habito, Long> {
    List<Habito> findByUsuarioIdUsuario(Long idUsuario);
}