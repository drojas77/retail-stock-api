package com.tienda.GestionStock.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cajas_stock")
public class CajaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne: Muchas cajas individuales de zapatos pertenecen a una misma variante (Modelo + Talla)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProducto variante;

    @Column(name = "qr_codigo_unico", nullable = false, unique = true, length = 100)
    private String qrCodigoUnico;

    // Nota: Aunque en SQL pusiste un CHECK, en Java lo manejamos como String de momento.
    // Los valores válidos según tu BD serán: 'DISPONIBLE', 'VENDIDO', 'DEVUELTO'
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "ubicacion_almacen", length = 50)
    private String ubicacionAlmacen;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public static final String DISPONIBLE= "DISPONIBLE";

    @Transient
    public static final String VENDIDO= "VENDIDO";

    // 1. Puente para obtener la Talla sin dar rodeos
    // Reemplaza el anterior getTalla() en CajaStock.java por este:
    public String getNumeroTallaText() {
        if (this.variante != null && this.variante.getTalla() != null) {
            // Aquí usamos una propiedad nativa de los objetos en Java:
            // Si tu clase Talla tiene un método tipo getNumero() o getTalla(), lo pones aquí.
            // Si no estás seguro de cómo se llama, 'String.valueOf(...)' convertirá a texto
            // el objeto Talla llamando a su método toString().
            return String.valueOf(this.variante.getTalla().getNumero());
        }
        return "N/A";
    }

    // 2. Puente para obtener el SKU (en tu tabla se llama skuModelo en la variante)
    public String getSkuCompleto() {
        if (this.variante != null) {
            return this.variante.getSkuModelo(); // <- El SKU de 4-4-4 letras que guardamos en la variante
        }
        return "SIN-SKU";
    }


    public Producto getProducto() {
        if (this.variante != null) {
            return this.variante.getProducto();
        }
        return null;
    }

    public String getUbicacion() {
        return this.ubicacionAlmacen; // <- Cambia 'ubicacionAlmacen' por el nombre exacto de tu variable de ubicación
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VarianteProducto getVariante() {
        return variante;
    }

    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
    }

    public String getQrCodigoUnico() {
        return qrCodigoUnico;
    }

    public void setQrCodigoUnico(String qrCodigoUnico) {
        this.qrCodigoUnico = qrCodigoUnico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacionAlmacen() {
        return ubicacionAlmacen;
    }

    public void setUbicacionAlmacen(String ubicacionAlmacen) {
        this.ubicacionAlmacen = ubicacionAlmacen;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}