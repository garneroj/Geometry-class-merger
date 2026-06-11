package org.jorge.garnero.geometry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClaseEspecificacion {

    private final String titulo;
    private final String subtitulo;
    private final String parrafoInicial;
    private final TablaConfig tabla;

    @JsonCreator
    public ClaseEspecificacion (
            @JsonProperty (value = "titulo", required = true) String titulo,             @JsonProperty (value = "subtitulo") String subtitulo,             @JsonProperty (value = "parrafo_inicial") String parrafoInicial,             @JsonProperty (value = "tabla", required = true) TablaConfig tabla) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.parrafoInicial = parrafoInicial;
        this.tabla = tabla;
    }

    public String getTitulo () {
        return titulo;
    }

    public String getSubtitulo () {
        return subtitulo;
    }

    public String getParrafoInicial () {
        return parrafoInicial;
    }

    public TablaConfig getTabla () {
        return tabla;
    }
}
