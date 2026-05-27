package com.proyecto06.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {

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