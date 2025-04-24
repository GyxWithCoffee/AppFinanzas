package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}
