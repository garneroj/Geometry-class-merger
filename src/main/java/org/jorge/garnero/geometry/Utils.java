package org.jorge.garnero.geometry;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.asciidoctor.Attributes;

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

    public static Attributes getThemeAttributes (String resourcePath) {
        try {
            // 1. Leer el YAML desde src/main/resources
            InputStream temaStream = Utils.class.getResourceAsStream (resourcePath) ;

            if (temaStream == null) {
                // Falla instantáneamente si el archivo no existe en el .jar
                throw new RuntimeException ("No se encontró el tema en los recursos del proyecto: " + resourcePath) ;
            }

            // 2. Crear un archivo temporal
            Path archivoTemaTemp = Files.createTempFile ("tema-geometria-", ".yml") ;
            archivoTemaTemp.toFile ().deleteOnExit () ;

            // 3. Copiar el contenido
            Files.copy (temaStream, archivoTemaTemp, StandardCopyOption.REPLACE_EXISTING) ;

            // 4. Retornar atributos
            return Attributes.builder ()
                             .attribute ("pdf-themesdir", archivoTemaTemp.getParent ().toAbsolutePath ().toString ())
                             .attribute ("pdf-theme", archivoTemaTemp.getFileName ().toString ())
                             .build () ;

        } catch (Exception e) {
            // Convierte cualquier falla de lectura/escritura en una excepción fatal
            throw new RuntimeException ("Fallo fatal al cargar el tema visual '" + resourcePath + "'. Abortando.", e) ;
        }
    }
}
