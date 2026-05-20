package com.proyecto06.Controlador;
//libreria
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {    
    @GetMapping("/index")
    public String dashboard(){
        return "html/index";
    }    
}//fin de la clase
