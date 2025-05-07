package com.example.nutriknoledge;

import java.util.List;

public class TopicData {
    private String titulo;
    private String descripcion_general;
    private List<String> puntos_principales;
    private List<LinkData> links_recomendados;
    private String notas_adicionales;

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcionGeneral() {
        return descripcion_general;
    }

    public List<String> getPuntosPrincipales() {
        return puntos_principales;
    }

    public List<LinkData> getLinksRecomendados() {
        return links_recomendados;
    }

    public String getNotasAdicionales() {
        return notas_adicionales;
    }

    public static class LinkData {
        private String titulo_link;
        private String url;

        public String getTituloLink() {
            return titulo_link;
        }

        public String getUrl() {
            return url;
        }
    }
} 