package com.tienda.GestionStock.service;


import com.tienda.GestionStock.dto.ProductoRequest;
import com.tienda.GestionStock.model.*;
import com.tienda.GestionStock.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductoViewService {

    private final CajaStockRepository cajaStockRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final ProductoRepository productoRepository;
    private final TallaRepository tallaRepository;
    private final MarcaRepository marcaRepository; // <-- 1. Inyectamos el nuevo repositorio

    public ProductoViewService(CajaStockRepository cajaStockRepository, VarianteProductoRepository varianteProductoRepository, ProductoRepository productoRepository, TallaRepository tallaRepository, MarcaRepository marcaRepository) {
        this.cajaStockRepository = cajaStockRepository;
        this.varianteProductoRepository = varianteProductoRepository;
        this.productoRepository = productoRepository;
        this.tallaRepository = tallaRepository;
        this.marcaRepository = marcaRepository;
    }

    @Transactional
    public void registrarProducto(ProductoRequest request) {

        // 2. Buscamos la marca en la BBDD por su ID
        Marca marca = marcaRepository.findById(request.getMarcaId())
                .orElseThrow(() -> new IllegalArgumentException("La marca seleccionada no existe en el sistema."));

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setMarca(marca);
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioBase(request.getPrecioBase());
        producto.setCategoria(request.getCategoria());
        producto.setColor(request.getColor());
        producto.setTemporada(request.getTemporada());

        Genero generoEnum = Genero.valueOf(request.getGenero().toUpperCase());
        producto.setGenero(generoEnum);

        producto = productoRepository.save(producto);


        // --- NUEVO MOTOR DE SKU CON COLOR ---
        // Limpiamos espacios en blanco
        String limpiaMarca = producto.getMarca().getNombre().replaceAll("\\s+", "").toUpperCase();
        String limpiaNombre = producto.getNombre().replaceAll("\\s+", "").toUpperCase();
        String limpiaColor = producto.getColor().replaceAll("\\s+", "").toUpperCase();

        // Extraemos las subcadenas de forma segura usando Math.min por si el nombre es más corto

        String prefijoMarca = limpiaMarca.substring(0, Math.min(limpiaMarca.length(), 4));
        String prefijoNombre = limpiaNombre.substring(0, Math.min(limpiaNombre.length(), 4));
        String prefijoColor = limpiaColor.substring(0, Math.min(limpiaColor.length(), 4));
/*
        String prefijoMarca = producto.getMarca().getNombre().replaceAll("\\s+", "").toUpperCase();
        String prefijoNombre = producto.getNombre().replaceAll("\\s+", "").toUpperCase();
        String prefijoColor = producto.getColor().replaceAll("\\s+", "").toUpperCase();
*/

        // El prefijo ahora será, por ejemplo: MUN-COP-AZ (Munich Copa Azul)
        String prefijoAutomatico = prefijoMarca + "-" + prefijoNombre + "-" + prefijoColor;
        // -------------------------------------
        // --------------------------------

        if(request.getRepoTexto()!= null && !request.getRepoTexto().trim().isEmpty()){

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
                    variante = varianteProductoRepository.save(variante);
                }

                // 5. Creamos tantas cajas físicas como indique la cantidad, con un QR único correlativo
                for (int i = 1; i <= cantidadPares; i++) {
                    CajaStock caja = new CajaStock();
                    caja.setVariante(variante);
                    // El QR será tipo: QR-NIKE-AIR-38-123456 (usamos milisegundos para que sea único en la tienda)
                    caja.setQrCodigoUnico("QR-" + skuGenerado + "-" + System.nanoTime());
                    caja.setUbicacionAlmacen(request.getUbicacionAlmacen());
                    caja.setEstado(caja.DISPONIBLE);

                    cajaStockRepository.save(caja);
                }

        }



        }
    }



}
