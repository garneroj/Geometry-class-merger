package org.jorge.garnero.geometry.pdf;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

import java.io.File;

public class AsciidoctorRenderEngine {

    public void generarPdfPrueba(String rutaArchivoSalida) {
        // 1. Inicializar el motor (usamos try-with-resources para que se cierre prolijamente)
        try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {

            // 2. Nuestro "documento" en texto plano con sintaxis AsciiDoc
            String contenidoAdoc = "= Conceptos Básicos\n" +
                    ":doctype: article\n" +
                    ":pdf-theme: default\n\n" +
                    "Este es un PDF generado dinámicamente en memoria desde Java.";

            // 3. Configurar que queremos un PDF y dónde guardarlo
            Options options = Options.builder()
                                     .backend("pdf")
                                     .toFile(new File(rutaArchivoSalida))
                                     .safe(SafeMode.UNSAFE) // Permite leer archivos locales (útil para los SVG más adelante)
                                     .build();

            // 4. ¡Magia!
            asciidoctor.convert(contenidoAdoc, options);

            System.out.println("✅ PDF generado en: " + rutaArchivoSalida);
        }
    }
}