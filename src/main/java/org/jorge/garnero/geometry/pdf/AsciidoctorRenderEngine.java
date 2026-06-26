package org.jorge.garnero.geometry.pdf;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.jorge.garnero.geometry.Utils;
import org.jorge.garnero.geometry.model.ClaseEspecificacion;

import java.io.File;

public class AsciidoctorRenderEngine {

    public void
    generarPdfPrueba (ClaseEspecificacion leccion, String rutaArchivoSalida) {

        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create ()) {

            StringBuilder adocBuilder = new StringBuilder () ;

            // 1. Cabecera del documento y propiedades
            adocBuilder.append (String.format ("= %s\n", leccion.getTitulo ())) ;
            adocBuilder.append (":doctype: article\n") ;
            adocBuilder.append (":pdf-theme: default\n\n") ;

            // 2. Subtítulo y Párrafo inicial
            adocBuilder.append (String.format ("== %s\n\n", leccion.getSubtitulo ())) ;
            adocBuilder.append (String.format ("%s\n\n", leccion.getParrafoInicial ())) ;

            // 3. --- INICIO DE LA TABLA ---
            // cols="1, 2, 3" define los anchos relativos de las columnas.
            // valign="middle" centra el contenido verticalmente, ideal para que el texto acompañe al gráfico.
            adocBuilder.append ("[cols=\"1, 2, 3\", valign=\"middle\"]\n") ;
            adocBuilder.append ("|===\n\n") ;

            for (int i = 0; i < leccion.getTabla ().getFilas ().size () ; i++) {
                var fila = leccion.getTabla ().getFilas ().get (i) ;
                String rutaSvg = "graficos_svg/" + fila.getGrafico () ;
                adocBuilder.append (String.format ("a| image::%s[width=120, align=\"center\"]\n", rutaSvg)) ;
                adocBuilder.append (String.format ("| %s\n", fila.getCol2 ())) ;
                adocBuilder.append (String.format ("| %s\n\n", fila.getCol3 ())) ;

            }
            adocBuilder.append ("|===\n") ;

            Attributes atributosTema = Utils.getThemeAttributes ("/tema-geometria.yml") ;

            Options options = Options.builder ()
                                     .backend ("pdf")
                                     .toFile (new File (rutaArchivoSalida))
                                     .safe (SafeMode.UNSAFE) // Permite leer los SVG locales
                                     .attributes (atributosTema)
                                     .build () ;

            //Utils.guardarAdocIntermedio (adocBuilder) ;

            asciidoctor.convert (adocBuilder.toString (), options) ;

            System.out.println ("✅ PDF generado en: " + rutaArchivoSalida) ;
        }
    }
}