package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.MetaAhorro;
import com.miproyecto.appfinanciera.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuario(Usuario usuario);
    MetaAhorro findTopByUsuarioOrderByIdDesc(Usuario usuario);
    @Query("SELECT SUM(m.abonado) FROM MetaAhorro m WHERE m.usuario.id = :usuarioId")
    Optional<Double> sumaAbonosByUsuario(@Param("usuarioId") Long usuarioId);

    Optional<MetaAhorro> findFirstByUsuarioAndCompletadaFalse(Usuario usuario);
}
