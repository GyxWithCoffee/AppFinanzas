package com.miproyecto.appfinanciera.repository;

import com.miproyecto.appfinanciera.model.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {
    List<Leccion> findByCategoriaOrderByOrdenAsc(String categoria);
    int countByCategoria(String categoria);

}
