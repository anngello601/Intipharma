package com.proyecto06.Controlador;

import com.proyecto06.Modelo.Producto;
import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorInicio {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    // Sin @Autowired, Spring lo detecta solo por ser el único constructor
    public ControladorInicio(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    // Página inicial
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/login")
    public String home() {
        return "login";
    }

    @GetMapping("/productos")
    public String listarProductos(HttpSession session, Model model) {
        // 1. Verificar si hay sesión
        if (session.getAttribute("nombre") == null) {
            return "redirect:/";
        }

        // 2. Verificar el rol
        Integer rol = (Integer) session.getAttribute("rol");

        // Si el rol es 2, le impedimos el acceso y lo enviamos a las alertas
        if (rol == 2) {
            return "redirect:/alertaProducto";
        }

        // Si es 0 o 1, dejamos que continúe con la lógica normal
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());

        return "productos";
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(@ModelAttribute Producto producto) {
        // Esto guarda los cambios hechos en el modal
        productoRepository.save(producto);
        return "redirect:/productos"; // Recarga la página después de guardar
    }

    @GetMapping("/eliminarProducto/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        // Esto borra el producto
        productoRepository.deleteById(id);
        return "redirect:/productos"; // Recarga la página después de borrar
    }


@GetMapping("/configuracion")
public String irAConfiguracion(HttpSession session, HttpServletRequest request) {
    Integer rol = (Integer) session.getAttribute("rol");
    
    if (rol != null && rol == 0) {
        return "configuracion";
    } else {
        // Obtenemos la página anterior
        String referer = request.getHeader("Referer");
        
        // Si existe un referer, regresa a él; si no, manda a productos por defecto
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/alertaProducto"; // Si no hay referer, lo mandamos a un lugar seguro
        }
    }
}

}