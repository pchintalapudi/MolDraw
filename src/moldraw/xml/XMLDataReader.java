/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author prem
 */
public class XMLDataReader implements AutoCloseable {

    private final XMLStreamReader reader;

    public XMLDataReader(InputStream input) {
        try {
            reader = XMLInputFactory.newFactory().createXMLStreamReader(input);
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                    break;
                }
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    public XMLData read() {
        try {
            if (!reader.hasNext()) {
                return null;
            }
            XMLDataBuilder builder = XMLDataBuilder.newInstance();
            builder.setElement(reader.getLocalName());
            int attributeCount = reader.getAttributeCount();
            for (int i = 0; i < attributeCount; i++) {
                builder.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        builder.addNestedXMLData(read());
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        builder.setData(reader.getText());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        return builder.build();
                }
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public List<XMLData> readAll() {
        List<XMLData> data = new ArrayList<>();
        try {
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                    data.add(read());
                }
            }
            close();
        } catch (XMLStreamException ex) {
            Logger.getLogger(XMLDataReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public Stream<XMLData> streamAll() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new XMLDataIterator(), 0), false);
    }

    public class XMLDataIterator implements Iterator<XMLData> {

        @Override
        public boolean hasNext() {
            try {
                while (reader.hasNext()) {
                    if (reader.isStartElement()) {
                        return true;
                    } else {
                        reader.next();
                    }
                }
                return false;
            } catch (XMLStreamException ex) {
                Logger.getLogger(XMLDataReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        @Override
        public XMLData next() {
            return read();
        }

    }

    @Override
    public void close() throws XMLStreamException {
        reader.close();
    }
}
