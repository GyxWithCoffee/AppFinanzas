package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.MetaAhorro;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuario(Usuario usuario);
    MetaAhorro findTopByUsuarioOrderByIdDesc(Usuario usuario);

}
