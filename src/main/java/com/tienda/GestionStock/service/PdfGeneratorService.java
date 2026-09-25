package com.tienda.GestionStock.service;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.util.QrGeneratorUtil;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generarPdfEtiquetasDobles(List<CajaStock> cajas) {
        // Ajustamos los márgenes a 15 puntos para aprovechar al máximo el ancho del folio A4
        Document document = new Document(PageSize.A4, 15, 15, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fuentes limpias, profesionales y compactas
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fontDetalle = FontFactory.getFont(FontFactory.HELVETICA, 7.5f);
            Font fontAviso = FontFactory.getFont(FontFactory.HELVETICA, 5.5f, Font.ITALIC);

            // 1. TABLA EXTERNA PRINCIPAL: Cuadrícula de 2 columnas (Dos cajas diferentes por fila)
            PdfPTable tablaPrincipal = new PdfPTable(2);
            tablaPrincipal.setWidthPercentage(100);
            tablaPrincipal.setWidths(new float[]{50f, 50f}); // Mitad y mitad del folio
            tablaPrincipal.setSpacingBefore(5f);

            for (CajaStock caja : cajas) {

                // Esta es la celda que contendrá el bloque completo de UNA caja
                PdfPCell celdaCajaContenedora = new PdfPCell();
                celdaCajaContenedora.setPadding(6);
                celdaCajaContenedora.setBorderColor(java.awt.Color.LIGHT_GRAY);
                celdaCajaContenedora.setBorderWidth(0.5f);

                // 2. PRIMERA SUBTABLA: Divide la caja en dos (Lado Izquierdo = TIENDA | Lado Derecho = VENTA)
                PdfPTable subTablaTiendaVenta = new PdfPTable(2);
                subTablaTiendaVenta.setWidthPercentage(100);
                subTablaTiendaVenta.setWidths(new float[]{50f, 50f}); // 50% para tienda y 50% para venta

                // Generamos la imagen del código QR una sola vez para esta caja (tamaño optimizado)
                //String contenidoQr = caja.getSkuCompleto();
                String contenidoQr = caja.getQrCodigoUnico();
                //BufferedImage bufferedImage = QrGeneratorUtil.generarCodigoQr(contenidoQr, 90, 90);
                BufferedImage bufferedImage = QrGeneratorUtil.generarCodigoQr(contenidoQr, 200, 200);
                Image imgQr = Image.getInstance(bufferedImage, null);
                //imgQr.scaleAbsolute(42f, 42f); // Tamaño ideal compacto de unos 1.5 cm
                imgQr.scaleAbsolute(55f, 55f); // Tamaño ideal compacto de unos 1.5 cm
                imgQr.setAlignment(Element.ALIGN_CENTER);


                // =========================================================
                // BLOQUE A: MITAD IZQUIERDA (ETIQUETA DE LA TIENDA)
                // =========================================================
                PdfPCell celdaBloqueTienda = new PdfPCell();
                celdaBloqueTienda.setBorder(PdfPCell.NO_BORDER);
                // Dejamos un margen derecho para simular la línea de corte central
                celdaBloqueTienda.setPaddingRight(4);

                // Creamos la tablita interna para separar Info (izq) y QR (der) dentro de Tienda
                PdfPTable tablaInfoQrTienda = new PdfPTable(2);
                tablaInfoQrTienda.setWidthPercentage(100);
                tablaInfoQrTienda.setWidths(new float[]{68f, 32f}); // 68% texto, 32% el QR lateral

                // Info Tienda (Texto)
                PdfPCell celdaInfoTienda = new PdfPCell();
                celdaInfoTienda.setBorder(PdfPCell.NO_BORDER);
                celdaInfoTienda.addElement(new Paragraph(caja.getProducto().getMarca().getNombre() + " " + caja.getProducto().getNombre(), fontTitulo));
                celdaInfoTienda.addElement(new Paragraph("Col: " + caja.getProducto().getColor(), fontDetalle));
                celdaInfoTienda.addElement(new Paragraph("Talla: " + caja.getNumeroTallaText(), fontTitulo)); // Talla resaltada
                //celdaInfoTienda.addElement(new Paragraph("id: " + caja.getId(), fontTitulo)); // Talla resaltada
                // NUEVO: Pintamos el texto del QR abajo pequeñito para tenerlo a la vista
                celdaInfoTienda.addElement(new Paragraph("QR: " + caja.getQrCodigoUnico(), fontAviso));
                /*if (caja.getUbicacion() != null) {
                    celdaInfoTienda.addElement(new Paragraph("Ubic: " + caja.getUbicacion(), fontDetalle));
                }*/
                tablaInfoQrTienda.addCell(celdaInfoTienda);

                // QR Tienda (Imagen)
                PdfPCell celdaQrTienda = new PdfPCell();
                celdaQrTienda.setBorder(PdfPCell.NO_BORDER);
                celdaQrTienda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaQrTienda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaQrTienda.addElement(imgQr);
                tablaInfoQrTienda.addCell(celdaQrTienda);

                celdaBloqueTienda.addElement(tablaInfoQrTienda);
                subTablaTiendaVenta.addCell(celdaBloqueTienda);


                // =========================================================
                // BLOQUE B: MITAD DERECHA (TICKET DE VENTA ARRANCABLE)
                // =========================================================
                PdfPCell celdaBloqueVenta = new PdfPCell();
                // Ponemos una línea punteada a la izquierda para saber por dónde doblar/cortar el ticket de venta
                celdaBloqueVenta.setBorder(PdfPCell.LEFT);
                celdaBloqueVenta.setBorderColor(java.awt.Color.GRAY);
                celdaBloqueVenta.setBorderWidth(0.5f);
                celdaBloqueVenta.setPaddingLeft(6);

                // Creamos la tablita interna para separar Info (izq) y QR (der) dentro de Venta
                PdfPTable tablaInfoQrVenta = new PdfPTable(2);
                tablaInfoQrVenta.setWidthPercentage(100);
                tablaInfoQrVenta.setWidths(new float[]{68f, 32f});

                // Info Venta (Texto)
                PdfPCell celdaInfoVenta = new PdfPCell();
                celdaInfoVenta.setBorder(PdfPCell.NO_BORDER);

                celdaInfoVenta.addElement(new Paragraph(caja.getProducto().getMarca().getNombre() + " " + caja.getProducto().getNombre(), fontTitulo));
                celdaInfoVenta.addElement(new Paragraph("Col: " + caja.getProducto().getColor(), fontDetalle));
                celdaInfoVenta.addElement(new Paragraph("Talla: " + caja.getNumeroTallaText(), fontTitulo));
                celdaInfoVenta.addElement(new Paragraph("QR: " + caja.getQrCodigoUnico(), fontAviso));
                //celdaInfoVenta.addElement(new Paragraph("id: " + caja.getId(), fontTitulo));
                //celdaInfoVenta.addElement(new Paragraph("Arranque al vender", fontAviso));
                tablaInfoQrVenta.addCell(celdaInfoVenta);

                // QR Venta (Misma imagen reutilizada para ahorrar memoria)
                PdfPCell celdaQrVenta = new PdfPCell();
                celdaQrVenta.setBorder(PdfPCell.NO_BORDER);
                celdaQrVenta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaQrVenta.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaQrVenta.addElement(imgQr);
                tablaInfoQrVenta.addCell(celdaQrVenta);

                celdaBloqueVenta.addElement(tablaInfoQrVenta);
                subTablaTiendaVenta.addCell(celdaBloqueVenta);

                // ---------------------------------------------------------
                // Cerramos el bloque metiendo las subtablas en la principal
                // ---------------------------------------------------------
                celdaCajaContenedora.addElement(subTablaTiendaVenta);
                tablaPrincipal.addCell(celdaCajaContenedora);
            }

            // Si el lote de pares introducido es impar, metemos una celda invisible para que no se descuadre la última fila
            if (cajas.size() % 2 != 0) {
                PdfPCell celdaVacia = new PdfPCell();
                celdaVacia.setBorder(PdfPCell.NO_BORDER);
                tablaPrincipal.addCell(celdaVacia);
            }

            document.add(tablaPrincipal);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generarEtiquetaIndividual(CajaStock caja) {
        // Mantenim exactament la mateixa mida de pàgina A4 i marges que el teu mètode per lots
        Document document = new Document(PageSize.A4, 15, 15, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Clònica exacta de les teves fonts professionals i compactes
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fontDetalle = FontFactory.getFont(FontFactory.HELVETICA, 7.5f);
            Font fontAviso = FontFactory.getFont(FontFactory.HELVETICA, 5.5f, Font.ITALIC);

            // 1. Creem una estructura de 2 columnes (com la principal), però configurada
            // perquè només ocupi la meitat del full (el costat esquerre), igual que faria la graella
            PdfPTable tablaPrincipal = new PdfPTable(2);
            tablaPrincipal.setWidthPercentage(100);
            tablaPrincipal.setWidths(new float[]{50f, 50f});
            tablaPrincipal.setSpacingBefore(5f);

            // Cel·la contenidora individual
            PdfPCell celdaCajaContenedora = new PdfPCell();
            celdaCajaContenedora.setPadding(6);
            celdaCajaContenedora.setBorderColor(java.awt.Color.LIGHT_GRAY);
            celdaCajaContenedora.setBorderWidth(0.5f);

            // 2. SUBTAULA INTERNA: Divideix l'etiqueta en dues (Esquerra = TIENDA | Dreta = VENTA)
            PdfPTable subTablaTiendaVenta = new PdfPTable(2);
            subTablaTiendaVenta.setWidthPercentage(100);
            subTablaTiendaVenta.setWidths(new float[]{50f, 50f});

            // Generem la imatge del QR reutilitzant la teva lògica exacta (mida 90x90 i escalat a 42f)
            String contenidoQr = caja.getQrCodigoUnico();
            //BufferedImage bufferedImage = QrGeneratorUtil.generarCodigoQr(contenidoQr, 90, 90);
            BufferedImage bufferedImage = QrGeneratorUtil.generarCodigoQr(contenidoQr, 200, 200);
            Image imgQr = Image.getInstance(bufferedImage, null);
            //imgQr.scaleAbsolute(42f, 42f);
            imgQr.scaleAbsolute(55f, 55f);
            imgQr.setAlignment(Element.ALIGN_CENTER);

            // =========================================================
            // BLOQUE A: MITAD IZQUIERDA (ETIQUETA DE LA TIENDA)
            // =========================================================
            PdfPCell celdaBloqueTienda = new PdfPCell();
            celdaBloqueTienda.setBorder(PdfPCell.NO_BORDER);
            celdaBloqueTienda.setPaddingRight(4);

            PdfPTable tablaInfoQrTienda = new PdfPTable(2);
            tablaInfoQrTienda.setWidthPercentage(100);
            tablaInfoQrTienda.setWidths(new float[]{68f, 32f});

            // Info Tienda (Adaptat amb els mètodes getProducto() que s'observen al teu PdfGenerator)
            PdfPCell celdaInfoTienda = new PdfPCell();
            celdaInfoTienda.setBorder(PdfPCell.NO_BORDER);
            celdaInfoTienda.addElement(new Paragraph(caja.getProducto().getMarca().getNombre() + " " + caja.getProducto().getNombre(), fontTitulo));
            celdaInfoTienda.addElement(new Paragraph("Col: " + caja.getProducto().getColor(), fontDetalle));
            celdaInfoTienda.addElement(new Paragraph("Talla: " + caja.getNumeroTallaText(), fontTitulo));
            celdaInfoTienda.addElement(new Paragraph("QR: " + caja.getQrCodigoUnico(), fontAviso));
            tablaInfoQrTienda.addCell(celdaInfoTienda);

            // QR Tienda
            PdfPCell celdaQrTienda = new PdfPCell();
            celdaQrTienda.setBorder(PdfPCell.NO_BORDER);
            celdaQrTienda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaQrTienda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaQrTienda.addElement(imgQr);
            tablaInfoQrTienda.addCell(celdaQrTienda);

            celdaBloqueTienda.addElement(tablaInfoQrTienda);
            subTablaTiendaVenta.addCell(celdaBloqueTienda);

            // =========================================================
            // BLOQUE B: MITAD DERECHA (TICKET DE VENTA ARRANCABLE)
            // =========================================================
            PdfPCell celdaBloqueVenta = new PdfPCell();
            celdaBloqueVenta.setBorder(PdfPCell.LEFT);
            celdaBloqueVenta.setBorderColor(java.awt.Color.GRAY);
            celdaBloqueVenta.setBorderWidth(0.5f);
            celdaBloqueVenta.setPaddingLeft(6);

            PdfPTable tablaInfoQrVenta = new PdfPTable(2);
            tablaInfoQrVenta.setWidthPercentage(100);
            tablaInfoQrVenta.setWidths(new float[]{68f, 32f});

            // Info Venta
            PdfPCell celdaInfoVenta = new PdfPCell();
            celdaInfoVenta.setBorder(PdfPCell.NO_BORDER);
            celdaInfoVenta.addElement(new Paragraph(caja.getProducto().getMarca().getNombre() + " " + caja.getProducto().getNombre(), fontTitulo));
            celdaInfoVenta.addElement(new Paragraph("Col: " + caja.getProducto().getColor(), fontDetalle));
            celdaInfoVenta.addElement(new Paragraph("Talla: " + caja.getNumeroTallaText(), fontTitulo));
            celdaInfoVenta.addElement(new Paragraph("QR: " + caja.getQrCodigoUnico(), fontAviso));
            tablaInfoQrVenta.addCell(celdaInfoVenta);

            // QR Venta
            PdfPCell celdaQrVenta = new PdfPCell();
            celdaQrVenta.setBorder(PdfPCell.NO_BORDER);
            celdaQrVenta.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaQrVenta.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaQrVenta.addElement(imgQr);
            tablaInfoQrVenta.addCell(celdaQrVenta);

            celdaBloqueVenta.addElement(tablaInfoQrVenta);
            subTablaTiendaVenta.addCell(celdaBloqueVenta);

            // Unim les subtaules a la cel·la contenidora
            celdaCajaContenedora.addElement(subTablaTiendaVenta);
            tablaPrincipal.addCell(celdaCajaContenedora);

            // Inserim una cel·la buida a la dreta (buit invisible) perquè l'etiqueta ocupi exactament
            // el mateix espai quadrat que tindria al teu mètode de lots i no es deformi a l'ample
            PdfPCell celdaVacia = new PdfPCell();
            celdaVacia.setBorder(PdfPCell.NO_BORDER);
            tablaPrincipal.addCell(celdaVacia);

            document.add(tablaPrincipal);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
