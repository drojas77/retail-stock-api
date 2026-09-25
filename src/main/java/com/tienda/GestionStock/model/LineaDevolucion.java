package com.tienda.GestionStock.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "lineas_devolucion")
public class LineaDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne: Muchas líneas de detalle pertenecen a una misma factura/venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;

    // Relación ManyToOne: Muchas líneas de detalle pertenecen a una caja física de stock
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private CajaStock cajaStock;

    @Column(name = "precio_final_abonado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFinalAbonado;

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public BigDecimal getPrecioFinalAbonado() {
        return precioFinalAbonado;
    }

    public void setPrecioFinalAbonado(BigDecimal precioFinalAbonado) {
        this.precioFinalAbonado = precioFinalAbonado;
    }

    public CajaStock getCajaStock() {
        return cajaStock;
    }

    public void setCajaStock(CajaStock cajaStock) {
        this.cajaStock = cajaStock;
    }
}