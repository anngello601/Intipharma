package com.proyecto06.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controladorInventario {

    @GetMapping("/inventario")
    public String inventario() {
        return "html/inventario";
    }

}
