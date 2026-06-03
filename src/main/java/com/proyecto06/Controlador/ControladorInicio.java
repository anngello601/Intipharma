package com.proyecto06.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {

    // Página inicial
    @GetMapping("/")
    public String login() {
        return "html/login";
    }

    // Dashboard después del login
    @GetMapping("/alertaProducto")
    public String dashboard() {
        return "html/alertaProducto";
    }

    @GetMapping("/productos")
    public String productos() {
        return "html/productos";
    }

    @GetMapping("/configuracion")
    public String configuracion() {
        return "html/configuracion";
    }

}