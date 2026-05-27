<<<<<<< HEAD
package com.proyecto06.Controlador;
=======
package com.proyecto06;
>>>>>>> 44aa0f066d2b0448dcc41f2fce695f99c1b446ca

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controladorInventario {

    @GetMapping("/inventario")
    public String inventario() {
        return "html/inventario";
    }

}
