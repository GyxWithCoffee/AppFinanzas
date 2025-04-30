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
            if (!nombre.isBlank())
                iniciales += nombre.charAt(0);
            if (!apellido.isBlank())
                iniciales += apellido.charAt(0);

            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("iniciales", iniciales.toUpperCase());
        } else {
            model.addAttribute("nombre", "Invitado");
            model.addAttribute("apellido", "");
            model.addAttribute("iniciales", "IV");
        }

        List<NoticiaDto> noticias = noticiasService.obtenerNoticias();
        if (noticias.isEmpty()) {
            System.out.println("No se encontraron noticias.");
        } else {
            System.out.println("Se encontraron " + noticias.size() + " noticias.");
        }
    
        model.addAttribute("noticias", noticias);

        return "dashboard";
    }

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/login"; // o "redirect:/login" si quieres que vaya al login primero
    }
}
