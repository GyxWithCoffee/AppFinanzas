package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.model.*;
import com.miproyecto.appfinanciera.service.PresupuestoService;
import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Controller
@RequestMapping("/presupuesto")
public class PresupuestoController {

    @Autowired
    private PresupuestoService presupuestoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verPresupuesto(Model model, Authentication auth) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);
        YearMonth actual = YearMonth.now();

        List<Gasto> gastos = presupuestoService.obtenerGastosDelMes(usuario, actual.getMonthValue(), actual.getYear());
        List<LimitePresupuesto> limites = presupuestoService.obtenerLimitesDelMes(usuario, actual.getMonthValue(), actual.getYear());

        model.addAttribute("gastos", gastos);
        model.addAttribute("limites", limites);
        model.addAttribute("nuevoGasto", new Gasto());
        model.addAttribute("nuevoLimite", new LimitePresupuesto());
        model.addAttribute("categorias", CategoriaGasto.values());

        // Gasto vs Límite
        Map<String, Double> gastoPorCategoria = new HashMap<>();
        Map<String, Double> limitePorCategoria = new HashMap<>();

        for (CategoriaGasto categoria : CategoriaGasto.values()) {
            double totalGasto = presupuestoService.calcularTotalPorCategoria(gastos, categoria);
            gastoPorCategoria.put(categoria.name(), totalGasto);
        }

        for (LimitePresupuesto limite : limites) {
            limitePorCategoria.put(limite.getCategoria().name(), limite.getLimiteMensual());
        }

        model.addAttribute("gastoPorCategoria", gastoPorCategoria);
        model.addAttribute("limitePorCategoria", limitePorCategoria);

        // Alertas educativas
        List<String> alertas = new ArrayList<>();
        for (String cat : limitePorCategoria.keySet()) {
            double gasto = gastoPorCategoria.getOrDefault(cat, 0.0);
            double limite = limitePorCategoria.get(cat);

            if (gasto > limite) {
                alertas.add("⚠️ Has superado el límite en " + cat.toLowerCase() + ". Evalúa reducir ese tipo de gasto innecesario.");
            } else if (gasto > limite * 0.8) {
                alertas.add("🔔 Estás cerca del límite en " + cat.toLowerCase() + ". Intenta contener tus gastos en esta categoría.");
            } else if (gasto > 0) {
                alertas.add("✅ Excelente control en " + cat.toLowerCase() + ". Sigue manteniendo buenos hábitos financieros.");
            }
        }


        model.addAttribute("alertas", alertas);

        return "presupuesto/presupuesto";
    }


    @PostMapping("/guardar-gasto")
    public String guardarGasto(@ModelAttribute Gasto nuevoGasto, Authentication auth) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);
        nuevoGasto.setUsuario(usuario);
        nuevoGasto.setFecha(LocalDate.now());
        presupuestoService.registrarGasto(nuevoGasto);
        return "redirect:/presupuesto";
    }

    @PostMapping("/guardar-limite")
    public String guardarLimite(@ModelAttribute LimitePresupuesto nuevoLimite, Authentication auth) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);
        nuevoLimite.setUsuario(usuario);
        YearMonth actual = YearMonth.now();
        nuevoLimite.setMes(actual.getMonthValue());
        nuevoLimite.setAnio(actual.getYear());
        presupuestoService.guardarLimite(nuevoLimite);
        return "redirect:/presupuesto";
    }
}
