package com.tienda.GestionStock.service;

import com.tienda.GestionStock.model.*;
import com.tienda.GestionStock.repository.CajaStockRepository;
import com.tienda.GestionStock.repository.DevolucionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DevolucionService {

    private final CajaStockRepository cajaStockRepository;
    private final DevolucionRepository devolucionRepository; // Inyectamos el nuevo repositorio

    public DevolucionService(CajaStockRepository cajaStockRepository, DevolucionRepository devolucionRepository) {
        this.cajaStockRepository = cajaStockRepository;
        this.devolucionRepository = devolucionRepository;
    }

    @Transactional
    public Devolucion procesarDevolucion(List<Long> cajaIds, String tipoAbono) {
        if (cajaIds == null || cajaIds.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en el carrito para procesar la venta.");
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setTipoAbono(tipoAbono.toUpperCase());

        BigDecimal totalAcumulado = new BigDecimal("0.0");;

        for (Long cajaId : cajaIds) {
            // Buscamos la caja física
            CajaStock caja = cajaStockRepository.findById(cajaId)
                    .orElseThrow(() -> new IllegalArgumentException("La caja con ID " + cajaId + " no existe."));

            if (!caja.VENDIDO.equalsIgnoreCase(caja.getEstado())) {
                throw new IllegalStateException("El zapato con QR " + caja.getQrCodigoUnico() + " ya no está disponible.");
            }

            // "Quemamos" la caja cambiando su estado
            caja.setEstado(caja.DISPONIBLE);
            cajaStockRepository.save(caja);

            // Creamos la línea de detalle de la venta
            LineaDevolucion linea = new LineaDevolucion();
            linea.setCajaStock(caja);
            // El precio de venta lo extraemos del precioBase del producto en ese momento
            BigDecimal precioAplicado = caja.getVariante().getProducto().getPrecioBase();
            linea.setPrecioFinalAbonado(precioAplicado);

            totalAcumulado = totalAcumulado.add(precioAplicado);

            // Vinculamos la línea a la venta usando el método helper de la entidad
            devolucion.agregarLinea(linea);
            devolucion.setTiendaCaja(caja);
        }

        devolucion.setTotal(totalAcumulado);

        // Guardamos la venta (gracias al CascadeType.ALL, se guardan las líneas a la vez automáticamente)
        return devolucionRepository.save(devolucion);
    }

    public Optional<CajaStock> buscarCajaParaDevolucion(String qrCodigo) {
        Optional<CajaStock> oCaja = cajaStockRepository.findByQrCodigoUnico(qrCodigo.trim());

        // Primer mirem si existeix, i si existeix comprovem si està VENDIDO
        if (oCaja.isPresent() && "VENDIDO".equalsIgnoreCase(oCaja.get().getEstado())) {
            return oCaja;
        }
        return Optional.empty();
    }
    /*
    public Optional<CajaStock> buscarCajaParaDevolucion(String qrCodigo) {

        // Buscamos la coincidencia exacta del DNI de la caja
        Optional<CajaStock> oCaja = cajaStockRepository.findByQrCodigoUnico(qrCodigo.trim());

        CajaStock caja = oCaja.get();

        // Solo la damos por válida si existe y está en la estantería como DISPONIBLE
        if (oCaja.isPresent() && caja.VENDIDO.equalsIgnoreCase(oCaja.get().getEstado())) {
            return oCaja;
        }
        return Optional.empty();
    }*/
}