package com.proyecto06.Controlador;

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

import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Ubicacion;

import jakarta.servlet.http.HttpSession;

@Controller
public class controladorInventario {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

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

        // 2. Calcular los totales
        long totalLotes = productos.size();
        long bajoStock = productos.stream().filter(p -> p.getCantidad() < 10).count(); // Ajusta el límite (ej. < 10)
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
    public String guardarInventario(@ModelAttribute Producto producto) {
        // Esto guarda los cambios hechos en el modal
        productoRepository.save(producto);
        return "redirect:/inventario"; // Recarga la página después de guardar
    }

    @GetMapping("/eliminarInventario/{id}")
    public String eliminarInventario(@PathVariable("id") Integer id) {
        // Esto borra el producto
        productoRepository.deleteById(id);
        return "redirect:/inventario"; // Recarga la página después de borrar
    }

    @GetMapping("/cerrarSesion")
    public String irACerrarSesion(HttpSession session) {
        // Aquí NO invalidamos la sesión todavía,
        // para que la página de cierre pueda mostrar el nombre del usuario
        return "cerrarSesion";
    }
}
