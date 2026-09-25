package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.dto.ResumenVentasDTO;
import com.tienda.GestionStock.service.ArqueoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/arqueo")
public class ArqueoViewController {

    private final ArqueoService arqueoService;

    public ArqueoViewController(ArqueoService arqueoService) {
        this.arqueoService = arqueoService;
    }

    @GetMapping
    public String verArqueo(
            @RequestParam(name = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(name = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // Por defecto, si no hay filtro de fechas, mostramos la caja de HOY
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }
        if (fechaFin == null) {
            fechaFin = fechaInicio;
        }

        // Construimos una etiqueta para mostrar en pantalla según el rango
        String etiqueta = fechaInicio.equals(fechaFin)
                ? "Día " + fechaInicio
                : "Del " + fechaInicio + " al " + fechaFin;

        ResumenVentasDTO resumen = arqueoService.obtenerResumenPorRango(etiqueta, fechaInicio, fechaFin);

        model.addAttribute("resumen", resumen);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "arqueo"; // Vuelve a cargar la misma vista arqueo.html
    }
}
