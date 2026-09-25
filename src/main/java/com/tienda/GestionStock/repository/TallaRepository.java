package com.tienda.GestionStock.repository;


import com.tienda.GestionStock.model.Talla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TallaRepository extends JpaRepository<Talla, Integer> {

    Optional<Talla> findByNumero(Integer numero);


}
