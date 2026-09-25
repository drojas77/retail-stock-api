package com.tienda.GestionStock.dto;

import com.tienda.GestionStock.model.LineaDevolucion;
import com.tienda.GestionStock.model.LineaVenta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResumenVentasDTO {

    private String periodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long totalParesVendidos;
    private long totalParesDevueltos;
    private BigDecimal totalIngresosVentas;
    private BigDecimal totalImporteDevoluciones;
    private BigDecimal balanceNeto;

    // Listas con el detalle de las operaciones
    private List<LineaVenta> lineasVentas = new ArrayList<>();
    private List<LineaDevolucion> lineasDevoluciones = new ArrayList<>();

    public ResumenVentasDTO() {}

    public ResumenVentasDTO(String periodo, LocalDate fechaInicio, LocalDate fechaFin,
                            long totalParesVendidos, long totalParesDevueltos,
                            BigDecimal totalIngresosVentas, BigDecimal totalImporteDevoluciones,
                            BigDecimal balanceNeto, List<LineaVenta> lineasVentas,
                            List<LineaDevolucion> lineasDevoluciones) {
        this.periodo = periodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalParesVendidos = totalParesVendidos;
        this.totalParesDevueltos = totalParesDevueltos;
        this.totalIngresosVentas = totalIngresosVentas;
        this.totalImporteDevoluciones = totalImporteDevoluciones;
        this.balanceNeto = balanceNeto;
        this.lineasVentas = lineasVentas;
        this.lineasDevoluciones = lineasDevoluciones;
    }

    // Getters y Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public long getTotalParesVendidos() { return totalParesVendidos; }
    public void setTotalParesVendidos(long totalParesVendidos) { this.totalParesVendidos = totalParesVendidos; }

    public long getTotalParesDevueltos() { return totalParesDevueltos; }
    public void setTotalParesDevueltos(long totalParesDevueltos) { this.totalParesDevueltos = totalParesDevueltos; }

    public BigDecimal getTotalIngresosVentas() { return totalIngresosVentas; }
    public void setTotalIngresosVentas(BigDecimal totalIngresosVentas) { this.totalIngresosVentas = totalIngresosVentas; }

    public BigDecimal getTotalImporteDevoluciones() { return totalImporteDevoluciones; }
    public void setTotalImporteDevoluciones(BigDecimal totalImporteDevoluciones) { this.totalImporteDevoluciones = totalImporteDevoluciones; }

    public BigDecimal getBalanceNeto() { return balanceNeto; }
    public void setBalanceNeto(BigDecimal balanceNeto) { this.balanceNeto = balanceNeto; }

    public List<LineaVenta> getLineasVentas() { return lineasVentas; }
    public void setLineasVentas(List<LineaVenta> lineasVentas) { this.lineasVentas = lineasVentas; }

    public List<LineaDevolucion> getLineasDevoluciones() { return lineasDevoluciones; }
    public void setLineasDevoluciones(List<LineaDevolucion> lineasDevoluciones) { this.lineasDevoluciones = lineasDevoluciones; }
}