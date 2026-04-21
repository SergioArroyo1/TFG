package com.example.TFG.service;

import com.example.TFG.modelo.Usuario;
import com.example.TFG.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // ======================
    //  REGISTRO
    // ======================
    public void registrar(Usuario u) {
        u.setRol("USER");
        u.setContrasena(encoder.encode(u.getContrasena()));
        repo.save(u);
    }

    // ======================
    // LISTAR TODOS
    // ======================
    public List<Usuario> obtenerTodos() {
        return repo.findAll();
    }

    // ======================
    // BUSCAR POR EMAIL
    // ======================
    public Usuario buscarPorEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    // ======================
    // BUSCAR POR ID
    // ======================
    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    // ======================
    // GUARDAR / ACTUALIZAR (ADMIN)
    // ======================
    public void guardar(Usuario u) {

        // ===== CREAR =====
        if (u.getIdUsuario() == null) {
            u.setContrasena(encoder.encode(u.getContrasena()));
        }

        // ===== ACTUALIZAR =====
        else {
            Usuario existente = repo.findById(u.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Si el admin introduce nueva contraseña → se actualiza
            if (u.getContrasena() != null && !u.getContrasena().isBlank()
                    && !encoder.matches(u.getContrasena(), existente.getContrasena())) {

                u.setContrasena(encoder.encode(u.getContrasena()));
            } else {
                // si no toca contraseña → mantener la anterior
                u.setContrasena(existente.getContrasena());
            }
        }

        repo.save(u);
    }

    // ======================
    // ACTUALIZAR LA NUEVA CONTRASEÑA (AL EDITAR EL USUARIO)
    // ======================

    public void actualizarConContrasena(Long id, String nuevaContrasena) {

        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        u.setContrasena(encoder.encode(nuevaContrasena));

        repo.save(u);
    }

    // ======================
    // ELIMINAR
    // ======================
    public void eliminar(Long id) {
        repo.deleteById(id);
    }


}