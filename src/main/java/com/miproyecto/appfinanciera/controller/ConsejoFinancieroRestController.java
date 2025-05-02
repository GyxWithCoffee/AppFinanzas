package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.service.ConsejoFinancieroService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/consejos")
public class ConsejoFinancieroRestController {

    private final ConsejoFinancieroService consejoService;

    public ConsejoFinancieroRestController(ConsejoFinancieroService consejoService) {
        this.consejoService = consejoService;
    }

    @GetMapping("/aleatorio")
    public String obtenerConsejoAleatorio() {
        return consejoService.obtenerConsejoAleatorio();
    }
}
