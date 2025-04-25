package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.model.MetaAhorro;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.MetaAhorroRepository;
import com.miproyecto.appfinanciera.service.UsuarioDetalles;

import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/ahorro")
public class AhorroController {

    @Autowired
    private MetaAhorroRepository metaAhorroRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public String redirigirAhorro(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetalles usuarioDetalles) {
            email = usuarioDetalles.getUsername();
        } else if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else {
            return "redirect:/error";
        }

        Usuario usuario = usuarioService.buscarPorEmail(email);
        List<MetaAhorro> metas = metaAhorroRepository.findByUsuario(usuario);

        boolean tieneActivas = metas.stream()
                .anyMatch(meta -> meta.getFechaFinal() != null && meta.getFechaFinal().isAfter(LocalDate.now()));

        if (tieneActivas) {
            return "redirect:/ahorro/progreso";
        } else {
            return "redirect:/ahorro/paso1";
        }
    }

    @GetMapping("/paso1")
    public String mostrarPaso1(@RequestParam(value = "id", required = false) Long id, Model model) {
        MetaAhorro meta = (id != null)
                ? metaAhorroRepository.findById(id).orElse(new MetaAhorro())
                : new MetaAhorro();
        model.addAttribute("metaAhorro", meta);
        return "ahorro/paso1";
    }

    @PostMapping("/paso1")
    public String guardarPaso1(@ModelAttribute("metaAhorro") MetaAhorro metaAhorro) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        String email;
        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetalles usuarioDetalles) {
            email = usuarioDetalles.getUsername();
        } else if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        } else return "redirect:/error";

        Usuario usuario = usuarioService.buscarPorEmail(email);
        metaAhorro.setUsuario(usuario);

        MetaAhorro metaGuardada = metaAhorroRepository.saveAndFlush(metaAhorro);
        return "redirect:/ahorro/paso2?id=" + metaGuardada.getId();
    }

    @GetMapping("/paso2")
    public String mostrarPaso2(@RequestParam("id") Long id, Model model) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(new MetaAhorro());
        model.addAttribute("metaAhorro", meta);
        model.addAttribute("categorias", List.of("Educación", "Negocio", "Hogar", "Tecnología", "Vehículo", "Ropa", "Salud", "Accesorios", "Viaje", "Otro"));
        return "ahorro/paso2";
    }

    @PostMapping("/paso2")
    public String guardarPaso2(@ModelAttribute("metaAhorro") MetaAhorro metaForm,
                               @RequestParam("id") Long id) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/paso1";

        meta.setCategoria(metaForm.getCategoria());
        meta.setMonto(metaForm.getMonto());

        metaAhorroRepository.save(meta);
        return "redirect:/ahorro/paso3?id=" + meta.getId();
    }

    @GetMapping("/paso3")
    public String mostrarPaso3(@RequestParam("id") Long id, Model model) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/paso1";

        model.addAttribute("metaAhorro", meta);
        model.addAttribute("frecuencias", List.of("Diario", "Semanal", "Mensual"));
        return "ahorro/paso3";
    }

    @PostMapping("/paso3")
    public String guardarPaso3(@ModelAttribute("metaAhorro") MetaAhorro metaForm,
                               @RequestParam("id") Long id, Model model) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/paso1";

        meta.setFechaFinal(metaForm.getFechaFinal());
        meta.setFrecuencia(metaForm.getFrecuencia());
        metaAhorroRepository.save(meta);

        long dias = ChronoUnit.DAYS.between(meta.getFechaCreacion(), meta.getFechaFinal());
        int intervalos = switch (meta.getFrecuencia()) {
            case "Semanal" -> (int) Math.ceil(dias / 7.0);
            case "Mensual" -> (int) Math.ceil(dias / 30.0);
            default -> (int) dias;
        };

        long abono = (intervalos == 0) ? meta.getMonto() : meta.getMonto() / intervalos;
        NumberFormat formato = NumberFormat.getInstance(new Locale("es", "CO"));
        String abonoFormateado = "$ " + formato.format(abono).replace(",", ".");

        model.addAttribute("categoria", meta.getCategoria());
        model.addAttribute("dias", dias);
        model.addAttribute("abonoFormateado", abonoFormateado);
        model.addAttribute("intervaloTexto", switch (meta.getFrecuencia()) {
            case "Semanal" -> "7 días";
            case "Mensual" -> "30 días";
            default -> "1 día";
        });
        model.addAttribute("id", meta.getId());

        return "ahorro/resumen";
    }

    @GetMapping("/progreso")
    public String mostrarTodasLasMetas(Model model, Authentication auth) {
        String email = obtenerEmailUsuario(auth);
        if (email == null) return "redirect:/login";

        Usuario usuario = usuarioService.buscarPorEmail(email);
        List<MetaAhorro> metas = metaAhorroRepository.findByUsuario(usuario);

        NumberFormat formato = NumberFormat.getInstance(new Locale("es", "CO"));

        List<Map<String, Object>> metasFormateadas = metas.stream().map(meta -> {
            Map<String, Object> datos = new HashMap<>();
            long monto = meta.getMonto();
            long abonado = meta.getAbonado() != null ? meta.getAbonado() : 0;
            long progreso = monto == 0 ? 0 : (abonado * 100 / monto);

            datos.put("id", meta.getId());
            datos.put("descripcion", meta.getDescripcion());
            datos.put("categoria", meta.getCategoria());
            datos.put("montoFormateado", "$ " + formato.format(monto).replace(",", "."));
            datos.put("abonadoFormateado", "$ " + formato.format(abonado).replace(",", "."));
            datos.put("progreso", progreso);

            return datos;
        }).toList();

        model.addAttribute("metas", metasFormateadas);
        return "ahorro/progreso";
    }

    private String obtenerEmailUsuario(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetalles usuarioDetalles) {
            return usuarioDetalles.getUsername();
        } else if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        } else {
            return null;
        }
    }


    @PostMapping("/abonar")
    public String abonar(@RequestParam("id") Long id, @RequestParam("abono") Long abono) {
        MetaAhorro meta = metaAhorroRepository.findById(id).orElse(null);
        if (meta == null) return "redirect:/ahorro/paso1";

        Long acumulado = meta.getAbonado() != null ? meta.getAbonado() : 0;
        meta.setAbonado(acumulado + abono);
        metaAhorroRepository.save(meta);

        return "redirect:/ahorro/progreso";
    }

    @PostMapping("/eliminar")
    public String eliminarMeta(@RequestParam("id") Long id) {
        metaAhorroRepository.deleteById(id);
        return "redirect:/ahorro/progreso"
    }
}
