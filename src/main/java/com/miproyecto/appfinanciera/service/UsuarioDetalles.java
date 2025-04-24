package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;

import java.util.*;
import java.util.stream.Collectors;

public class UsuarioDetalles implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetalles(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(rol -> (GrantedAuthority) rol::getNombre)
                .collect(Collectors.toSet());
    }
    public Usuario getUsuario() {
        return this.usuario;
    }

    @Override public String getPassword() { return usuario.getContrasena();}
    @Override public String getUsername() { return usuario.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return usuario.isHabilitado(); }
}