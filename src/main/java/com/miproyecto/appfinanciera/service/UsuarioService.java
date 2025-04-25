package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.model.Rol;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.RolRepository;
import com.miproyecto.appfinanciera.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrarNuevo(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Rol rolUsuario = rolRepo.findByNombre("ROLE_USER");
        usuario.setRoles(Collections.singleton(rolUsuario));
        return usuarioRepo.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        Optional<Usuario> usuario = usuarioRepo.findByEmail(email);
        return usuario.orElse(null);
    }

    public Usuario obtenerUsuarioPorAuthentication(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        return email != null ? buscarPorEmail(email) : null;
    }
}
