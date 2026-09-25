package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.Marca;
import com.tienda.GestionStock.model.Producto;
import com.tienda.GestionStock.service.ProductoApiService; // <-- Inyectamos el servicio
import com.tienda.GestionStock.service.ProductoViewService;
import com.tienda.GestionStock.service.StockViewService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final ProductoApiService productoApiService;
    private final StockViewService stockViewService;

    // El constructor ya no conoce repositorios, solo servicios
    public ProductoApiController(ProductoApiService productoApiService, StockViewService stockViewService) {
        this.productoApiService = productoApiService;
        this.stockViewService = stockViewService;
    }

    @GetMapping("/marcas")
    public List<Marca> obtenerMarcas(@RequestParam String temporada) {
        return productoApiService.obtenerMarcasPorTemporada(temporada);
    }

    @GetMapping("/modelos")
    public List<String> obtenerModelos(@RequestParam String temporada, @RequestParam Long marcaId) {
        return productoApiService.obtenerModelosPorTemporadaYMarca(temporada, marcaId);
    }

    @GetMapping("/colores")
    public List<Producto> obtenerColores(@RequestParam String temporada, @RequestParam Long marcaId, @RequestParam String modelo) {
        return productoApiService.obtenerColoresPorFiltro(temporada, marcaId, modelo);
    }

    // Dentro de tu ApiController (por ejemplo, ProductoApiController o uno nuevo de Stock)

    @GetMapping("/cajas-por-estado")
    public List<Map<String, Object>> obtenerCajasPorEstado(@RequestParam String estado) {
        List<CajaStock> cajas;

        if ("TODOS".equalsIgnoreCase(estado)) {
            cajas = stockViewService.verInventario(); // Por si quieres dar la opción de ver todo
        } else {
            //cajas = cajaStockRepository.findByEstadoIgnoreCaseOrderByIdDesc(estado);
            cajas = stockViewService.verCajasPorEstado(estado);
        }

        // Mapeamos los datos para enviarle a JavaScript solo lo que necesita la tabla
        return cajas.stream().map(caja -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", caja.getId());
            dto.put("qr", caja.getQrCodigoUnico());
            dto.put("marca", caja.getVariante().getProducto().getMarca().getNombre());
            dto.put("modelo", caja.getVariante().getProducto().getNombre());
            dto.put("color", caja.getVariante().getProducto().getColor());
            dto.put("talla", caja.getVariante().getTalla().getNumero());
            dto.put("ubicacion", caja.getUbicacionAlmacen());
            dto.put("estado", caja.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/cajas-por-marca")
    public List<Map<String, Object>> obtenerCajasPorMarca(@RequestParam("marca") String marca) {
        List<CajaStock> cajas;

        if ("TODAS".equalsIgnoreCase(marca)) {
            cajas = stockViewService.verInventario(); // Por si quieres dar la opción de ver todo
        } else {
            //cajas = cajaStockRepository.findByEstadoIgnoreCaseOrderByIdDesc(estado);
            cajas = stockViewService.verCajasPorMarca(marca);
        }

        // Mapeamos los datos para enviarle a JavaScript solo lo que necesita la tabla
        return cajas.stream().map(caja -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", caja.getId());
            dto.put("qr", caja.getQrCodigoUnico());
            dto.put("marca", caja.getVariante().getProducto().getMarca().getNombre());
            dto.put("modelo", caja.getVariante().getProducto().getNombre());
            dto.put("color", caja.getVariante().getProducto().getColor());
            dto.put("talla", caja.getVariante().getTalla().getNumero());
            dto.put("ubicacion", caja.getUbicacionAlmacen());
            dto.put("estado", caja.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }
}