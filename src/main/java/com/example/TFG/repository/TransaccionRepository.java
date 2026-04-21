package com.example.TFG.repository;

import com.example.TFG.modelo.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuario_IdUsuario(Long idUsuario);
}