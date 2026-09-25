package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.Marca;
import com.tienda.GestionStock.model.Talla;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    Optional<Marca> findById(Integer numero);

    List<Marca> findAllByOrderByNombreAsc();
}
