package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.dto.NoticiaDto;
import com.miproyecto.appfinanciera.service.NoticiasService;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.service.CustomOAuth2User;
import com.miproyecto.appfinanciera.service.UsuarioDetalles;
import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.security.core.Authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NoticiasService noticiasService;

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String error,
                        @RequestParam(required = false) String registrado) {

        if (logout != null) {
            model.addAttribute("mensajeLogout", "Sesión cerrada con éxito.");
        }

        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales incorrectas.");
        }

        if (registrado != null) {
            model.addAttribute("mensajeRegistro", "¡Registro exitoso! Ya puedes iniciar sesión.");
        }

        return "login"; // login.html
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


    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/login"; // o "redirect:/login" si quieres que vaya al login primero
    }
}
