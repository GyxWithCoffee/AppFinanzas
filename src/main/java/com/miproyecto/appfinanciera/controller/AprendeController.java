package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.dto.Opcion;
import com.miproyecto.appfinanciera.model.*;
import com.miproyecto.appfinanciera.repository.*;
import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/aprende")
public class AprendeController {

    @Autowired
    private LeccionRepository leccionRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private LeccionCompletadaRepository leccionCompletadaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public String mostrarMenuAprende(Model model, Authentication auth) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);

        // Obtener todas las lecciones completadas por el usuario
        List<LeccionCompletada> completadas = leccionCompletadaRepository.findByUsuario(usuario);
        Set<Long> idsCompletadas = completadas.stream()
                .map(lc -> lc.getLeccion().getId())
                .collect(Collectors.toSet());

        // Mapas para contar progreso por categoría
        Map<String, Long> totalPorCategoria = leccionRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Leccion::getCategoria, Collectors.counting()));

        Map<String, Long> completadasPorCategoria = leccionRepository.findAll()
                .stream()
                .filter(leccion -> idsCompletadas.contains(leccion.getId()))
                .collect(Collectors.groupingBy(Leccion::getCategoria, Collectors.counting()));

        // Asegura que todas las categorías estén presentes (aunque tenga 0 completadas)
        for (String categoria : totalPorCategoria.keySet()) {
            completadasPorCategoria.putIfAbsent(categoria, 0L);
        }

        model.addAttribute("totalPorCategoria", totalPorCategoria);
        model.addAttribute("completadasPorCategoria", completadasPorCategoria);

        return "Aprende/index";
    }



    // Lista de lecciones por categoría
    @GetMapping("/{categoria}")
    public String verLeccionesPorCategoria(@PathVariable String categoria, Model model, Authentication auth) {
        List<Leccion> lecciones = leccionRepository.findByCategoriaOrderByOrdenAsc(categoria);
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);
        List<LeccionCompletada> completadas = leccionCompletadaRepository.findByUsuario(usuario);

        Set<Long> leccionesCompletadasIds = completadas.stream()
                .map(lc -> lc.getLeccion().getId())
                .collect(Collectors.toSet());

        model.addAttribute("lecciones", lecciones);
        model.addAttribute("completadas", leccionesCompletadasIds);
        model.addAttribute("categoria", categoria);
        return "Aprende/lecciones";
    }

    // Vista introductoria
    @GetMapping("/detalle/{id}")
    public String verDetalleLeccion(@PathVariable Long id, Model model) {
        Leccion leccion = leccionRepository.findById(id).orElseThrow();
        model.addAttribute("leccion", leccion);
        return "Aprende/detalle";
    }

    @GetMapping("/quizz/{id}")
    public String iniciarQuizz(@PathVariable Long id, Model model) {
        Leccion leccion = leccionRepository.findById(id).orElseThrow();
        List<Pregunta> preguntas = preguntaRepository.findByLeccionOrderByOrdenAsc(leccion);

        // Mezclar opciones de cada pregunta
        List<Map<String, Object>> preguntasConOpciones = preguntas.stream().map(p -> {
            List<Opcion> opciones = new ArrayList<>(List.of(
                    new Opcion(p.getOpcion1(), 1),
                    new Opcion(p.getOpcion2(), 2),
                    new Opcion(p.getOpcion3(), 3)
            ));
            Collections.shuffle(opciones); // ✅ CORREGIDO: lista mutable

            Map<String, Object> data = new HashMap<>();
            data.put("id", p.getId());
            data.put("texto", p.getTexto());
            data.put("tip", p.getTip());
            data.put("opciones", opciones);
            data.put("respuestaCorrecta", p.getRespuestaCorrecta());
            return data;
        }).toList();

        model.addAttribute("leccion", leccion);
        model.addAttribute("preguntas", preguntasConOpciones);
        return "Aprende/quizz";
    }


    @PostMapping("/quizz/completar/{id}")
    public String marcarLeccionComoCompletada(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(authentication);
        Leccion leccion = leccionRepository.findById(id).orElseThrow();

        // Verifica que no esté ya registrada
        boolean yaCompletada = leccionCompletadaRepository.existsByUsuarioAndLeccion(usuario, leccion);
        if (!yaCompletada) {
            LeccionCompletada completada = new LeccionCompletada();
            completada.setUsuario(usuario);
            completada.setLeccion(leccion);
            completada.setFechaCompletado(LocalDateTime.now());
            leccionCompletadaRepository.save(completada);
        }

        return "redirect:/aprende/" + leccion.getCategoria();
    }

}
