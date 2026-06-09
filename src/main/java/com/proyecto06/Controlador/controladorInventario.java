package com.proyecto06.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class controladorInventario {

    @GetMapping("/inventario")
    public String irAlInventario(HttpSession session) {
        // 1. Validar si existe sesión (si no, al login)
        if (session.getAttribute("nombre") == null) {
            return "redirect:/";
        }

        // 2. Validar que el usuario tenga permiso (Rol 0 o 1)
        Integer rol = (Integer) session.getAttribute("rol");
        if (rol != 0 && rol != 1) {
            return "redirect:/alertaProducto"; // Si no es admin o asistente, lo mandamos a un lugar seguro
        }

        return "inventario"; // Nombre de tu archivo inventario.html
    }

    @GetMapping("/cerrarSesion")
    public String irACerrarSesion(HttpSession session) {
        // Aquí NO invalidamos la sesión todavía,
        // para que la página de cierre pueda mostrar el nombre del usuario
        return "cerrarSesion";
    }
}
