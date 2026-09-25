package com.tienda.GestionStock.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public class QrGeneratorUtil {

    /**
     * Genera un código QR en memoria como un BufferedImage.
     * @param texto El contenido que guardará el QR (Ej: "QR-MUNI-COPA-AZUL-41-ID")
     * @param ancho Ancho en píxeles
     * @param alto Alto en píxeles
     * @return BufferedImage listo para ser insertado en el PDF
     */
    public static BufferedImage generarCodigoQr(String texto, int ancho, int alto) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        // Generamos la matriz de bits del código QR con codificación estándar
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

        // Convertimos la matriz directamente a una imagen en memoria utilizable por Java
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}