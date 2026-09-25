package com.tienda.GestionStock.controller;


import com.tienda.GestionStock.dto.ProductoRequest;
import com.tienda.GestionStock.repository.MarcaRepository;
import com.tienda.GestionStock.repository.ProductoRepository;
import com.tienda.GestionStock.service.ProductoViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class ProductoViewController {

    private final ProductoRepository productoRepository;
    private final ProductoViewService productoViewService;

    @Autowired
    private MarcaRepository marcaRepository; // Inyectamos el repositorio en el controlador de vistas

    public ProductoViewController(ProductoRepository productoRepository, ProductoViewService productoViewService,
                                  MarcaRepository marcaRepository) {
        this.productoRepository = productoRepository;
        this.productoViewService = productoViewService;
        this.marcaRepository = marcaRepository;
    }

    /**
     * Muestra el formulario para crear un producto.
     * URL: GET http://localhost:8080/productos/nuevo
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioProducto(Model model) {

        
        model.addAttribute("productoRequest", new ProductoRequest());

        // Pasamos la lista de todas las marcas registradas para rellenar el desplegable HTML
        model.addAttribute("todasLasMarcas", marcaRepository.findAllByOrderByNombreAsc());


        return "alta-producto";
    }


    @PostMapping("/guardar-producto")
    public String guardarProcducto(@ModelAttribute("productoRequest") ProductoRequest request, Model model) {
        try {
            productoViewService.registrarProducto(request);

            /*TODO Aquí tengo que redirigir a una pantalla con los productos acabados de introducir
            para sacar el pdf*/
            return "redirect:/stock/ver-ultimo-introducido";
        } catch (Exception e) {
            /*model.addAttribute("error", e.getMessage());
            model.addAttribute("todosLosProductos", productoRepository.findAll());
            return "alta-producto";*/

            // 1. Pasamos el mensaje de error para mostrarlo en la vista
            model.addAttribute("error", e.getMessage());

            // 2. RECARGAMOS el desplegable de marcas (ordenadas como en el GET)
            model.addAttribute("todasLasMarcas", marcaRepository.findAllByOrderByNombreAsc());

            // 3. Volvemos a inyectar el request recibido para conservar los campos que ya rellenó el usuario
            model.addAttribute("productoRequest", request);

            // 4. Retornamos a la misma vista de alta
            return "alta-producto";

        }
    }




}
