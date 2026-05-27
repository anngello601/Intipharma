package com.proyecto06.Controlador;
//librerias
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorLogin {
    
    @GetMapping("/login")
    public String PaginaInicio(){
        return "html/login";
    }
}
