package com.tienda.GestionStock.service;

import com.tienda.GestionStock.dto.ResumenVentasDTO;
import com.tienda.GestionStock.model.Devolucion;
import com.tienda.GestionStock.model.LineaDevolucion;
import com.tienda.GestionStock.model.LineaVenta;
import com.tienda.GestionStock.model.Venta;
import com.tienda.GestionStock.repository.DevolucionRepository;
import com.tienda.GestionStock.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArqueoServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DevolucionRepository devolucionRepository;

    @InjectMocks
    private ArqueoService arqueoService;

    private Venta venta1;
    private Venta venta2;
    private Devolucion devolucion1;

    @BeforeEach
    void setUp() {
        // --- 1. Preparar Ventas (2 ventas: 1 par a 50.00€ y 2 pares a 30.00€ c/u) ---
        venta1 = new Venta();
        venta1.setTotal(new BigDecimal("50.00"));
        LineaVenta lv1 = new LineaVenta();
        venta1.agregarLinea(lv1);

        venta2 = new Venta();
        venta2.setTotal(new BigDecimal("60.00"));
        LineaVenta lv2 = new LineaVenta();
        LineaVenta lv3 = new LineaVenta();
        venta2.agregarLinea(lv2);
        venta2.agregarLinea(lv3);

        // --- 2. Preparar Devoluciones (1 devolución: 1 par a 20.00€) ---
        devolucion1 = new Devolucion();
        devolucion1.setTotal(new BigDecimal("20.00"));
        LineaDevolucion ld1 = new LineaDevolucion();
        devolucion1.agregarLinea(ld1);
    }

    @Test
    @DisplayName("Debe calcular correctamente el resumen de caja con ventas y devoluciones en un rango")
    void obtenerResumenPorRango_CalculaTotalesYBalanceCorrectamente() {
        // Arrange
        LocalDate fecha = LocalDate.of(2026, 9, 24);

        when(ventaRepository.findByFechaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(venta1, venta2));
        when(devolucionRepository.findByFechaDevolucionBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(devolucion1));

        // Act
        ResumenVentasDTO resumen = arqueoService.obtenerResumenDiario(fecha);

        // Assert
        assertNotNull(resumen);
        assertEquals(3, resumen.getTotalParesVendidos()); // 1 par de venta1 + 2 pares de venta2
        assertEquals(1, resumen.getTotalParesDevueltos()); // 1 par de devolucion1

        assertEquals(new BigDecimal("110.00"), resumen.getTotalIngresosVentas()); // 50.00 + 60.00
        assertEquals(new BigDecimal("20.00"), resumen.getTotalImporteDevoluciones()); // 20.00
        assertEquals(new BigDecimal("90.00"), resumen.getBalanceNeto()); // 110.00 - 20.00

        verify(ventaRepository, times(1)).findByFechaBetween(any(), any());
        verify(devolucionRepository, times(1)).findByFechaDevolucionBetween(any(), any());
    }

    @Test
    @DisplayName("Debe retornar ceros cuando no hay ventas ni devoluciones en el rango seleccionado")
    void obtenerResumenPorRango_SinRegistros_RetornaTotalesEnCero() {
        // Arrange
        LocalDate fecha = LocalDate.of(2026, 9, 24);

        when(ventaRepository.findByFechaBetween(any(), any())).thenReturn(Collections.emptyList());
        when(devolucionRepository.findByFechaDevolucionBetween(any(), any())).thenReturn(Collections.emptyList());

        // Act
        ResumenVentasDTO resumen = arqueoService.obtenerResumenDiario(fecha);

        // Assert
        assertNotNull(resumen);
        assertEquals(0, resumen.getTotalParesVendidos());
        assertEquals(0, resumen.getTotalParesDevueltos());
        assertEquals(BigDecimal.ZERO, resumen.getTotalIngresosVentas());
        assertEquals(BigDecimal.ZERO, resumen.getTotalImporteDevoluciones());
        assertEquals(BigDecimal.ZERO, resumen.getBalanceNeto());
    }
}
