package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.LineaDevolucion;
import com.tienda.GestionStock.model.LineaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaDevolucionRepository extends JpaRepository<LineaDevolucion, Long> {
    // Este repositorio nos servirá si en el futuro queremos hacer informes de líneas de artículos vendidos.
}