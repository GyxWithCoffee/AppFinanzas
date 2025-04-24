package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.service.CustomOAuth2User;
import com.miproyecto.appfinanciera.service.UsuarioDetalles;
import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario) {
        usuarioService.registrarNuevo(usuario);
        return "redirect:/login?registroExitoso";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            String nombre = "";
            String apellido = "";

            if (principal instanceof CustomOAuth2User customUser) {
                nombre = customUser.getUsuario().getNombre();
                apellido = customUser.getUsuario().getApellido();
            } else if (principal instanceof UsuarioDetalles userDetails) {
                nombre = userDetails.getUsuario().getNombre();
                apellido = userDetails.getUsuario().getApellido();
            }

            String iniciales = "";
            if (!nombre.isBlank()) iniciales += nombre.charAt(0);
            if (!apellido.isBlank()) iniciales += apellido.charAt(0);

            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("iniciales", iniciales.toUpperCase());
        } else {
            model.addAttribute("nombre", "Invitado");
            model.addAttribute("apellido", "");
            model.addAttribute("iniciales", "IV");
        }

        return "dashboard";
    }

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/login"; // o "redirect:/login" si quieres que vaya al login primero
    }
}
