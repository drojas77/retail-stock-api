package com.tienda.GestionStock.service;

import com.tienda.GestionStock.dto.ResumenVentasDTO;
import com.tienda.GestionStock.model.Devolucion;
import com.tienda.GestionStock.model.LineaDevolucion;
import com.tienda.GestionStock.model.LineaVenta;
import com.tienda.GestionStock.model.Venta;
import com.tienda.GestionStock.repository.DevolucionRepository;
import com.tienda.GestionStock.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ArqueoService {

    private final VentaRepository ventaRepository;
    private final DevolucionRepository devolucionRepository;

    public ArqueoService(VentaRepository ventaRepository, DevolucionRepository devolucionRepository) {
        this.ventaRepository = ventaRepository;
        this.devolucionRepository = devolucionRepository;
    }

    // 1. MÉTODO GENÉRICO POR RANGO DE FECHAS
    @Transactional(readOnly = true)
    public ResumenVentasDTO obtenerResumenPorRango(String etiquetaPeriodo, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        // 1. Obtener ventas y extraer sus líneas de detalle
        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);

        List<LineaVenta> lineasVentas = ventas.stream()
                .flatMap(v -> v.getLineas().stream())
                .toList();

        long paresVendidos = lineasVentas.size();

        BigDecimal ingresosVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Obtener devoluciones y extraer sus líneas de detalle
        List<Devolucion> devoluciones = devolucionRepository.findByFechaDevolucionBetween(inicio, fin);

        List<LineaDevolucion> lineasDevoluciones = devoluciones.stream()
                .flatMap(d -> d.getLineas().stream())
                .toList();

        long paresDevueltos = lineasDevoluciones.size();

        BigDecimal importeDevoluciones = devoluciones.stream()
                .map(Devolucion::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balanceNeto = ingresosVentas.subtract(importeDevoluciones);

        return new ResumenVentasDTO(
                etiquetaPeriodo,
                fechaInicio,
                fechaFin,
                paresVendidos,
                paresDevueltos,
                ingresosVentas,
                importeDevoluciones,
                balanceNeto,
                lineasVentas,
                lineasDevoluciones
        );
    }

    // 2. RESUMEN DIARIO
    public ResumenVentasDTO obtenerResumenDiario(LocalDate fecha) {
        return obtenerResumenPorRango("Día " + fecha, fecha, fecha);
    }

    // 3. RESUMEN SEMANAL (calcula automáticamente el Lunes y Domingo de la semana de la fecha dada)
    public ResumenVentasDTO obtenerResumenSemanal(LocalDate fechaPertenecienteASemana) {
        LocalDate inicioSemana = fechaPertenecienteASemana.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = fechaPertenecienteASemana.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String etiqueta = "Semana del " + inicioSemana + " al " + finSemana;
        return obtenerResumenPorRango(etiqueta, inicioSemana, finSemana);
    }

    // 4. RESUMEN MENSUAL (para un año y mes concreto, ej: 2026, 9)
    public ResumenVentasDTO obtenerResumenMensual(int anio, int mes) {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate finMes = yearMonth.atEndOfMonth();

        String etiqueta = "Mes " + mes + "/" + anio;
        return obtenerResumenPorRango(etiqueta, inicioMes, finMes);
    }
}