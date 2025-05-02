package com.miproyecto.appfinanciera.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ConsejoFinancieroService {

    private final List<String> consejos = Arrays.asList(
            "💡 Lleva un registro de tus ingresos y gastos cada mes.",
            "📊 Usa presupuestos para tomar decisiones inteligentes.",
            "🧠 Antes de gastar, pregúntate si es una necesidad o un deseo.",
            "📆 Ahorra primero, gasta después.",
            "🔄 Automatiza tu ahorro si puedes.",
            "🏦 Usa una cuenta separada solo para tus ahorros.",
            "💳 No pagues con tarjeta lo que no puedas pagar en efectivo.",
            "🧾 Revisa tus suscripciones y elimina las que no usas.",
            "🎯 Ponte metas financieras claras y medibles.",
            "📉 Evita las deudas de consumo con altas tasas.",
            "🚨 Un fondo de emergencia te protege en tiempos difíciles.",
            "👩‍🏫 Aprende algo nuevo sobre finanzas cada mes.",
            "🛒 Haz listas antes de comprar y evita compras impulsivas.",
            "⏳ Sé paciente: el dinero crece con el tiempo si lo inviertes.",
            "🔒 Protege tu información financiera como tu contraseña.",
            "🔧 Invierte en cosas que ahorren dinero a largo plazo.",
            "🧺 Diversifica tus fuentes de ingreso cuando sea posible.",
            "📚 La educación financiera te da libertad, no restricciones.",
            "📈 Invierte solo en lo que entiendes.",
            "👨‍👩‍👧 Enseña a otros lo que aprendes: reforzarás tu propio conocimiento."
    );

    public String obtenerConsejoAleatorio() {
        Collections.shuffle(consejos);
        return consejos.get(0);
    }
}
