package com.proyecto06.Controlador;

import com.proyecto06.Modelo.Categoria;
import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Rol;
import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.UbicacionRepository;
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
    @Autowired
    private com.proyecto06.Repository.RolRepository rolRepo;

    @Autowired
    private UbicacionRepository ubicacionRepo;

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
        model.addAttribute("roles", rolRepo.findAll());
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
        Usuario usuarioExistente = usuarioRepo.findById(usuario.getIdUsuario()).orElse(null);

        if (usuarioExistente != null) {
            usuarioExistente.setDni(usuario.getDni());
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setCorreo(usuario.getCorreo());

            // Actualizar el Rol
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                Rol nuevoRol = rolRepo.findById(usuario.getRol().getIdRol()).orElse(null);
                usuarioExistente.setRol(nuevoRol);
            }

            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuario.getPassword());
            }

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