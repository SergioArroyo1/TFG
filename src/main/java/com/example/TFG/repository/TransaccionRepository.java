package com.example.TFG.repository;

import com.example.TFG.modelo.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    Page<Transaccion> findByUsuario_IdUsuario(Long idUsuario, Pageable pageable);
    Page<Transaccion> findByUsuarioIdUsuarioAndTituloContainingIgnoreCase(Long idUsuario, String titulo, Pageable pageable);
    long countByUsuario_IdUsuario(Long idUsuario);

}