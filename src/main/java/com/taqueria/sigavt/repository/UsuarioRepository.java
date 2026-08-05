package com.taqueria.sigavt.repository;

import com.taqueria.sigavt.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // SELECT * FROM Usuario WHERE username = ?
    Optional<Usuario> findByUsername(String username);
}