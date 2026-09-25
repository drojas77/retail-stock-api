package com.tienda.GestionStock.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "lineas_venta")
public class LineaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne: Muchas líneas de detalle pertenecen a una misma factura/venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    // Relación ManyToOne: Muchas líneas de detalle pertenecen a una misma factura/venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private CajaStock cajaStock;

    @Column(name = "precio_final_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFinalCobrado;

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }


    public CajaStock getCajaStock() {
        return cajaStock;
    }

    public void setCajaStock(CajaStock cajaStock) {
        this.cajaStock = cajaStock;
    }

    public BigDecimal getPrecioFinalCobrado() {
        return precioFinalCobrado;
    }

    public void setPrecioFinalCobrado(BigDecimal precioFinalCobrado) {
        this.precioFinalCobrado = precioFinalCobrado;
    }
}