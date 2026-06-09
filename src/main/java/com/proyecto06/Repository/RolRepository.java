package com.proyecto06.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto06.Modelo.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
