package com.tienda.GestionStock.controller;

import com.tienda.GestionStock.dto.AltaRepoRequest;
import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.service.StockViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/stock")
public class StockViewController {

    private final StockViewService stockViewService;

    // Inyectamos el repositorio para poder leer de la base de datos
    public StockViewController(StockViewService stockViewService) {

        this.stockViewService = stockViewService;
    }

    /**
     * URL: GET http://localhost:8080/stock/ver
     */

    @GetMapping("/ver")
    public String verInventario(Model model) {
        model.addAttribute("todasLasMarcas", stockViewService.obtenerTodasMarcas());
        return "inventario";
    }

    @GetMapping("/ver-ultimo-introducido")
    public String verUltimoModeloIntroducido(Model model) {

        // 2. Metemos la lista en el "Model" de Spring para que Thymeleaf pueda leerla
        model.addAttribute("listaCajas", stockViewService.verUltimoModeloIntroducido());
        // 3. Devolvemos el nombre del archivo HTML (sin el .html)
        return "resultado-alta-producto";
    }

    @GetMapping("/ver-ultima-repo")
    public String verUltimaRepoIntroducida(@RequestParam("ids") String idsString, Model model) {

        // 3. Pasamos los datos a Thymeleaf
        model.addAttribute("listaCajas", stockViewService.verUltimaLoteIntroducido(idsString));
        model.addAttribute("idsParaPdf", idsString); // Te vendrá genial para el botón de imprimir este lote

        return "resultado-alta-reposicion";
    }



    @GetMapping("/repo")
    public String mostrarFormularioRepo(Model model) {

        model.addAttribute("repoRequest", new AltaRepoRequest());

        // ¡MUCHO MEJOR! El controlador le pide las temporadas al cerebro del sistema
        model.addAttribute("todasLasTemporadas", stockViewService.obtenerTemporadasDisponibles());
        //model.addAttribute("todosLosProductos", stockViewService.verTodosLosProductos());
        return "alta-reposicion";
    }

    @GetMapping("/exportar-pdf/lote")
    @ResponseBody // <-- Le dice a Spring que devuelva los bytes del archivo directamente
    public ResponseEntity<byte[]> descargarEtiquetasPorLote(@RequestParam("ids") String idsString) {

        return stockViewService.descargarEtiquetasPorLote(idsString);

    }

    @GetMapping("/exportar-pdf/ultimo")
    @ResponseBody // <--- ¡Esta línea le dice a Spring que devuelva los bytes del PDF y no busque un HTML!
    public ResponseEntity<byte[]> descargarEtiquetasUltimoProducto() {

        return stockViewService.crearEtiquetasUltimoProducto();

    }

    @PostMapping("/guardar-repo")
    public String guardarRepo(@ModelAttribute("repoRequest") AltaRepoRequest request, RedirectAttributes redirectAttributes, Model model) {
        try {
            // 1. Registramos y recuperamos las cajas del lote
            List<CajaStock> cajasNuevas = stockViewService.registrarRepoStock(request);

            // 2. Convertimos los IDs a un String separado por comas: "24,25,26"
            String idsString = cajasNuevas.stream()
                    .map(c -> String.valueOf(c.getId()))
                    .collect(Collectors.joining(","));

            // 3. Pasamos los IDs como parámetro seguro al redirect (viajará en la URL)
            redirectAttributes.addAttribute("ids", idsString);

            return "redirect:/stock/ver-ultima-repo";
        } catch (Exception e) {
            /*model.addAttribute("error", e.getMessage());
            model.addAttribute("todosLosProductos", stockViewService.verTodosLosProductos());
            return "alta-reposicion";*/
            // 1. Inyectamos el mensaje de error capturado
            model.addAttribute("error", e.getMessage());

            // 2. RECARGAMOS los desplegables de la vista de reposición
            model.addAttribute("todasLasTemporadas", stockViewService.obtenerTemporadasDisponibles());
            // Si la vista utiliza productos en algún desplegable, mantenemos también esta línea:
            model.addAttribute("todosLosProductos", stockViewService.verTodosLosProductos());

            // 3. Devolvemos el request para conservar la información rellenada por el usuario
            model.addAttribute("repoRequest", request);

            // 4. Retornamos la plantilla HTML de alta de reposición
            return "alta-reposicion";
        }


        }



}
