package com.proyecto06.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto06.Modelo.Ubicacion;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    
}
