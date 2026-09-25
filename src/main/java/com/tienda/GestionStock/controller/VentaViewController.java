package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.model.Venta;
import com.tienda.GestionStock.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaViewController {

    private final VentaService ventaService;

    public VentaViewController(VentaService ventaService) {

        this.ventaService = ventaService;
    }

    // Abre el mostrador de ventas vacío
    @GetMapping("/mostrador")
    public String mostrarMostradorVentas() {
        return "mostrador-ventas";
    }

    // Procesa el cobro del carrito
    @PostMapping("/procesar")
    public String procesarVenta(@RequestParam("cajaIds") List<Long> cajaIds,
                                @RequestParam("metodoPago") String metodoPago,
                                RedirectAttributes redirectAttributes) {
        try {
            Venta ventaGuardada = ventaService.registrarVenta(cajaIds, metodoPago);

            // Pasamos un mensaje de éxito a la siguiente pantalla
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "¡Venta registrada con éxito! Ticket Nº: " + ventaGuardada.getId() + " - Total: " + ventaGuardada.getTotal() + "€");

            return "redirect:/ventas/mostrador";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/ventas/mostrador";
        }
    }
}