package com.proyecto06.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto06.Modelo.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}