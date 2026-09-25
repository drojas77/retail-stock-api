package com.tienda.GestionStock.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name="tallas")
public class Talla {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String numero;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

}
