package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Gasto;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioAndFechaBetween(Usuario usuario, LocalDate inicio, LocalDate fin);
}
