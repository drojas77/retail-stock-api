package com.tienda.GestionStock.service;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.repository.CajaStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class GeneradorEtiquetaExpressRunner implements CommandLineRunner {

    private final CajaStockRepository cajaStockRepository;
    private final PdfGeneratorService etiquetaService; // El teu servei de maquetar PDF

    public GeneradorEtiquetaExpressRunner(CajaStockRepository cajaStockRepository, PdfGeneratorService etiquetaService) {
        this.cajaStockRepository = cajaStockRepository;
        this.etiquetaService = etiquetaService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- CONFIGURACIÓ EXPRESS ---
        // 1. Enganxa aquí el codi QR exacte de la caixa que vols reetiquetar:
        String qrAImprimir = "AEMAS-3D44BBA5";

        // 2. Si vols activar-ho, posa-ho en 'true'. Quan acabis, el deixes en 'false'.
        boolean activarImpresion = false;
        // -----------------------------

        if (activarImpresion) {
            System.out.println("====== [CONSOLA] GENERANT ETIQUETA EXPRESS ======");

            Optional<CajaStock> oCaja = cajaStockRepository.findByQrCodigoUnico(qrAImprimir.trim());

            if (oCaja.isPresent()) {
                CajaStock caja = oCaja.get();

                // Cridem al teu servei (el que genera el array de bytes del PDF)
                byte[] pdfBytes = etiquetaService.generarEtiquetaIndividual(caja);

                // Guardem el fitxer directament al teu escriptori de Linux o carpeta temporal
                java.nio.file.Path path = java.nio.file.Paths.get("/home/david/IdeaProjects/GestionStock/etiqueta_recuperada.pdf");
                java.nio.file.Files.write(path, pdfBytes);

                System.out.println("====== [OK] Etiqueta generada correctament a: " + path.toAbsolutePath() + " ======");
            } else {
                System.out.println("====== [ERROR] No s'ha trobat cap caixa amb el QR: " + qrAImprimir + " ======");
            }
        }
    }
}