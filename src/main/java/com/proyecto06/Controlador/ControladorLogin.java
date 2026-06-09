package com.proyecto06.Controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//librerias
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto06.Modelo.Categoria;
import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Rol;
import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Repository.CategoriaRepository;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.RolRepository;
import com.proyecto06.Repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorLogin {

    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ProductoRepository productoRepo;
    @Autowired
    private CategoriaRepository categoriaRepo;

    @GetMapping("/alertaProducto")
    public String listarAlertas(Model model) {
        List<Producto> productos = productoRepo.findAll();
        List<Categoria> categorias = categoriaRepo.findAll(); // Cargamos categorías

        // Cálculos rápidos
        long vencidos = productos.stream().filter(p -> p.getDiasParaVencer() <= 0).count();
        long porVencer = productos.stream().filter(p -> p.getDiasParaVencer() > 0 && p.getDiasParaVencer() <= 30)
                .count();
        long vigentes = productos.stream().filter(p -> p.getDiasParaVencer() > 30).count();

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias); // Enviamos la lista al HTML
        model.addAttribute("vencidos", vencidos);
        model.addAttribute("porVencer", porVencer);
        model.addAttribute("vigentes", vigentes);

        return "alertaProducto";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam("correo") String correo,
            @RequestParam("password") String password,
            HttpSession session, Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreoAndPassword(correo, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Guardamos datos en sesión
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("rol", usuario.getRol().getIdRol()); // 0: Admin, 1: Asistente, 2: Usuario

            return "redirect:/alertaProducto";
        }

        model.addAttribute("error", "Correo o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "Register";
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @Autowired
    private RolRepository rolRepo;

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {

        // 1. Buscamos el rol con ID 2 (Usuario) en la base de datos
        // Si no existe el rol 2, lanzará un error (es bueno para detectar fallos)
        Rol rolPorDefecto = rolRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Error: El Rol 2 no existe en la BD"));

        // 2. Asignamos el objeto rol completo al usuario
        usuario.setRol(rolPorDefecto);

        // 3. Guardamos el usuario
        usuarioRepo.save(usuario);

        return "redirect:/login?registroExitoso";
    }

}
