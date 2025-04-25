package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Ingreso;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByUsuario(Usuario usuario);
}
