package com.tienda.GestionStock.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago; // 'TARJETA', 'EFECTIVO', etc.

    // Relación uno a muchos: Una venta tiene muchas líneas de detalle
    // CascadeType.ALL hace que al guardar la venta se guarden todas sus líneas automáticamente
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaVenta> lineas = new ArrayList<>();


    // --- MÉTODO HELPER PARA AÑADIR LÍNEAS CÓMODAMENTE ---
    public void agregarLinea(LineaVenta linea) {
        lineas.add(linea);
        linea.setVenta(this);
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<LineaVenta> getLineas() {
        return lineas;
    }
    public void setLineas(List<LineaVenta> lineas) { this.lineas = lineas;
    }
}