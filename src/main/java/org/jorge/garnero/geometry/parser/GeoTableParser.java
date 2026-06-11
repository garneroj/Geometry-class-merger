package org.jorge.garnero.geometry.parser;

import org.jorge.garnero.geometry.model.ClaseEspecificacion;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GeoTableParser {

    private final ObjectMapper mapper;

    public GeoTableParser () {
        this.mapper = new ObjectMapper (new YAMLFactory ()) ;
        this.mapper.enable (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) ;
    }

    // Para leer archivos externos en tu Workspace
    public ClaseEspecificacion parsear (File archivoYaml) throws IOException {
        return mapper.readValue (archivoYaml, ClaseEspecificacion.class) ;
    }

    // Para leer los ejemplos internos empaquetados en src/main/resources
    public ClaseEspecificacion parsear (InputStream inputStream) throws IOException {
        return mapper.readValue (inputStream, ClaseEspecificacion.class) ;
    }
}
