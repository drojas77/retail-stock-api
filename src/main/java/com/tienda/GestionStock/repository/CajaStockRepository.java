package com.tienda.GestionStock.repository;


import com.tienda.GestionStock.model.CajaStock;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CajaStockRepository extends JpaRepository<CajaStock, Long> {


    // Buscar la caja física exacta escaneando su QR único
    Optional<CajaStock> findByQrCodigoUnico(String qrCodigoUnico);

    // Consultar cuántas cajas físicas tenemos en el almacén de una variante específica (Modelo + Talla) y que estén 'DISPONIBLE'
    List<CajaStock> findByVarianteIdAndEstadoIgnoreCase(Long varianteId, String estado);

    List<CajaStock> findByVarianteProductoId(Long productoId);

    // Dentro de CajaStockRepository.java

    // Filtra las cajas por estado y las ordena para que las últimas que han entrado (o vendido) salgan arriba
    List<CajaStock> findByEstadoIgnoreCaseOrderByIdDesc(String estado);

    List<CajaStock> findByVarianteProductoMarcaId(Long marca);


    @Query("SELECT c FROM CajaStock c " +
                "WHERE (:estado = 'TODOS' OR c.estado = :estado) " +
                "AND (:marcaId IS NULL OR c.variante.producto.marca.id = :marcaId)")
    Page<CajaStock> buscarCajasPaginadas(@Param("estado") String estado,
                                         @Param("marcaId") Long marcaId,
                                         Pageable pageable);

    boolean existsByQrCodigoUnico(String qrCode);
}