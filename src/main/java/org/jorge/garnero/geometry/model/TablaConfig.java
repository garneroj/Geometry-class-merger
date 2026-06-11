package org.jorge.garnero.geometry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TablaConfig {

    private final List<FilaTabla> filas;

    @JsonCreator
    public TablaConfig (@JsonProperty (value = "filas", required = true) List<FilaTabla> filas) {
        this.filas = filas;
    }

    public List<FilaTabla> getFilas () {
        return filas;
    }
}
