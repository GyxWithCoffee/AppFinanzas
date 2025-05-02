package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.LimitePresupuesto;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LimitePresupuestoRepository extends JpaRepository<LimitePresupuesto, Long> {
    List<LimitePresupuesto> findByUsuarioAndMesAndAnio(Usuario usuario, int mes, int anio);
}
