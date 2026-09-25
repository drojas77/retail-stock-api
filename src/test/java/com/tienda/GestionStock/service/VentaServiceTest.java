package com.tienda.GestionStock.service;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.Producto;
import com.tienda.GestionStock.model.VarianteProducto;
import com.tienda.GestionStock.model.Venta;
import com.tienda.GestionStock.repository.CajaStockRepository;
import com.tienda.GestionStock.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private CajaStockRepository cajaStockRepository;

    @InjectMocks
    private VentaService ventaService;

    private CajaStock cajaDisponible;
    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        // Preparamos los objetos mock necesarios para las pruebas
        productoEjemplo = new Producto();
        productoEjemplo.setPrecioBase(new BigDecimal("59.95"));

        VarianteProducto variante = new VarianteProducto();
        variante.setProducto(productoEjemplo);

        cajaDisponible = new CajaStock();
        cajaDisponible.setId(1L);
        cajaDisponible.setQrCodigoUnico("QR-TEST-001");
        cajaDisponible.setEstado(CajaStock.DISPONIBLE);
        cajaDisponible.setVariante(variante);
    }

    // --- PRUEBAS DE REGISTRAR VENTA ---

    @Test
    @DisplayName("Registrar venta con éxito: Cambia estado a VENDIDO y calcula el total acumulado")
    void registrarVenta_Exito() {
        // Arrange
        List<Long> cajaIds = List.of(1L);
        when(cajaStockRepository.findById(1L)).thenReturn(Optional.of(cajaDisponible));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Venta ventaRealizada = ventaService.registrarVenta(cajaIds, "EFECTIVO");

        // Assert
        assertNotNull(ventaRealizada);
        assertEquals("EFECTIVO", ventaRealizada.getMetodoPago());
        assertEquals(new BigDecimal("59.95"), ventaRealizada.getTotal());
        assertEquals(CajaStock.VENDIDO, cajaDisponible.getEstado()); // Comprueba que cambió el estado de la caja

        verify(cajaStockRepository, times(1)).save(cajaDisponible);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la lista de productos está vacía o es nula")
    void registrarVenta_ListaVacia_LanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                ventaService.registrarVenta(Collections.emptyList(), "TARJETA")
        );

        assertThrows(IllegalArgumentException.class, () ->
                ventaService.registrarVenta(null, "TARJETA")
        );

        verifyNoInteractions(ventaRepository);
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si la caja ya no está DISPONIBLE")
    void registrarVenta_CajaNoDisponible_LanzaExcepcion() {
        // Arrange
        cajaDisponible.setEstado(CajaStock.VENDIDO); // Cambiamos a ya vendido
        when(cajaStockRepository.findById(1L)).thenReturn(Optional.of(cajaDisponible));

        // Act & Assert
        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () ->
                ventaService.registrarVenta(List.of(1L), "TARJETA")
        );

        assertTrue(excepcion.getMessage().contains("ya no está disponible"));
        verify(cajaStockRepository, never()).save(any());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la caja no existe en la BD")
    void registrarVenta_CajaInexistente_LanzaExcepcion() {
        // Arrange
        when(cajaStockRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                ventaService.registrarVenta(List.of(99L), "EFECTIVO")
        );
    }
}