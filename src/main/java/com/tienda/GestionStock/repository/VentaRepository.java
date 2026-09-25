package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // De momento, con los métodos heredados de JpaRepository (save, findAll, findById) tenemos suficiente.
    // Consulta por la propiedad "fecha"
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}