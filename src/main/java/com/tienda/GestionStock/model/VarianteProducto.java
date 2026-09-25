package com.tienda.GestionStock.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name="variantes_producto")
public class VarianteProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne: Muchas variantes pueden pertenecer a un mismo Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Relación ManyToOne: Muchas variantes pueden compartir la misma Talla
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talla_id", nullable = false)
    private Talla talla;

    @Column(name = "sku_modelo", nullable = false, unique = true, length = 50)
    private String skuModelo;

    // Este campo es opcional en tu BD (puede ser NULL si se aplica el precio_base del producto)
    @Column(name = "precio_especifico", precision = 10, scale = 2)
    private BigDecimal precioEspecifico;


    public BigDecimal getPrecioEspecifico() {
        return precioEspecifico;
    }

    public void setPrecioEspecifico(BigDecimal precioEspecifico) {
        this.precioEspecifico = precioEspecifico;
    }

    public String getSkuModelo() {
        return skuModelo;
    }

    public void setSkuModelo(String skuModelo) {
        this.skuModelo = skuModelo;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
