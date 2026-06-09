package com.proyecto06.Repository;

import com.proyecto06.Modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByFechaVencimientoBefore(LocalDate fecha);
    
}