package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.dto.CajaDTO;
import com.tienda.GestionStock.service.StockViewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class StockRestController {

    private final StockViewService stockViewService;

    public StockRestController(StockViewService stockViewService) {
        this.stockViewService = stockViewService;
    }

    @GetMapping("/cajas")
    public ResponseEntity<Page<CajaDTO>> listarCajasPaginadas(
            @RequestParam(defaultValue = "DISPONIBLE") String estado,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CajaDTO> resultado = stockViewService.obtenerCajasPaginadas(estado, marcaId, pageable);

        return ResponseEntity.ok(resultado);
    }
}