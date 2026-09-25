package com.tienda.GestionStock.repository;


import com.tienda.GestionStock.model.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {

    // Para recuperar todas las tallas/variantes que pertenecen a un mismo producto
    List<VarianteProducto> findByProductoId(Long productoId);

    // Buscar una variante exacta mediante su código SKU
    Optional<VarianteProducto> findBySkuModelo(String skuModelo);
}