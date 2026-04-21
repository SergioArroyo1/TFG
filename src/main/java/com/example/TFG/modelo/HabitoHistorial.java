package com.example.TFG.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_habito", nullable = false)
    private Habito habito;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean cumplimiento = false;
}