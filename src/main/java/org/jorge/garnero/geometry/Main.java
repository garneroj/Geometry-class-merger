package org.jorge.garnero.geometry;

import org.jorge.garnero.geometry.model.ClaseEspecificacion;
import org.jorge.garnero.geometry.parser.GeoTableParser;
import org.jorge.garnero.geometry.pdf.AsciidoctorRenderEngine;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main (String[] args) {

        if (args.length != 0 && args.length != 2)
            Utils.ExitOnError (1, "⚠️ Uso incorrecto.") ;

        if (args.length == 0)
            demoAndExit () ;

        // PROD
        System.out.println ("🚀 Iniciando GeometryClassMerger en modo Workspace...") ;

        File archivoYaml = new File (args [0]) ;
        String directorioBaseSvg = args [1] ;

        if (!archivoYaml.exists ())
            Utils.ExitOnError (2, "❌ El archivo YAML no existe: %s", archivoYaml.getAbsolutePath ()) ;

        //--- parse
        ClaseEspecificacion leccion = null;
        try {
            GeoTableParser parser  = new GeoTableParser () ;
            leccion = parser.parsear (archivoYaml) ;
            imprimirReporte (leccion, directorioBaseSvg) ;
        } catch (Exception e) {
            Utils.ExitOnError (3, "❌ Error fatal al procesar el archivo YAML: %s", e.getMessage ()) ;
        }

        System.out.println ("📄 Yaml parseado exitosamente: ") ;

        String pdfName = Paths.get (args [0]).getFileName ().toString ().replace (".yaml",".pdf") ;
        System.out.printf ("PDF=%s\n", pdfName);
        AsciidoctorRenderEngine engine = new AsciidoctorRenderEngine ();
        engine.generarPdfPrueba (leccion, pdfName) ;

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private static void demoAndExit () {

        GeoTableParser parser = new GeoTableParser () ;

        System.out.println ("ℹ️ No se proveyeron argumentos. Ejecutando DEMO interna desde resources...") ;

        // Carga el archivo desde dentro del .jar
        try (InputStream yamlStream = Main.class.getResourceAsStream ("/ejemplo_minimalista.yaml")) {
            if (yamlStream == null) {
                System.err.println ("❌ No se encontró el ejemplo en resources.") ;
                return;
            }
            ClaseEspecificacion leccion = parser.parsear (yamlStream) ;
            imprimirReporte (leccion, "[directorio-resources-interno]") ;

        } catch (Exception e) {
            System.err.println ("❌ Error en la demostración: " + e.getMessage ()) ;
        }

        System.exit (0) ;

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private static void imprimirReporte (ClaseEspecificacion leccion, String directorioBaseSvg) {

        System.out.println ("✅ YAML parseado correctamente.") ;
        System.out.println ("--------------------------------------------------") ;
        System.out.println ("Título: " + leccion.getTitulo ()) ;

        var filas = leccion.getTabla ().getFilas () ;
        for (int i = 0; i < filas.size () ; i++) {
            var fila = filas.get (i) ;
            Path rutaAbsolutaSvg = Paths.get (directorioBaseSvg, fila.getGrafico ()) ;

            System.out.printf ("Fila %d | Columnas: %d | Resolverá SVG en: %s%n", (i + 1), (fila.tieneCuartaColumna () ? 4 : 3), rutaAbsolutaSvg.toString ()) ;

        }
        System.out.println ("--------------------------------------------------") ;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

}