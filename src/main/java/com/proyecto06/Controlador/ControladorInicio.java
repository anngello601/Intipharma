package com.proyecto06.Controlador;

import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UsuarioRepository usuarioRepo;

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
    public String irAConfiguracion(HttpSession session, HttpServletRequest request, Model model) {
        Integer rol = (Integer) session.getAttribute("rol");

        // 1. Validar acceso (si no es admin, redirigir)
        if (rol == null || rol != 0) {
            String referer = request.getHeader("Referer");
            return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/alertaProducto";
        }

        // 2. Si es admin, cargar datos para la vista (esto es código alcanzable)
        model.addAttribute("usuarios", usuarioRepo.findAll());

        // 3. Retornar la vista
        return "configuracion";
    }

    @GetMapping("/configuracion/editar/{id}")
    public String editarUsuarioForm(@PathVariable Integer id, Model model) {
        // Buscamos al usuario; si no existe, redirigimos a la lista con un error
        return usuarioRepo.findById(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    return "editarUsuario";
                })
                .orElse("redirect:/configuracion?error=noEncontrado");
    }

    @PostMapping("/configuracion/editar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario) {
        // 1. Buscamos al usuario real en la base de datos por su ID
        Usuario usuarioExistente = usuarioRepo.findById(usuario.getIdUsuario()).orElse(null);

        if (usuarioExistente != null) {
            // 2. Actualizamos los datos básicos
            usuarioExistente.setDni(usuario.getDni());
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCorreo(usuario.getCorreo());

            // 3. SOLO actualizamos la contraseña si el usuario escribió algo nuevo
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuario.getPassword()); // Aquí deberías aplicar BCrypt
            }

            // 4. Guardamos
            usuarioRepo.save(usuarioExistente);
        }

        return "redirect:/configuracion";
    }

    @PostMapping("/configuracion/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuarioRepo.deleteById(id);
        return "redirect:/configuracion";
    }

    @PostMapping("/configuracion/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        // Si el objeto usuario trae el ID, JPA hará un UPDATE en lugar de INSERT
        usuarioRepo.save(usuario);
        return "redirect:/configuracion";
    }
}