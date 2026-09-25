package com.tienda.GestionStock.service;

import com.tienda.GestionStock.model.Marca;
import com.tienda.GestionStock.model.Producto;
import com.tienda.GestionStock.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoApiService {

    private final ProductoRepository productoRepository;

    public ProductoApiService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Marca> obtenerMarcasPorTemporada(String temporada) {
        return productoRepository.findDistinctMarcasByTemporada(temporada);
    }

    public List<String> obtenerModelosPorTemporadaYMarca(String temporada, Long scarmaId) {
        return productoRepository.findDistinctNombresByTemporadaYMarca(temporada, scarmaId);
    }

    public List<Producto> obtenerColoresPorFiltro(String temporada, Long scarmaId, String modelo) {
        return productoRepository.findByTemporadaAndMarcaIdAndNombreOrderByColorAsc(temporada, scarmaId, modelo);
    }


}