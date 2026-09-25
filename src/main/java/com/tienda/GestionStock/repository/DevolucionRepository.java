package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    // En el futuro, aquí podrás crear métodos como:
    // List<Devolucion> findByFechaDevolucionBetween(...) para sacar informes mensuales

    // Consulta por la propiedad "fechaDevolucion"
    List<Devolucion> findByFechaDevolucionBetween(LocalDateTime inicio, LocalDateTime fin);
}
