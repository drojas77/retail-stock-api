package com.tienda.GestionStock.dto;

public class CajaDTO {
    private Long id;
    private String qr;
    private String marca;
    private String modelo;
    private String color;
    private String talla;
    private String ubicacion;
    private String estado;

    public CajaDTO(Long id, String qr, String marca, String modelo, String color, String talla, String ubicacion, String estado) {
        this.id = id;
        this.qr = qr;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.talla = talla;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}