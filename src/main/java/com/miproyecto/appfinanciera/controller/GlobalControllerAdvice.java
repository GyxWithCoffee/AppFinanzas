package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.dto.NoticiaDto;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.service.NoticiasService;
import com.miproyecto.appfinanciera.service.UsuarioService;
import com.miproyecto.appfinanciera.service.UsuarioDetalles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioService usuarioService;
    private final NoticiasService noticiasService;

    public GlobalControllerAdvice(UsuarioService usuarioService, NoticiasService noticiasService) {
        this.usuarioService = usuarioService;
        this.noticiasService = noticiasService;
    }

    @ModelAttribute
    public void agregarDatosGlobales(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            String email = null;

            if (principal instanceof UsuarioDetalles usuarioDetalles) {
                email = usuarioDetalles.getUsername();
            } else if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
                email = oidcUser.getEmail();
            }

            if (email != null) {
                Usuario usuario = usuarioService.buscarPorEmail(email);
                if (usuario != null) {
                    model.addAttribute("nombre", usuario.getNombre());
                    model.addAttribute("apellido", usuario.getApellido());

                    String iniciales = ("" + usuario.getNombre().charAt(0) +
                            (usuario.getApellido().isEmpty() ? "" : usuario.getApellido().charAt(0))).toUpperCase();
                    model.addAttribute("iniciales", iniciales);
                }
            }
        }

        NoticiaDto ultima = noticiasService.obtenerUltimaNoticia();
        model.addAttribute("ultimaNoticia", ultima);
    }
}
