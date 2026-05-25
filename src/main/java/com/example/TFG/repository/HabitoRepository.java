package com.example.TFG.repository;

import com.example.TFG.modelo.Habito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitoRepository extends JpaRepository<Habito, Long> {

    Page<Habito> findByUsuarioIdUsuario(Long idUsuario, Pageable pageable);

}