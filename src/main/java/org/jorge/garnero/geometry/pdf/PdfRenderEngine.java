package org.jorge.garnero.geometry.pdf ;

import org.apache.pdfbox.pdmodel.PDDocument ;
import org.apache.pdfbox.pdmodel.PDPage ;
import org.apache.pdfbox.pdmodel.PDPageContentStream ;
import org.apache.pdfbox.pdmodel.font.PDType0Font ;
import org.jorge.garnero.geometry.model.ClaseEspecificacion ;

import org.apache.batik.transcoder.TranscoderInput ;
import org.apache.batik.transcoder.TranscoderOutput ;
import org.apache.batik.transcoder.image.PNGTranscoder ;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject ;
import org.jorge.garnero.geometry.model.FilaTabla;

import java.io.ByteArrayOutputStream ;
import java.nio.file.Paths ;
import java.nio.file.Path ;
import java.nio.file.Files ;

import java.io.IOException ;
import java.io.InputStream ;

import static org.jorge.garnero.geometry.pdf.PdfPar.FONT_SIZE_TABLE;
import static org.jorge.garnero.geometry.pdf.PdfPar.MARGEN;
import static org.jorge.garnero.geometry.pdf.PdfPar.FONT_SIZE_INI_PAR;
import static org.jorge.garnero.geometry.pdf.PdfPar.FONT_SIZE_SUBTITLE;
import static org.jorge.garnero.geometry.pdf.PdfPar.FONT_SIZE_TITLE;
import static org.jorge.garnero.geometry.pdf.PdfPar.GRAPHICS_INTERSPACE;
import static org.jorge.garnero.geometry.pdf.PdfPar.INI_TABLE_LINE_POSY;
import static org.jorge.garnero.geometry.pdf.PdfPar.ROW_HEIGHT;
import static org.jorge.garnero.geometry.pdf.PdfPar.TITLE_INTERSPACE;

public class PdfRenderEngine {

    public void generarPdf (ClaseEspecificacion leccion, String directorioBaseSvg, String rutaArchivoSalida) throws IOException {

        try (PDDocument doc = getDocument ()) {

            PDType0Font fuenteMatematica = getMathFont (doc) ;

            try (PDPageContentStream stream = new PDPageContentStream (doc, doc.getPage (0))) {

                int i = INI_TABLE_LINE_POSY ;
                showText (stream, fuenteMatematica, FONT_SIZE_TITLE,    50, i + 3 * TITLE_INTERSPACE, leccion.getTitulo ()) ;
                showText (stream, fuenteMatematica, FONT_SIZE_SUBTITLE, 50, i + 2 * TITLE_INTERSPACE, leccion.getSubtitulo ()) ;
                showText (stream, fuenteMatematica, FONT_SIZE_INI_PAR,  50, i + 1 * TITLE_INTERSPACE, leccion.getParrafoInicial ()) ;

                int cursorY  = INI_TABLE_LINE_POSY ;
                int techo    = cursorY ;
                dibujarLinea (stream, MARGEN.get (0), cursorY, MARGEN.get (4), cursorY) ;

                var filas = leccion.getTabla ().getFilas () ;
                for (int f = 0; f < filas.size () ; f++) {

                    var fila = filas.get (f) ;

                    cursorY = cursorY - GRAPHICS_INTERSPACE ;
                    renderGrafico (doc, stream, directorioBaseSvg, fila, cursorY) ;

                    showText (stream, fuenteMatematica, FONT_SIZE_TABLE, MARGEN.get (1), cursorY, fila.getCol2 ()) ;
                    showText (stream, fuenteMatematica, FONT_SIZE_TABLE, MARGEN.get (2), cursorY, fila.getCol3 ()) ;
                    if (fila.tieneCuartaColumna ())
                        showText (stream, fuenteMatematica, FONT_SIZE_TABLE, MARGEN.get (3),cursorY, fila.getCol4 ()) ;

                    cursorY = cursorY - ROW_HEIGHT ;
                    dibujarLinea (stream, MARGEN.get (1), techo  , MARGEN.get (1), cursorY) ;
                    dibujarLinea (stream, MARGEN.get (2), techo  , MARGEN.get (2), cursorY) ;
                    dibujarLinea (stream, MARGEN.get (3), techo  , MARGEN.get (3), cursorY) ;
                    dibujarLinea (stream, MARGEN.get (4), techo  , MARGEN.get (4), cursorY) ;

                    dibujarLinea (stream, MARGEN.get (0), cursorY, MARGEN.get (4), cursorY) ;

                    techo = cursorY ;

                }

            }
            doc.save (rutaArchivoSalida) ;
        }
    }

    private void renderGrafico (PDDocument doc, PDPageContentStream stream, final String directorioBaseSvg, final FilaTabla fila, int ycoor) {

        Path rutaAbsolutaSvg = Paths.get (directorioBaseSvg, fila.getGrafico ()) ;

        if ( ! Files.exists (rutaAbsolutaSvg)) {
            System.err.println ("❌ ARCHIVO NO ENCONTRADO: " + rutaAbsolutaSvg.toString ()) ;
            return;
        }

        try {
            byte[] imagenBytes = convertirSvgAPngBytes (rutaAbsolutaSvg.toString ()) ;
            PDImageXObject pdImage = PDImageXObject.createFromByteArray (doc, imagenBytes, fila.getGrafico ()) ;
            stream.drawImage (pdImage, MARGEN.get(0), ycoor - 60, 80, 80) ;

        } catch (Exception e) {
            System.err.println ("⚠️ Error procesando SVG (" + fila.getGrafico () + "): " + e.getMessage ()) ;
        }

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void showText ( final PDPageContentStream stream, final PDType0Font font,
                            final int fontSize, final int tx, final int ty, final String s) throws IOException {

        //--- Validate
        if (s == null || s.trim ().isEmpty ())
            return ;

        stream.beginText () ;

        stream.setFont         (font, fontSize) ;
        stream.newLineAtOffset (tx, ty)         ;
        stream.showText        (s)              ;

        stream.endText () ;

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void dibujarLinea (final PDPageContentStream stream, final float x1, final float y1, final float x2, final float y2) throws IOException {

        stream.setLineWidth (1f) ;
        stream.moveTo (x1, y1) ;
        stream.lineTo (x2, y2) ;
        stream.stroke () ;

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

    private byte[] convertirSvgAPngBytes (String rutaAbsolutaSvg) throws Exception {

        PNGTranscoder transcoder = new PNGTranscoder () ;
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream ()) {
            TranscoderInput input = new TranscoderInput (Paths.get (rutaAbsolutaSvg).toUri ().toURL ().toString ()) ;
            TranscoderOutput output = new TranscoderOutput (ostream) ;
            transcoder.transcode (input, output) ;
            return ostream.toByteArray () ;
        }

    }

}