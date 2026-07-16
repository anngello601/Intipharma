package com.proyecto06.Controlador;

import com.proyecto06.Modelo.Categoria;
import com.proyecto06.Modelo.Producto;

import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.RolRepository;
import com.proyecto06.Repository.UbicacionRepository;
import com.proyecto06.Repository.UsuarioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorInicio {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepo;

    // Sin @Autowired, Spring lo detecta solo por ser el único constructor
    public ControladorInicio(CategoriaRepository categoriaRepository, 
                             ProductoRepository productoRepository,
                             UsuarioRepository usuarioRepo,
                             RolRepository rolRepo,
                             UbicacionRepository ubicacionRepo) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.ubicacionRepo = ubicacionRepo;
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
        model.addAttribute("listaUbicaciones", ubicacionRepo.findAll());

        return "productos";
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(@ModelAttribute Producto productoEditado) {
        if (productoEditado.getIdProducto() != null && productoEditado.getIdProducto() == 0) {
            productoEditado.setIdProducto(null);
        }

        if (productoEditado.getIdProducto() == null) {
            productoEditado.setFechaIngreso(java.time.LocalDate.now());
            productoRepository.save(productoEditado);
        } else {
            Producto p = productoRepository.findById(productoEditado.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("ID no encontrado"));

            // ACTUALIZA TODOS LOS CAMPOS
            p.setNombreProducto(productoEditado.getNombreProducto());
            p.setDescripcion(productoEditado.getDescripcion());
            p.setCodigoBarras(productoEditado.getCodigoBarras());
            p.setLote(productoEditado.getLote());
            p.setLaboratorio(productoEditado.getLaboratorio());
            p.setRegistroSanitario(productoEditado.getRegistroSanitario());
            p.setPrecio(productoEditado.getPrecio());
            p.setCantidad(productoEditado.getCantidad());
            p.setUnidadMedida(productoEditado.getUnidadMedida());
            p.setFechaVencimiento(productoEditado.getFechaVencimiento());
            p.setEstado(productoEditado.getEstado());
            p.setIdUbicacion(productoEditado.getIdUbicacion()); // <--- AHORA SÍ SE GUARDARÁ

            // Manejo de Categoría
            if (productoEditado.getCategoria() != null && productoEditado.getCategoria().getIdCategoria() != null) {
                Categoria cat = categoriaRepository.findById(productoEditado.getCategoria().getIdCategoria())
                        .orElse(null);
                p.setCategoria(cat);
            }

            productoRepository.save(p);
        }
        return "redirect:/productos";
    }

    @GetMapping("/eliminarProducto/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        // Esto borra el producto
        productoRepository.deleteById(id);
        return "redirect:/productos"; // Recarga la página después de borrar
    }
}