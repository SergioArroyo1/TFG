package com.example.TFG.repository;

import com.example.TFG.modelo.HabitoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoHistorialRepository extends JpaRepository<HabitoHistorial, Long> {

    List<HabitoHistorial> findByHabitoUsuarioIdUsuario(Long idUsuario);
}