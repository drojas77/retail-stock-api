package com.tienda.GestionStock.service;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.LineaVenta;
import com.tienda.GestionStock.model.Venta;
import com.tienda.GestionStock.repository.CajaStockRepository;
import com.tienda.GestionStock.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final CajaStockRepository cajaStockRepository;

    public VentaService(VentaRepository ventaRepository, CajaStockRepository cajaStockRepository) {
        this.ventaRepository = ventaRepository;
        this.cajaStockRepository = cajaStockRepository;
    }

    // 1. BUSCAR CAJA POR QR: Este método lo usará el JavaScript de la pantalla en caliente
    /*public Optional<CajaStock> buscarCajaParaVenta(String qrCodigo) {
        Optional<CajaStock> oCaja = cajaStockRepository.findByQrCodigoUnico(qrCodigo);

        // Solo la devolvemos si existe y además está "DISPONIBLE" en la tienda
        if (oCaja.isPresent() && "DISPONIBLE".equalsIgnoreCase(oCaja.get().getEstado())) {
            return oCaja;
        }
        return Optional.empty();
    }*/

    public Optional<CajaStock> buscarCajaParaVenta(String qrCodigo) {

        if (qrCodigo == null || qrCodigo.trim().isEmpty()) {
            return Optional.empty();
        }
        // Buscamos la coincidencia exacta del DNI de la caja
        Optional<CajaStock> oCaja = cajaStockRepository.findByQrCodigoUnico(qrCodigo.trim());

        CajaStock caja = oCaja.get();

        // Solo la damos por válida si existe y está en la estantería como DISPONIBLE
        if (oCaja.isPresent() && caja.DISPONIBLE.equalsIgnoreCase(oCaja.get().getEstado())) {
            return oCaja;
        }
        return Optional.empty();
    }

    // 2. PROCESAR LA VENTA EN BLOQUE (Transaccional)
    @Transactional
    public Venta registrarVenta(List<Long> cajaIds, String metodoPago) {
        if (cajaIds == null || cajaIds.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en el carrito para procesar la venta.");
        }

        Venta venta = new Venta();
        venta.setMetodoPago(metodoPago.toUpperCase());

        BigDecimal totalAcumulado = new BigDecimal("0.0");;

        for (Long cajaId : cajaIds) {
            // Buscamos la caja física
            CajaStock caja = cajaStockRepository.findById(cajaId)
                    .orElseThrow(() -> new IllegalArgumentException("La caja con ID " + cajaId + " no existe."));

            if (!caja.DISPONIBLE.equalsIgnoreCase(caja.getEstado())) {
                throw new IllegalStateException("El zapato con QR " + caja.getQrCodigoUnico() + " ya no está disponible.");
            }

            // "Quemamos" la caja cambiando su estado
            caja.setEstado(caja.VENDIDO);
            cajaStockRepository.save(caja);

            // Creamos la línea de detalle de la venta
            LineaVenta linea = new LineaVenta();
            linea.setCajaStock(caja);
            // El precio de venta lo extraemos del precioBase del producto en ese momento
            BigDecimal precioAplicado = caja.getVariante().getProducto().getPrecioBase();
            linea.setPrecioFinalCobrado(precioAplicado);

            totalAcumulado = totalAcumulado.add(precioAplicado);

            // Vinculamos la línea a la venta usando el método helper de la entidad
            venta.agregarLinea(linea);
        }

        venta.setTotal(totalAcumulado);

        // Guardamos la venta (gracias al CascadeType.ALL, se guardan las líneas a la vez automáticamente)
        return ventaRepository.save(venta);
    }
}