package com.proyecto06.Controlador;

import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.UbicacionRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.proyecto06.Modelo.Categoria;
import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Ubicacion;

import jakarta.servlet.http.HttpSession;

@Controller
public class controladorInventario {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/inventario")
    public String irAlInventario(HttpSession session, Model model) {
        // 1. Validar si existe sesión
        if (session.getAttribute("nombre") == null) {
            return "redirect:/";
        }

        // 2. Validar rol
        Integer rol = (Integer) session.getAttribute("rol");
        if (rol != 0 && rol != 1) {
            return "redirect:/alertaProducto";
        }

        // 1. Obtener listas
        List<Producto> productos = productoRepository.findAll();
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", categorias);
        // 2. Calcular los totales
        long totalLotes = productos.size();
        long bajoStock = productos.stream()
                .filter(p -> "Bajo stock".equalsIgnoreCase(p.getEstado()))
                .count();
        long ubicacionesActivas = productos.stream()
                .map(Producto::getIdUbicacion)
                .distinct()
                .count();

        // 3. Agregar al modelo
        model.addAttribute("listaProductos", productos);
        model.addAttribute("listaUbicaciones", ubicaciones);
        model.addAttribute("totalLotes", totalLotes);
        model.addAttribute("bajoStock", bajoStock);
        model.addAttribute("ubicacionesActivas", ubicacionesActivas);

        return "inventario";
    }

    @PostMapping("/guardarInventario")
    public String guardarInventario(@ModelAttribute Producto productoEditado) {

        // 1. Si ID es 0, lo tratamos como nulo para indicar "Nuevo registro"
        if (productoEditado.getIdProducto() != null && productoEditado.getIdProducto() == 0) {
            productoEditado.setIdProducto(null);
        }

        Producto productoGuardar;

        // 2. Si ID es null, creamos objeto nuevo; si tiene ID, buscamos el existente
        if (productoEditado.getIdProducto() == null) {
            productoGuardar = productoEditado;
            productoGuardar.setFechaIngreso(java.time.LocalDate.now()); // Solo al crear
        } else {
            productoGuardar = productoRepository.findById(productoEditado.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("ID no encontrado"));

            // Actualizamos los campos del objeto existente
            productoGuardar.setNombreProducto(productoEditado.getNombreProducto());
            productoGuardar.setCodigoBarras(productoEditado.getCodigoBarras());
            productoGuardar.setDescripcion(productoEditado.getDescripcion());
            productoGuardar.setLote(productoEditado.getLote());
            productoGuardar.setLaboratorio(productoEditado.getLaboratorio());
            productoGuardar.setRegistroSanitario(productoEditado.getRegistroSanitario());
            productoGuardar.setUnidadMedida(productoEditado.getUnidadMedida());
            productoGuardar.setPrecio(productoEditado.getPrecio());
            productoGuardar.setCantidad(productoEditado.getCantidad());
            productoGuardar.setFechaVencimiento(productoEditado.getFechaVencimiento());
            productoGuardar.setEstado(productoEditado.getEstado());
            productoGuardar.setIdUbicacion(productoEditado.getIdUbicacion());
        }

        // 3. Manejo de Categoría (Relación)
        if (productoEditado.getCategoria() != null && productoEditado.getCategoria().getIdCategoria() != null) {
            Categoria cat = categoriaRepository.findById(productoEditado.getCategoria().getIdCategoria()).orElse(null);
            productoGuardar.setCategoria(cat);
        }

        // REGLA DE NEGOCIO AUTOMÁTICA
        if (productoGuardar.getCantidad() < 10) {
            productoGuardar.setEstado("Bajo stock");
        } else {
            productoGuardar.setEstado("Activo");
        }

        productoRepository.save(productoGuardar);
        return "redirect:/inventario";
    }

    // Asegúrate de que el path sea el mismo que usas en el HTML
    @GetMapping("/eliminarInventario/{id}")
    public String eliminarInventario(@PathVariable("id") Integer id) {
        productoRepository.deleteById(id);
        return "redirect:/inventario";
    }

    @GetMapping("/cerrarSesion")
    public String irACerrarSesion(HttpSession session) {
        // Aquí NO invalidamos la sesión todavía,
        // para que la página de cierre pueda mostrar el nombre del usuario
        return "cerrarSesion";
    }
}
