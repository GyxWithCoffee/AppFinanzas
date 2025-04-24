package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.model.MetaAhorro;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.MetaAhorroRepository;
import com.miproyecto.appfinanciera.service.UsuarioDetalles;
import com.miproyecto.appfinanciera.service.UsuarioService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/ahorro")
public class AhorroController {

    @Autowired
    private MetaAhorroRepository metaAhorroRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ahorro/paso1")
    public String mostrarPaso1(@RequestParam(value = "id", required = false) Long id, Model model) {
        MetaAhorro meta = (id != null)
                ? metaAhorroRepository.findById(id).orElse(new MetaAhorro())
                : new MetaAhorro();
        model.addAttribute("metaAhorro", meta);
        return "ahorro/paso1"; // asegúrate de tener el archivo paso1.html en templates/ahorro
    }




    @PostMapping("/paso1")
    public String guardarPaso1(@ModelAttribute("metaAhorro") MetaAhorro metaAhorro) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String email;

        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetalles usuarioDetalles) {
            email = usuarioDetalles.getUsername();
        } else if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else {
            return "redirect:/error";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        metaAhorro.setUsuario(usuario);

        MetaAhorro metaGuardada = metaAhorroRepository.saveAndFlush(metaAhorro);
        System.out.println("ID generado de MetaAhorro: " + metaGuardada.getId());

        return "redirect:/ahorro/paso2?id=" + metaGuardada.getId();
    }



    @GetMapping("/paso2")
    public String mostrarPaso2(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id == null) {
            // Si no se pasó un ID, puede redirigir al paso 1 o mostrar un error personalizado
            return "redirect:/ahorro/ahorro/paso1"; // 👈 o crea un error.html bonito
        }

        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(new MetaAhorro());
        model.addAttribute("metaAhorro", meta);
        model.addAttribute("categorias", List.of("Educación", "Negocio", "Hogar", "Tecnología", "Vehículo", "Ropa", "Salud", "Accesorios", "Viaje", "Otro"));
        return "ahorro/paso2";
    }




    @PostMapping("/paso2")
    public String guardarPaso2(@ModelAttribute("metaAhorro") MetaAhorro metaAhorroForm,
                               @RequestParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetalles usuarioDetalles) {
            email = usuarioDetalles.getUsername();
        } else if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else {
            return "redirect:/error";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);

        // Recupera la meta existente
        MetaAhorro metaExistente = metaAhorroRepository.findById(id).orElse(null);
        if (metaExistente == null) {
            return "redirect:/ahorro/ahorro/paso1"; // fallback
        }

        // Actualiza solo los campos modificables en paso 2
        metaExistente.setCategoria(metaAhorroForm.getCategoria());
        metaExistente.setMonto(metaAhorroForm.getMonto());
        metaExistente.setUsuario(usuario); // en realidad ya lo tenía

        metaAhorroRepository.save(metaExistente);
        return "redirect:/ahorro/paso3?id=" + metaExistente.getId();
    }



    @GetMapping("/paso3")
    public String mostrarPaso3(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id == null) {
            return "redirect:/ahorro/ahorro/paso1";
        }

        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) {
            return "redirect:/ahorro/ahorro/paso1";
        }

        model.addAttribute("metaAhorro", meta);
        model.addAttribute("frecuencias", List.of("Diario", "Semanal", "Mensual"));
        return "ahorro/paso3";
    }



    @PostMapping("/paso3")
    public String guardarPaso3(@ModelAttribute("metaAhorro") MetaAhorro metaAhorroForm,
                               @RequestParam("id") Long id, Model model) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) {
            return "redirect:/ahorro/ahorro/paso1";
        }

        meta.setFechaFinal(metaAhorroForm.getFechaFinal());
        meta.setFrecuencia(metaAhorroForm.getFrecuencia());

        metaAhorroRepository.save(meta);

        // Calculamos los días y los abonos
        long diasTotales = java.time.temporal.ChronoUnit.DAYS.between(meta.getFechaCreacion(), meta.getFechaFinal());
        int intervalos;
        switch (meta.getFrecuencia()) {
            case "Semanal" -> intervalos = (int) Math.ceil(diasTotales / 7.0);
            case "Mensual" -> intervalos = (int) Math.ceil(diasTotales / 30.0);
            default -> intervalos = (int) diasTotales;
        }

        long abono = intervalos == 0 ? meta.getMonto() : meta.getMonto() / intervalos;
        NumberFormat formatoPesos = NumberFormat.getInstance(new Locale("es", "CO"));
        String abonoFormateado = String.format("$ %,d", abono).replace(",", ".");

        model.addAttribute("categoria", meta.getCategoria());
        model.addAttribute("dias", diasTotales);
        model.addAttribute("abonoFormateado", abonoFormateado);
        model.addAttribute("intervaloTexto", switch (meta.getFrecuencia()) {
            case "Semanal" -> "7 días";
            case "Mensual" -> "30 días";
            default -> "1 día";
        });
        // ⚠️ AGREGAR ESTO:
        model.addAttribute("id", meta.getId());

        return "ahorro/resumen";
    }




    @GetMapping("/progreso")
    public String mostrarProgreso(@RequestParam("id") Long id, Model model) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/ahorro/paso1";

        long monto = meta.getMonto();
        long abonado = meta.getAbonado() != null ? meta.getAbonado() : 0;
        long progreso = (monto == 0) ? 0 : (abonado * 100 / monto);

        NumberFormat formatoPesos = NumberFormat.getInstance(new Locale("es", "CO"));
        String montoFormateado = "$ " + formatoPesos.format(monto).replace(",", ".");
        String abonadoFormateado = "$ " + formatoPesos.format(abonado).replace(",", ".");

        model.addAttribute("meta", meta);
        model.addAttribute("montoFormateado", montoFormateado);
        model.addAttribute("abonadoFormateado", abonadoFormateado);
        model.addAttribute("progreso", progreso);

        return "ahorro/progreso";
    }

    @PostMapping("/abonar")
    public String abonar(@RequestParam("id") Long id, @RequestParam("abono") Long abono) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/ahorro/paso1";

        Long acumulado = meta.getAbonado() == null ? 0 : meta.getAbonado();
        meta.setAbonado(acumulado + abono);
        metaAhorroRepository.save(meta);

        return "redirect:/ahorro/progreso?id=" + id;
    }

    @PostMapping("/eliminar")
    public String eliminarMeta(@RequestParam("id") Long id) {
        metaAhorroRepository.deleteById(id);
        return "redirect:/dashboard";
    }

}
