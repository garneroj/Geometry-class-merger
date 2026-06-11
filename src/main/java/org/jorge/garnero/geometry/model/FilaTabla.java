package org.jorge.garnero.geometry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FilaTabla {

    private final String grafico;
    private final String col2;
    private final String col3;
    private final String col4;

    @JsonCreator
    public FilaTabla (
            @JsonProperty ("grafico") String grafico,             @JsonProperty ("col2") String col2,             @JsonProperty ("col3") String col3,             @JsonProperty ("col4") String col4) {
        
        // Validación estricta manual
        if (grafico == null) throw new IllegalArgumentException ("Fila inválida: Falta el tag obligatorio 'grafico'") ;
        if (col2 == null) throw new IllegalArgumentException ("Fila inválida: Falta el tag obligatorio 'col2'") ;
        if (col3 == null) throw new IllegalArgumentException ("Fila inválida: Falta el tag obligatorio 'col3'") ;

        this.grafico = grafico;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
    }

    public String getGrafico () { return grafico; }
    public String getCol2 () { return col2; }
    public String getCol3 () { return col3; }
    public String getCol4 () { return col4; }

    public boolean tieneCuartaColumna () {
        return col4 != null && !col4.strip ().isEmpty () ;
    }
}
