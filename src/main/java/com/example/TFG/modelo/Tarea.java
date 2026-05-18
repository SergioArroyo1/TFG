package com.example.TFG.modelo;

import com.example.TFG.modelo.enums.EstadoTarea;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarea;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String titulo;

    @Column(length = 500)
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion = "";

    @Column(nullable = false)
    @NotNull(message = "La fecha límite es obligatoria")
    private LocalDate fechaLimite;

    @Column(nullable = false)
    @Min(value = 0, message = "La prioridad mínima es 0")
    @Max(value = 5, message = "La prioridad máxima es 5")
    private Integer prioridad = 0;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado es obligatorio")
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}