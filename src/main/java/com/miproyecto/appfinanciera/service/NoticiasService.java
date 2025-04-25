package com.miproyecto.appfinanciera.service;

import com.miproyecto.appfinanciera.dto.NoticiaDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticiasService {

    public List<NoticiaDto> obtenerNoticias() {
        List<NoticiaDto> noticias = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.larepublica.co/peso-colombiano").get();
            Elements elementos = doc.select(".first-news a");

            for (Element elemento : elementos) {
                String titulo = elemento.text();
                String enlace = elemento.absUrl("href");

                System.out.println("Título: " + titulo);
                System.out.println("Enlace: " + enlace);

                noticias.add(new NoticiaDto(titulo, enlace));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores
        }
        return noticias;
    }
}
