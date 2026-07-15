package com.proyecto06.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto06.Modelo.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    
}
