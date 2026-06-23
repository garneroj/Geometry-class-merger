package org.jorge.garnero.geometry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static void ExitOnError (int exitCode, String format, Object... args) {

        System.err.printf (format, args) ;
        System.err.println () ; // to be neat.

        System.err.println ("Uso: java -jar <jarfile> <ruta/clases.yaml> <ruta/svgFolder>") ;

        System.exit (exitCode) ;

    }

        /**
         * Vuelca el contenido de un StringBuilder a un archivo físico para depuración.
         * * @param contenidoAdoc El AsciiDoc generado en memoria.
         */
        public static void guardarAdocIntermedio (StringBuilder contenidoAdoc) {
            Path rutaDestino = Paths.get ("intermedio.adoc") ;
            try {
                Files.write (rutaDestino, contenidoAdoc.toString ().getBytes (
                        StandardCharsets.UTF_8)) ;
                System.out.println ("Archivo de depuración generado con éxito en: " + rutaDestino.toAbsolutePath ()) ;
            } catch (IOException e) {
                System.err.println ("Error al escribir intermedio.adoc: " + e.getMessage ()) ;
            }
        }

        public static String generateTunedPath (String filePath) {
            if (filePath == null || filePath.isEmpty ()) {
                return filePath;
            }

            // Convertimos a minúsculas temporalmente para atajar casos como ".SVG"
            if (filePath.toLowerCase ().endsWith (".svg")) {
                // Recortamos los últimos 4 caracteres (".svg") y anexamos el nuevo sufijo
                return filePath.substring (0, filePath.length () - 4) + "-tuned.svg";
            } else {
                // Si no termina en .svg, simplemente lo concatenamos al final
                return filePath + "-tuned.svg";
            }
        }

}
