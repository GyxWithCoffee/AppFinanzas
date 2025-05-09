package com.miproyecto.appfinanciera.repository;


import com.miproyecto.appfinanciera.model.Pregunta;
import com.miproyecto.appfinanciera.model.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByLeccionOrderByOrdenAsc(Leccion leccion);
}
