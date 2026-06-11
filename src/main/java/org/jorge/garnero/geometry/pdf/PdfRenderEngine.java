package org.jorge.garnero.geometry.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.jorge.garnero.geometry.model.ClaseEspecificacion;

import java.io.IOException;

public class PdfRenderEngine {

    public void generarPdf (ClaseEspecificacion leccion, String directorioBaseSvg, String rutaArchivoSalida) throws IOException {

        // 1. Instanciamos un documento nuevo en memoria
        try (PDDocument documento = new PDDocument ()) {

            // 2. Creamos una página nueva (Apache PDFBox usa tamaño Carta / Letter por defecto)
            PDPage pagina = new PDPage () ;
            documento.addPage (pagina) ;

            // TODO: 3. Aquí abriremos un PDPageContentStream para empezar a dibujar textos y tablas.

            // 4. Escribimos el documento final en el disco
            documento.save (rutaArchivoSalida) ;
        }
    }
}
