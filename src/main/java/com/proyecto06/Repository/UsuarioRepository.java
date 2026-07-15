package com.proyecto06.Repository;

import com.proyecto06.Modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Spring crea la consulta automáticamente por el nombre del método
    Optional<Usuario> findByCorreoAndPassword(String correo, String password);
}