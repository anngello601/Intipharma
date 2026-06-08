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
import org.springframework.web.bind.annotation.ResponseBody;

import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorLogin {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/alertaProducto")
    public String alerta(HttpSession session, Model model) {

        model.addAttribute("usuario", session.getAttribute("usuario"));

        return "alertaProducto";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        System.out.println("POST LOGIN EJECUTADO");
        System.out.println("USER: " + username);
        System.out.println("PASS: " + password);

        Optional<Usuario> usuario = usuarioRepo.findByUsernameAndPassword(username, password);

        System.out.println("EXISTE EN BD: " + usuario.isPresent());

        if (usuario.isPresent()) {
            session.setAttribute("usuario", usuario.get());

            // 👉 LOGIN OK → manda a alerta
            return "redirect:/alertaProducto";
        }

        // ❌ LOGIN FAIL → vuelve al login
        return "redirect:/login?error=true";
    }

    @GetMapping("/register")
    public String mostrarRegistro() {
        return "Register";
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Usuario u) {
        usuarioRepo.save(u);
        return "redirect:/login";
    }

    @GetMapping("/test")
    public String test(HttpSession session, Model model) {

        model.addAttribute("usuario", session.getAttribute("usuario"));

        return "test";
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDB() {

        List<Usuario> lista = usuarioRepo.findAll();

        return "Total usuarios: " + lista.size();
    }
}
