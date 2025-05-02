package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.model.CategoriaGasto;
import com.miproyecto.appfinanciera.model.Gasto;
import com.miproyecto.appfinanciera.model.LimitePresupuesto;
import com.miproyecto.appfinanciera.model.Usuario;
import com.miproyecto.appfinanciera.repository.GastoRepository;
import com.miproyecto.appfinanciera.repository.LimitePresupuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PresupuestoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private LimitePresupuestoRepository limiteRepository;

    public void registrarGasto(Gasto gasto) {
        gastoRepository.save(gasto);
    }

    public List<Gasto> obtenerGastosDelMes(Usuario usuario, int mes, int anio) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
        return gastoRepository.findByUsuarioAndFechaBetween(usuario, inicio, fin);
    }

    public void guardarLimite(LimitePresupuesto limite) {
        limiteRepository.save(limite);
    }

    public List<LimitePresupuesto> obtenerLimitesDelMes(Usuario usuario, int mes, int anio) {
        return limiteRepository.findByUsuarioAndMesAndAnio(usuario, mes, anio);
    }

    public double calcularTotalPorCategoria(List<Gasto> gastos, CategoriaGasto categoria) {
        return gastos.stream()
                .filter(g -> g.getCategoria() == categoria)
                .mapToDouble(Gasto::getMonto)
                .sum();
    }
}
