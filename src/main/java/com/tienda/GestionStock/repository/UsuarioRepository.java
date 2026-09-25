package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);
    // Spring Data JPA creará la consulta automáticamente analizando el nombre del método
    Optional<Usuario> findByUsername(String username);

}
