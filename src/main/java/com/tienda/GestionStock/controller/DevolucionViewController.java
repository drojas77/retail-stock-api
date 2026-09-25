package com.tienda.GestionStock.controller;


import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.Devolucion;
import com.tienda.GestionStock.service.DevolucionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/devoluciones")
public class DevolucionViewController {


    private final DevolucionService devolucionService;

    public DevolucionViewController(DevolucionService devolucionService) {

        this.devolucionService = devolucionService;

    }

    // Abre el mostrador de ventas vacío
    @GetMapping("/mostrador")
    public String mostrarMostradorVentas() {
        return "mostrador-devoluciones";
    }



    @PostMapping("/procesar")
    public String procesarDevolucion(@RequestParam("cajaIds") List<Long> cajaIds
                                ,@RequestParam("tipoAbono") String tipoAbono
                                ,RedirectAttributes redirectAttributes) {
        try {
            Devolucion devolucionGuardada = devolucionService.procesarDevolucion(cajaIds, tipoAbono);

                // Pasamos un mensaje de éxito a la siguiente pantalla
                redirectAttributes.addFlashAttribute("mensajeExito",
                        "¡Devolucion registrada con éxito! Ticket Nº: " + devolucionGuardada.getId());

            return "redirect:/devoluciones/mostrador";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/devoluciones/mostrador";
        }
    }

}
