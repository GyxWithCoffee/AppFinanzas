package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final Usuario usuario;

    public CustomOAuth2User(OAuth2User oauth2User, Usuario usuario) {
        this.oauth2User = oauth2User;
        this.usuario = usuario;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return usuario.getNombre();
    }

    public String getEmail() {
        return usuario.getEmail();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}

