package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaApiController {

    private final VentaService ventaService;

    public VentaApiController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/buscar-qr")
    public ResponseEntity<?> buscarQrParaVenta(@RequestParam String qr) {
        Optional<CajaStock> oCaja = ventaService.buscarCajaParaVenta(qr.trim());

        if (oCaja.isEmpty()) {
            // Si no existe o ya está vendido, devolvemos un error 404 con un mensaje aclaratorio
            return ResponseEntity.status(404).body("El código QR no pertenece a ningún producto disponible.");
        }

        CajaStock caja = oCaja.get();

        // Mapeamos los datos limpios que necesita el JavaScript de la tabla
        Map<String, Object> data = new HashMap<>();
        data.clear();
        data.put("cajaId", caja.getId());
        data.put("qr", caja.getQrCodigoUnico());
        data.put("marca", caja.getVariante().getProducto().getMarca().getNombre());
        data.put("modelo", caja.getVariante().getProducto().getNombre());
        data.put("color", caja.getVariante().getProducto().getColor());
        data.put("talla", caja.getVariante().getTalla().getNumero());
        data.put("precio", caja.getVariante().getProducto().getPrecioBase());

        return ResponseEntity.ok(data);
    }
}