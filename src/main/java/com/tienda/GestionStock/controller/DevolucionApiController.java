package com.tienda.GestionStock.controller;


import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.service.DevolucionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionApiController {

    private final DevolucionService devolucionService;

    public DevolucionApiController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @GetMapping("/buscar-qr")
    public ResponseEntity<?> buscarQrParaDevolucion(@RequestParam String qr) {
        Optional<CajaStock> oCaja = devolucionService.buscarCajaParaDevolucion(qr.trim());

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
