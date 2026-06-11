package org.jorge.garnero.geometry;

public class Utils {

    public static void ExitOnError (int exitCode, String format, Object... args) {

        System.err.printf (format, args) ;
        System.err.println () ; // to be neat.

        System.err.println ("Uso: java -jar <jarfile> <ruta/clases.yaml> <ruta/svgFolder>") ;

        System.exit (exitCode) ;

    }
}
