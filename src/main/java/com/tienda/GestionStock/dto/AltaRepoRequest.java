package com.tienda.GestionStock.dto;

public class AltaRepoRequest {

    private Integer productoId;
    private String prefijoCodigo; // Ej: NIKE-AIR
    private String repoTexto;    // Ej: 38-2,39-4,40-3
    private String ubicacionAlmacen;


    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getUbicacionAlmacen() {
        return ubicacionAlmacen;
    }

    public void setUbicacionAlmacen(String ubicacionAlmacen) {
        this.ubicacionAlmacen = ubicacionAlmacen;
    }

    public String getRepoTexto() {
        return repoTexto;
    }

    public void setRepoTexto(String repoTexto) {
        this.repoTexto = repoTexto;
    }

    public String getPrefijoCodigo() {
        return prefijoCodigo;
    }

    public void setPrefijoCodigo(String prefijoCodigo) {
        this.prefijoCodigo = prefijoCodigo;
    }
}
