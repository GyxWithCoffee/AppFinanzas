package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Deuda;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {
    List<Deuda> findByUsuario(Usuario usuario);
}
