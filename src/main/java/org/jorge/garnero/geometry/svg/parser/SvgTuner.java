package org.jorge.garnero.geometry.svg.parser;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jorge.garnero.geometry.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Locale;

public class SvgTuner {

    public static void clean (Node node) {

        // If node is a tag like <path>, <ellipse>, etc.
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element elemento = (Element) node;
            NamedNodeMap atributos = elemento.getAttributes();

            // From end to start to preserve indexing.
            for (int i = atributos.getLength() - 1; i >= 0; i--) {
                Node atributo = atributos.item(i);
                if ("null".equals(atributo.getNodeValue())) {
                    elemento.removeAttribute(atributo.getNodeName());
                }
            }
        }

        //--- clean children.
        NodeList hijos = node.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            clean(hijos.item(i));
        }
    }

    private static void export (Node doc, String rutaArchivo) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaArchivo));

        transformer.transform(source, result);
    }

    public static void tune(String inputFilePath, String outputFilePath, double margin) {

        try {
            //--- load
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            File svgFile = new File(inputFilePath);
            SVGDocument doc = factory.createSVGDocument(svgFile.toURI().toString());

            Element root = doc.getDocumentElement();

            //--- clean
            clean(root);

            //--- giant canvas.
            root.setAttribute("width", "10000");
            root.setAttribute("height", "10000");
            root.removeAttribute("viewBox");

            //--- Context and graphic nodes.
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);

            GVTBuilder builder = new GVTBuilder();
            GraphicsNode rootNode = builder.build(ctx, doc);

            //--- bounds calculation.
            Rectangle2D bounds = rootNode.getPrimitiveBounds();
            if (bounds == null) {
                System.err.println("El archivo SVG parece estar vacío.");
                return;
            }

            double minX   = bounds.getX() - margin;
            double minY   = bounds.getY() - margin;
            double width  = bounds.getWidth() + (margin * 2);
            double height = bounds.getHeight() + (margin * 2);

            //--- Inyect attributes at root.
            root.setAttribute("width", String.format(Locale.US, "%.2f", width));
            root.setAttribute("height", String.format(Locale.US, "%.2f", height));
            root.setAttribute("viewBox", String.format(Locale.US, "%.2f %.2f %.2f %.2f", minX, minY, width, height));

            //--- export
            export(doc, outputFilePath);
        } catch (Exception e) {
            Utils.ExitOnError (5, "⚠️ Error procesando el SVG: " + e.getMessage());
        }

    }

}
