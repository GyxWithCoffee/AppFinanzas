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
                
                // Buscar la imagen dentro del enlace
                Element imagenElement = elemento.selectFirst("img");
                String imagenUrl = (imagenElement != null) ? imagenElement.absUrl("src") : null;
    
                // Filtramos solo si tiene título y enlace
                if (titulo != null && !titulo.isBlank() && enlace != null && !enlace.isBlank()) {
                    noticias.add(new NoticiaDto(titulo, enlace, imagenUrl));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores
        }
        return noticias;
    }
    public NoticiaDto obtenerUltimaNoticia() {
        List<NoticiaDto> noticias = obtenerNoticias();
        return noticias.isEmpty() ? null : noticias.get(0);
    }


}
