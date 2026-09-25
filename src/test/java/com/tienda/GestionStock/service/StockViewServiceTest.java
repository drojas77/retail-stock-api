package com.tienda.GestionStock.service;

import com.tienda.GestionStock.repository.CajaStockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockViewServiceTest {

    @Mock
    private CajaStockRepository cajaStockRepository;

    @InjectMocks
    private StockViewService stockViewService;

    @Test
    @DisplayName("Debe confirmar que existe stock cuando el repositorio encuentra el QR")
    void testExisteStockEncontrado() {
        // Arrange (Preparación)
        String qrCode = "QR-NEWB-NEWB-BLAN-42-9431F9A9-1971";
        when(cajaStockRepository.existsByQrCodigoUnico(qrCode)).thenReturn(true);

        // Act (Ejecución)
        boolean resultado = stockViewService.existeStock(qrCode);

        // Assert (Verificación)
        assertTrue(resultado);
        verify(cajaStockRepository, times(1)).existsByQrCodigoUnico(qrCode);
    }

    @Test
    @DisplayName("Debe retornar false cuando el QR no está registrado")
    void testExisteStockNoEncontrado() {
        // Arrange
        String qrCode = "QR-INEXISTENTE";
        when(cajaStockRepository.existsByQrCodigoUnico(qrCode)).thenReturn(false);

        // Act
        boolean resultado = stockViewService.existeStock(qrCode);

        // Assert
        assertFalse(resultado);
        verify(cajaStockRepository, times(1)).existsByQrCodigoUnico(qrCode);
    }
}