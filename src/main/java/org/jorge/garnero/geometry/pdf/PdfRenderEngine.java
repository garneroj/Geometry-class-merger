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
import java.io.ByteArrayOutputStream ;
import java.io.StringReader;
import java.nio.file.Paths ;
import java.nio.file.Path ;
import java.nio.file.Files ;

import java.nio.charset.StandardCharsets;

import java.io.IOException ;
import java.io.InputStream ;

public class PdfRenderEngine {

    public void generarPdf (ClaseEspecificacion leccion, String directorioBaseSvg, String rutaArchivoSalida) throws IOException {

        try (PDDocument doc = getDocument ()) {
            PDType0Font fuenteMatematica = getMathFont (doc) ;

            try (PDPageContentStream stream = new PDPageContentStream (doc, doc.getPage (0))) {

                showText (stream, fuenteMatematica, 18, 50, 720, leccion.getTitulo ()) ;
                showText (stream, fuenteMatematica, 14, 50, 695, leccion.getSubtitulo ()) ;
                showText (stream, fuenteMatematica, 12, 50, 670, leccion.getParrafoInicial ()) ;

                // 2. Coordenadas base
                int cursorY  = 600 ;
                int col1X    =  50 ;
                int col2X    = 150 ;
                int altoFila = 100 ;

                // Coordenadas para las líneas de la tabla
                int margenIzq = 40 ;
                int margenDer = 550 ;
                int separadorVerticalX = 140 ; // Línea entre el gráfico y el texto
                int techoTabla = cursorY + 20 ;

                // Dibujar el "techo" superior de la tabla
                dibujarLinea (stream, margenIzq, techoTabla, margenDer, techoTabla) ;

                // 3. Bucle iterativo
                var filas = leccion.getTabla ().getFilas () ;
                for (int i = 0; i < filas.size () ; i++) {
                    var fila = filas.get (i) ;

                    // --- Renderizar Gráfico SVG ---
                    Path rutaAbsolutaSvg = Paths.get (directorioBaseSvg, fila.getGrafico ()) ;
                    if (Files.exists (rutaAbsolutaSvg)) {
                        try {
                            byte[] imagenBytes = convertirSvgAPngBytes (rutaAbsolutaSvg.toString ()) ;
                            PDImageXObject pdImage = PDImageXObject.createFromByteArray (doc, imagenBytes, fila.getGrafico ()) ;
                            stream.drawImage (pdImage, col1X, cursorY - 60, 80, 80) ;
                        } catch (Exception e) {
                            System.err.println ("⚠️ Error procesando SVG (" + fila.getGrafico () + "): " + e.getMessage ()) ;
                        }
                    } else {
                        System.err.println ("❌ ARCHIVO NO ENCONTRADO: " + rutaAbsolutaSvg.toString ()) ;
                    }
                    // --- Renderizar Textos ---
                    showText (stream, fuenteMatematica, 12, col2X, cursorY, fila.getCol2 ()) ;

                    // --- Dibujar la cuadrícula de esta fila ---
                    int baseFila = cursorY - altoFila + 20 ;

                    // Línea horizontal inferior
                    dibujarLinea (stream, margenIzq, baseFila, margenDer, baseFila) ;

                    // Líneas verticales (Borde izquierdo, separador central, y borde derecho)
                    dibujarLinea (stream, margenIzq, techoTabla, margenIzq, baseFila) ;
                    dibujarLinea (stream, separadorVerticalX, techoTabla, separadorVerticalX, baseFila) ;
                    dibujarLinea (stream, margenDer, techoTabla, margenDer, baseFila) ;

                    // Actualizar el techo para la siguiente iteración
                    techoTabla = baseFila ;

                    // Bajar el cursor de contenido
                    cursorY -= altoFila ;
                }
            }
            doc.save (rutaArchivoSalida) ;
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