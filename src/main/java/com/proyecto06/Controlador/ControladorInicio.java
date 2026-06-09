package com.proyecto06.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorInicio {

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
    public String listarProductos(HttpSession session) {
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
        return "productos";
    }

    @GetMapping("/configuracion")
    public String irAConfiguracion(HttpSession session) {
        // 1. Validar que exista una sesión activa
        if (session.getAttribute("nombre") == null) {
            return "redirect:/"; // Si no hay sesión, regresa al login
        }

        // 2. Validar que solo el Administrador (rol 0) pueda entrar
        Integer rol = (Integer) session.getAttribute("rol");
        if (rol == null || rol != 0) {
            return "redirect:/alertaProducto"; // Si no es admin, no puede entrar
        }

        return "configuracion"; // Debe coincidir con tu archivo configuracion.html
    }

}