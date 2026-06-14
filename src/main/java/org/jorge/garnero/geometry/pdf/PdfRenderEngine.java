package org.jorge.garnero.geometry.pdf ;

import org.apache.pdfbox.pdmodel.PDDocument ;
import org.apache.pdfbox.pdmodel.PDPage ;
import org.apache.pdfbox.pdmodel.PDPageContentStream ;
import org.apache.pdfbox.pdmodel.font.PDType0Font ;
import org.jorge.garnero.geometry.model.ClaseEspecificacion ;

import java.io.IOException ;
import java.io.InputStream ;

public class PdfRenderEngine {

    public void generarPdf (ClaseEspecificacion leccion, String directorioBaseSvg, String rutaArchivoSalida) throws IOException {

        try (PDDocument doc = getDocument ()) {
            PDType0Font fuenteMatematica = getMathFont (doc) ;

            try (PDPageContentStream stream = new PDPageContentStream (doc, doc.getPage (0))) {

                stream.beginText () ;
                // origen=(0,0).50=margen-izq 720=cerca-borde-superior
                showText (stream, fuenteMatematica, 12, 50, 720, "Raines-Geometry") ;
                showText (stream, fuenteMatematica, 18, 0, -25, leccion.getTitulo ());
                stream.endText () ;

            }
            doc.save (rutaArchivoSalida) ;
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void showText ( final PDPageContentStream stream, final PDType0Font font,
                            final int fontSize, final int tx, final int ty, final String s) throws IOException {

        stream.setFont         (font, fontSize) ;
        stream.newLineAtOffset (tx, ty)         ;
        stream.showText        (s)              ;

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    PDType0Font getMathFont (PDDocument documento) throws IOException {

        PDType0Font result ;

        try (InputStream fontStream = PdfRenderEngine.class.getResourceAsStream ("/fonts/DejaVuSans.ttf")) {
            if (fontStream == null) {
                throw new IOException ("No se encontró la fuente DejaVuSans.ttf en los resources.") ;
            }
            result = PDType0Font.load (documento, fontStream) ;
        }

        return result ;

    }

    PDDocument getDocument () {

        PDDocument doc = new PDDocument () ;
        PDPage pagina = new PDPage () ;
        doc.addPage (pagina) ;

        return doc ;

    }




}