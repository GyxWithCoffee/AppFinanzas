package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.model.Rol;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.RolRepository;
import com.miproyecto.appfinanciera.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public CustomOAuth2UserService() {
        System.out.println("🧠 CustomOAuth2UserService CARGADO");
    }


    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        System.out.println("🚀 Entrando a CustomOAuth2UserService.loadUser");
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(request);

        String email = oauth2User.getAttribute("email");
        String nombreCompleto = oauth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("No se pudo obtener el correo del usuario.");
        }

        String[] partes = nombreCompleto != null ? nombreCompleto.trim().split(" ") : new String[]{"Usuario"};
        String nombre = partes[0];
        String apellido = partes.length > 1 ? String.join(" ", Arrays.copyOfRange(partes, 1, partes.length)) : "";

        Optional<Usuario> existente = usuarioRepo.findByEmail(email);
        Usuario usuario;

        if (existente.isEmpty()) {
            System.out.println("🌱 Usuario de Google nuevo. Registrando...");

            Usuario nuevo = new Usuario();
            nuevo.setEmail(email);
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setContrasena(passwordEncoder.encode("oauth_google"));
            nuevo.setHabilitado(true);
            nuevo.setProveedor("GOOGLE");

            Rol rolUser = rolRepo.findByNombre("ROLE_USER");
            if (rolUser == null) {
                rolUser = new Rol();
                rolUser.setNombre("ROLE_USER");
                rolUser = rolRepo.save(rolUser);
                System.out.println("🔧 Rol creado en DB: " + rolUser.getNombre());
            }

            nuevo.setRoles(Collections.singleton(rolUser));

            try {
                usuario = usuarioRepo.save(nuevo);
                System.out.println("✅ Usuario guardado: " + usuario.getId());
            } catch (Exception e) {
                System.err.println("❌ Error al guardar usuario de Google:");
                e.printStackTrace();
                throw new OAuth2AuthenticationException("No se pudo guardar el usuario.");
            }
        } else {
            usuario = existente.get();
            System.out.println("🔁 Usuario de Google ya existe: " + email);
        }

        System.out.println("✅ Finalizado CustomOAuth2UserService para: " + email);
        return new CustomOAuth2User(oauth2User, usuario);
    }

}
