package com.tienda.GestionStock.repository;

import com.tienda.GestionStock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/*
@Repository es una anotación de Spring que indica que una clase pertenece a la capa de acceso a
datos (DAO - Data Access Object).

Se utiliza para:

Marcar la clase como un bean de Spring: Al ser una especialización de @Component, permite que
Spring la detecte automáticamente y la gestione.

Definir la capa de persistencia: Indica claramente que la clase se encarga de operaciones CRUD
(crear, leer, actualizar, eliminar) con la base de datos.

Traducción automática de excepciones: Su función más importante es convertir excepciones específicas
de la base de datos (como SQLException) en excepciones no verificadas de Spring (DataAccessException),
haciendo el manejo de errores más consistente y portable.
 */


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Spring generará un "SELECT * FROM productos WHERE nombre ILIKE %...%" (ignora mayúsculas/minúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Para filtrar el catálogo por una marca concreta
    List<Producto> findByMarcaIgnoreCase(String marca);

    Producto findFirstByOrderByIdDesc();

    // 1. Extrae todas las temporadas únicas del catálogo
    @Query("SELECT DISTINCT p.temporada FROM Producto p WHERE p.temporada IS NOT NULL ORDER BY p.temporada DESC")
    List<String> findDistinctTemporadas();

    // 2. Extrae los objetos Marca únicos asociados a los productos de una temporada concreta
    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.temporada = :temporada ORDER BY p.marca.nombre ASC")
    List<com.tienda.GestionStock.model.Marca> findDistinctMarcasByTemporada(@Param("temporada") String temporada);

    // 3. Extrae los nombres de modelos únicos de una temporada y marca específicas
    @Query("SELECT DISTINCT p.nombre FROM Producto p WHERE p.temporada = :temporada AND p.marca.id = :marcaId ORDER BY p.nombre ASC")
    List<String> findDistinctNombresByTemporadaYMarca(@Param("temporada") String temporada, @Param("marcaId") Long marcaId);

    // 4. Extrae los productos completos para obtener los colores e IDs finales
    @Query("SELECT p FROM Producto p WHERE p.temporada = :temporada AND p.marca.id = :marcaId AND p.nombre = :modelo ORDER BY p.color ASC")
    List<Producto> findByTemporadaAndMarcaIdAndNombreOrderByColorAsc(
            @Param("temporada") String temporada,
            @Param("marcaId") Long marcaId,
            @Param("modelo") String modelo);
}
