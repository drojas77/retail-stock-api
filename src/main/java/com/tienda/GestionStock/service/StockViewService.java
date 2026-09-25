package com.tienda.GestionStock.service;


import com.tienda.GestionStock.dto.AltaRepoRequest;
import com.tienda.GestionStock.dto.CajaDTO;
import com.tienda.GestionStock.model.CajaStock;
import com.tienda.GestionStock.model.Producto;
import com.tienda.GestionStock.model.Talla;
import com.tienda.GestionStock.model.VarianteProducto;
import com.tienda.GestionStock.repository.*;
import org.springframework.data.domain.Page;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockViewService implements IStockService{

    private final CajaStockRepository cajaStockRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final ProductoRepository productoRepository;
    private final TallaRepository tallaRepository;
    private final MarcaRepository marcaRepository;
    private PdfGeneratorService pdfGeneratorService;


    public StockViewService(CajaStockRepository cajaStockRepository, VarianteProductoRepository varianteProductoRepository, ProductoRepository productoRepository, TallaRepository tallaRepository, MarcaRepository marcaRepository, PdfGeneratorService pdfGeneratorService) {
        this.cajaStockRepository = cajaStockRepository;
        this.varianteProductoRepository = varianteProductoRepository;
        this.productoRepository = productoRepository;
        this.tallaRepository = tallaRepository;
        this.marcaRepository = marcaRepository;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public Page<CajaDTO> obtenerCajasPaginadas(String estado, Long marcaId, Pageable pageable) {

        Page<CajaStock> cajasPage = cajaStockRepository.buscarCajasPaginadas(estado, marcaId, pageable);

        // Mapeamos las entidades CajaStock a CajaDTO conservando la paginación de Spring Data
        return cajasPage.map(caja -> new CajaDTO(
                caja.getId(),
                caja.getQrCodigoUnico(),
                caja.getVariante().getProducto().getMarca().getNombre(),
                caja.getVariante().getProducto().getNombre(),
                caja.getVariante().getProducto().getColor(),
                caja.getVariante().getTalla().getNumero(),
                caja.getUbicacionAlmacen(),
                caja.getEstado()
        ));
    }

    @Transactional
    @Override
    public ArrayList<CajaStock> registrarRepoStock(AltaRepoRequest request) {

        Producto producto = productoRepository.findById(Long.valueOf(request.getProductoId()))
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        // --- NUEVO MOTOR DE SKU CON COLOR ---
        // Limpiamos espacios en blanco
        String limpiaMarca = producto.getMarca().getNombre().replaceAll("\\s+", "").toUpperCase();
        String limpiaNombre = producto.getNombre().replaceAll("\\s+", "").toUpperCase();
        String limpiaColor = producto.getColor().replaceAll("\\s+", "").toUpperCase();


        // Extraemos las subcadenas de forma segura usando Math.min por si el nombre es más corto
        String prefijoMarca = limpiaMarca.substring(0, Math.min(limpiaMarca.length(), 4));
        String prefijoNombre = limpiaNombre.substring(0, Math.min(limpiaNombre.length(), 4));
        String prefijoColor = limpiaColor.substring(0, Math.min(limpiaColor.length(), 4));

        // El prefijo ahora será, por ejemplo: MUN-COP-AZ (Munich Copa Azul)
        String prefijoAutomatico = prefijoMarca + "-" + prefijoNombre + "-" + prefijoColor;
        // -------------------------------------
        // --------------------------------

        // Lista temporal para capturar SOLAMENTE las cajas creadas en esta entrada
        java.util.ArrayList<CajaStock> cajasCreadasEnEsteLote = new java.util.ArrayList<>();

        // 1. Troceamos la cadena por comas: "38-2", "39-4"
        String[] bloques = request.getRepoTexto().trim().split("\\s+");

        for (String bloque : bloques) {
            // Troceamos cada bloque por el guion: ["38", "2"]
            String[] partes = bloque.trim().split("/");
            if (partes.length != 2) continue;

            Integer numeroTalla = Integer.parseInt(partes[0].trim());
            int cantidadPares = Integer.parseInt(partes[1].trim());

            // 2. Buscamos la talla en la BBDD por su número (ej.: 38)
            Talla talla = tallaRepository.findByNumero(numeroTalla)
                    .orElseThrow(() -> new IllegalArgumentException("La talla " + numeroTalla + " no está configurada en el sistema."));

            // 3. Fabricamos el SKU automático para esa talla (Ej.: NIKE-AIR-38)
            //String skuGenerado = request.getPrefijoCodigo().toUpperCase() + "-" + numeroTalla;

            // Fabricamos el SKU combinando el prefijo automático con la talla (Ej.: ADI-SUP-38)
            String skuGenerado = prefijoAutomatico + "-" + numeroTalla;

            // 4. Si la variante (SKU) no existe en la BBDD, ¡la creamos nosotros solos de golpe!
            VarianteProducto variante = varianteProductoRepository.findBySkuModelo(skuGenerado)
                    .orElse(null);

            if (variante == null) {
                variante = new VarianteProducto();
                variante.setProducto(producto);
                variante.setTalla(talla);
                variante.setSkuModelo(skuGenerado);
                varianteProductoRepository.save(variante);
            }

            // 5. Creamos tantas cajas físicas como indique la cantidad, con un QR único correlativo
            for (int i = 1; i <= cantidadPares; i++) {
                CajaStock caja = new CajaStock();
                caja.setVariante(variante);
                // El QR será tipo: QR-NIKE-AIR-38-123456 (usamos milisegundos para que sea único en la tienda)
                // 1. Generamos el DNI único con su marca de tiempo exacta en nanosegundos
                String codigoUnicoLargo = "QR-" + skuGenerado + "-" + System.nanoTime()+i;
                caja.setQrCodigoUnico(codigoUnicoLargo);
                caja.setUbicacionAlmacen(request.getUbicacionAlmacen());
                caja.setEstado(caja.DISPONIBLE);

                cajaStockRepository.save(caja);
                cajasCreadasEnEsteLote.add(caja); // La metemos en la lista
            }

        }

        // Al final, devolvemos únicamente las cajas de esta repo
        return cajasCreadasEnEsteLote;
    }

    public List<CajaStock> verInventario() {
        // 1. Buscamos todas las cajas físicas que hay en la base de datos
        return cajaStockRepository.findAll();

        // 2. Metemos la lista en el "Model" de Spring para que Thymeleaf pueda leerla
        //model.addAttribute("listaCajas", todasLasCajas);



    }

    public List<CajaStock> verUltimoModeloIntroducido() {

        Producto ultimoProducto = productoRepository.findFirstByOrderByIdDesc();
        List<CajaStock> cajasCreadas = cajaStockRepository.findByVarianteProductoId(ultimoProducto.getId());
        // 2. Metemos la lista en el "Model" de Spring para que Thymeleaf pueda leerla
        //model.addAttribute("listaCajas", cajasCreadas);

        return cajasCreadas;

    }

    public ResponseEntity<byte[]> crearEtiquetasUltimoProducto() {

        //TODO poner el código del zapato, el id en el QR
        // 1. Busca el último zapato guardado
        Producto ultimoProducto = productoRepository.findFirstByOrderByIdDesc();
        if (ultimoProducto == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // 2. Busca las cajas de ese zapato
        List<CajaStock> cajasCreadas = cajaStockRepository.findByVarianteProductoId(ultimoProducto.getId());
        if (cajasCreadas.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // 3. Llama al servicio que picamos antes para dibujar el PDF de etiquetas dobles
        byte[] pdfContenido = pdfGeneratorService.generarPdfEtiquetasDobles(cajasCreadas);

        // 4. Configura las cabeceras de descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nombreArchivo = "etiquetas-" + ultimoProducto.getMarca().getNombre().replaceAll("\\s+", "") + ".pdf";
        headers.setContentDispositionFormData("attachment", nombreArchivo);

        // 5. Devuelve el archivo
        return new ResponseEntity<>(pdfContenido, headers, HttpStatus.OK);
    }

    public List<String> obtenerTemporadasDisponibles() {
        return productoRepository.findDistinctTemporadas();
    }

    public List<Producto> verTodosLosProductos() {

        return productoRepository.findAll();
    }

    public List<CajaStock> verUltimaRepoIntroducida() {

        Producto ultimoProducto = productoRepository.findFirstByOrderByIdDesc();
        List<CajaStock> cajasCreadas = cajaStockRepository.findByVarianteProductoId(ultimoProducto.getId());
        // 2. Metemos la lista en el "Model" de Spring para que Thymeleaf pueda leerla
        //model.addAttribute("listaCajas", cajasCreadas);

        return cajasCreadas;
    }

    public List<CajaStock> buscarCajasPorIds(List<Long> ids) {
        return cajaStockRepository.findAllById(ids);
    }

    public ResponseEntity<byte[]> descargarEtiquetasPorLote(String idsString) {


        // 1. Convertimos el String "24,25,26" en una lista de Longs Java
        List<Long> ids = java.util.Arrays.stream(idsString.split(","))
                .map(Long::valueOf)
                .collect(java.util.stream.Collectors.toList());

        // 2. Buscamos en la base de datos única y exclusivamente esas cajas del lote
        List<CajaStock> cajasDelLote = this.buscarCajasPorIds(ids);

        if (cajasDelLote.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // 3. Conseguimos los datos del producto (para el nombre del archivo PDF)
        Producto producto = cajasDelLote.get(0).getVariante().getProducto();

        // 4. Llamamos a tu servicio para dibujar el PDF pasándole SOLO este lote de cajas
        byte[] pdfContenido = pdfGeneratorService.generarPdfEtiquetasDobles(cajasDelLote);

        // 5. Configuramos las cabeceras de descarga del navegador
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String nombreArchivo = "etiquetas-reposicion-" + producto.getMarca().getNombre().replaceAll("\\s+", "") + ".pdf";
        headers.setContentDispositionFormData("attachment", nombreArchivo);

        // 6. Escupimos los bytes del PDF
        return new ResponseEntity<>(pdfContenido, headers, HttpStatus.OK);


    }

    public List<CajaStock> verUltimaLoteIntroducido(String idsString) {

        // 1. Convertimos el String "24,25,26" de vuelta a una lista de Longs Java
        List<Long> ids = java.util.Arrays.stream(idsString.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 2. Buscamos en la BBDD única y exclusivamente esas cajas
        List<CajaStock> cajasRecientes = this.buscarCajasPorIds(ids);

        return cajasRecientes;
    }

    public List<CajaStock> verCajasPorEstado(String estado) {

        return cajaStockRepository.findByEstadoIgnoreCaseOrderByIdDesc(estado);
    }

    public @Nullable Object obtenerTodasMarcas() {

        return marcaRepository.findAll();
    }

    public List<CajaStock> verCajasPorMarca(String marca) {

        Long id_marca = Long.valueOf(marca);
        return  cajaStockRepository.findByVarianteProductoMarcaId(id_marca);
    }

    /**
     * Comprueba si existe stock registrado para un código QR determinado.
     * @param qrCodigoUnico El código QR a buscar.
     * @return true si existe en la base de datos, false en caso contrario.
     */
    public boolean existeStock(String qrCodigoUnico) {
        if (qrCodigoUnico == null || qrCodigoUnico.trim().isEmpty()) {
            return false;
        }
        return cajaStockRepository.existsByQrCodigoUnico(qrCodigoUnico);
    }
}
