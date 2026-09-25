package com.tienda.GestionStock.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class VarianteRequest {
    private Long productoId;
    private Integer tallaId; // Recuerda que en Talla usamos Integer (SERIAL en Postgres)
    private String skuModelo;
    private BigDecimal precioEspecifico;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getTallaId() {
        return tallaId;
    }

    public void setTallaId(Integer tallaId) {
        this.tallaId = tallaId;
    }

    public String getSkuModelo() {
        return skuModelo;
    }

    public void setSkuModelo(String skuModelo) {
        this.skuModelo = skuModelo;
    }

    public BigDecimal getPrecioEspecifico() {
        return precioEspecifico;
    }

    public void setPrecioEspecifico(BigDecimal precioEspecifico) {
        this.precioEspecifico = precioEspecifico;
    }
}
