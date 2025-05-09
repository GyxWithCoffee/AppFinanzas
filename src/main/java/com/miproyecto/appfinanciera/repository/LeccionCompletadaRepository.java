package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Leccion;
import com.miproyecto.appfinanciera.model.LeccionCompletada;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeccionCompletadaRepository extends JpaRepository<LeccionCompletada, Long> {

    List<LeccionCompletada> findByUsuario(Usuario usuario);

    boolean existsByUsuarioAndLeccion(Usuario usuario, Leccion leccion);
    int countByUsuario(Usuario usuario);
    int countByUsuarioAndLeccion_Categoria(Usuario usuario, String categoria);


}
