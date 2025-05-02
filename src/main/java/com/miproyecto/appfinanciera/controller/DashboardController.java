package com.miproyecto.appfinanciera.controller;

import com.miproyecto.appfinanciera.dto.NoticiaDto;
import com.miproyecto.appfinanciera.model.MetaAhorro;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.DeudaRepository;
import com.miproyecto.appfinanciera.repository.IngresoRepository;
import com.miproyecto.appfinanciera.repository.MetaAhorroRepository;
import com.miproyecto.appfinanciera.service.ConsejoFinancieroService;
import com.miproyecto.appfinanciera.service.NoticiasService;
import com.miproyecto.appfinanciera.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private MetaAhorroRepository metaAhorroRepository;

    @Autowired
    private ConsejoFinancieroService consejoFinancieroService;
    @Autowired
    private NoticiasService noticiasService;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, Authentication auth) {
        Usuario usuario = usuarioService.obtenerUsuarioPorAuthentication(auth);

        // Ahorro actual
        double totalAhorro = metaAhorroRepository.sumaAbonosByUsuario(usuario.getId()).orElse(0.0);
        model.addAttribute("totalAhorroFormateado", String.format("$%,.2f", totalAhorro));

        // Deudas actuales
        double totalDeudas = deudaRepository.findByUsuario(usuario)
                .stream()
                .mapToDouble(d -> d.getMonto() != null ? d.getMonto() : 0.0)
                .sum();
        model.addAttribute("totalDeudasFormateado", String.format("$%,.2f", totalDeudas));

        // Meta activa
        Optional<MetaAhorro> metaActiva = metaAhorroRepository.findFirstByUsuarioAndCompletadaFalse(usuario);
        model.addAttribute("nombreMetaActiva", metaActiva.map(MetaAhorro::getDescripcion).orElse("Sin meta activa"));

        // Tip financiero del día
        String tip = consejoFinancieroService.obtenerConsejoAleatorio();
        model.addAttribute("tipFinanciero", tip);
        List<NoticiaDto> noticias = noticiasService.obtenerNoticias();
        model.addAttribute("noticias", noticias);


        return "/dashboard";
    }
}
