package com.proyecto06.Controlador;

import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Modelo.Rol;
import com.proyecto06.Repository.UsuarioRepository;
import com.proyecto06.Repository.RolRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/configuracion")
public class ControladorConfiguracion {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;

    public ControladorConfiguracion(UsuarioRepository usuarioRepo, RolRepository rolRepo) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
    }

    // Página principal
    @GetMapping
    public String mostrarConfiguracion(HttpSession session, HttpServletRequest request, Model model) {
        Integer rol = (Integer) session.getAttribute("rol");
        if (rol == null || rol != 0) {
            String referer = request.getHeader("Referer");
            return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/alertaProducto";
        }
        model.addAttribute("usuarios", usuarioRepo.findAll());
        model.addAttribute("roles", rolRepo.findAll());
        return "configuracion";
    }

    // Editar usuario (GET) - si se usa una vista separada
    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Integer id, Model model) {
        return usuarioRepo.findById(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    return "editarUsuario";
                })
                .orElse("redirect:/configuracion?error=noEncontrado");
    }

    // Actualizar usuario (POST)
    @PostMapping("/editar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {
        Usuario existente = usuarioRepo.findById(usuario.getIdUsuario()).orElse(null);
        if (existente != null) {
            existente.setDni(usuario.getDni());
            existente.setNombre(usuario.getNombre());
            existente.setApellido(usuario.getApellido());
            existente.setCorreo(usuario.getCorreo());
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                Rol nuevoRol = rolRepo.findById(usuario.getRol().getIdRol()).orElse(null);
                existente.setRol(nuevoRol);
            }
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                existente.setPassword(usuario.getPassword());
            }
            usuarioRepo.save(existente);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario actualizado");
        } else {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
        }
        return "redirect:/configuracion";
    }

    // Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        try {
            usuarioRepo.deleteById(id);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar");
        }
        return "redirect:/configuracion";
    }

    // Guardar nuevo usuario (si se usa)
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {
        try {
            usuarioRepo.save(usuario);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario guardado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al guardar");
        }
        return "redirect:/configuracion";
    }

    // ========== NUEVAS FUNCIONALIDADES ==========

    // Exportar usuarios a Excel (simulado)
    @GetMapping("/exportar-excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx");
        // Aquí iría la lógica real con Apache POI
        response.getOutputStream().println("Exportación de usuarios (simulada)");
        response.getOutputStream().flush();
    }

    // Generar backup (simulado)
    @GetMapping("/backup")
    public String generarBackup(RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje", "Copia de seguridad generada a las " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return "redirect:/configuracion";
    }

    // Guardar configuración de alertas
    @PostMapping("/alertas/guardar")
    public String guardarAlertas(@RequestParam Integer dias,
                                 @RequestParam String prioridad,
                                 RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje",
                "Alertas actualizadas: días=" + dias + ", prioridad=" + prioridad);
        return "redirect:/configuracion";
    }

    // Guardar configuración de correos
    @PostMapping("/correos/guardar")
    public String guardarCorreos(@RequestParam String correo,
                                 @RequestParam(value = "enviarAlertas", defaultValue = "false") boolean enviarAlertas,
                                 RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje",
                "Correos actualizados: " + correo + ", alertas=" + enviarAlertas);
        return "redirect:/configuracion";
    }

    // Cambiar contraseña (usa correo de sesión)
    @PostMapping("/seguridad/cambiar-password")
    public String cambiarPassword(@RequestParam String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {
        String correo = (String) session.getAttribute("correo");
        if (correo != null) {
            Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setPassword(password);
                usuarioRepo.save(usuario);
                redirectAttrs.addFlashAttribute("mensaje", "Contraseña actualizada");
            } else {
                redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "No has iniciado sesión");
        }
        return "redirect:/configuracion";
    }

    // Guardar preferencias de notificaciones
    @PostMapping("/notificaciones/guardar")
    public String guardarNotificaciones(@RequestParam(value = "sistema", defaultValue = "false") boolean sistema,
                                        @RequestParam(value = "sonidos", defaultValue = "false") boolean sonidos,
                                        RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje", "Preferencias de notificaciones actualizadas");
        return "redirect:/configuracion";
    }
}