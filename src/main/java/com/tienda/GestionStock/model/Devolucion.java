package com.tienda.GestionStock.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDateTime fechaDevolucion;

    private String motivo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "tipo_abono")
    private String tipoAbono;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaDevolucion> lineas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private CajaStock tiendaCaja; // Asegúrate de cambiar "Caja" por el nombre real de tu entidad de terminales

    public CajaStock getTiendaCaja() {
        return tiendaCaja;
    }

    public void setTiendaCaja(CajaStock tiendaCaja) {
        this.tiendaCaja = tiendaCaja;
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaDevolucion == null) {
            this.fechaDevolucion = LocalDateTime.now();
        }
    }
    // --- MÉTODO HELPER ---
    public void agregarLinea(LineaDevolucion linea) {
        lineas.add(linea);
        linea.setDevolucion(this);
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getTipoAbono() { return tipoAbono; }
    public void setTipoAbono(String tipoAbono) { this.tipoAbono = tipoAbono; }

    public List<LineaDevolucion> getLineas() { return lineas; }
    public void setLineas(List<LineaDevolucion> lineas) { this.lineas = lineas; }
}