/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.xml;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author prem
 */
public class XMLDataWriter implements AutoCloseable {

    private final XMLStreamWriter writer;

    public XMLDataWriter(OutputStream output) {
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean startWritten;

    public XMLDataWriter write(XMLData data) {
        delegateWrite(data, true);
        return this;
    }

    private void delegateWrite(XMLData data, boolean flush) {
        try {
            //Branch prediction will likely elide after the first check
            if (!startWritten) {
                writer.writeStartDocument("utf-8", "1.0");
                startWritten = true;
            }
            writer.writeStartElement(data.element());
            data.attributes().entrySet().forEach(e -> {
                try {
                    writer.writeAttribute(e.getKey(), e.getValue());
                } catch (XMLStreamException ex) {
                    Logger.getLogger(XMLDataWriter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    System.out.println(data.attributes());
                    throw ex;
                }
            });
            writer.writeCharacters(data.data());
            data.nested().forEach(d -> delegateWrite(d, false));
            writer.writeEndElement();
            if (flush) {
                writer.flush();
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(XMLDataWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }
}
