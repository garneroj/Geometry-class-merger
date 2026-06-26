package org.jorge.garnero.geometry.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;

public class PdfConcatenator {

    public static void merge (String[] archivosEntrada, String archivoSalida) {

        try {
            PDFMergerUtility merger = new PDFMergerUtility () ;
            merger.setDestinationFileName (archivoSalida) ;

            int archivosValidos = 0;

            //--- enqueue input pdf's
            for (String rutaArchivo : archivosEntrada) {
                File file = new File (rutaArchivo) ;
                if (file.exists ()) {
                    merger.addSource (file) ;
                    System.out.println ("Agregando: " + rutaArchivo) ;
                    archivosValidos++;
                } else {
                    System.err.println ("Advertencia: No se encontró el archivo y será omitido -> " + rutaArchivo) ;
                }
            }

            //--- no files ?
            if (archivosValidos == 0) {
                System.err.println ("Error: No se encontró ningún archivo PDF válido para concatenar. Proceso abortado.") ;
                return;
            }

            //--- concatenate.
            merger.mergeDocuments (MemoryUsageSetting.setupMainMemoryOnly ()) ;
            System.out.println ("¡Éxito! " + archivosValidos + " PDFs concatenados en: " + archivoSalida) ;

        } catch (IOException e) {
            System.err.println ("Error grave al concatenar los PDFs: " + e.getMessage ()) ;
        }
    }
}