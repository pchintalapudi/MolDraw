/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author prem
 */
public class XMLDataBuilder {

    private String element = "";
    private String data = "";
    private final List<XMLData> nested;
    private final Map<String, String> attributes;

    private XMLDataBuilder() {
        nested = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public XMLDataBuilder setElement(String element) {
        this.element = element;
        return this;
    }

    public XMLDataBuilder setData(String data) {
        this.data = data;
        return this;
    }

    public XMLDataBuilder addNestedXMLData(XMLData nested) {
        this.nested.add(nested);
        return this;
    }

    public XMLDataBuilder addAttribute(String name, String data) {
        this.attributes.put(name, data);
        return this;
    }
    public XMLDataBuilder addAttribute(String name, Object data) {
        this.attributes.put(name, data.toString());
        return this;
    }

    public XMLData build() {
        return new XMLDataImpl(element, data, nested, attributes);
    }

    public static XMLDataBuilder newInstance() {
        return new XMLDataBuilder();
    }

    private static class XMLDataImpl implements XMLData {

        private final String element, data;
        private final List<XMLData> nested;
        private final Map<String, String> attributes;

        public XMLDataImpl(String element, String data, List<XMLData> nested, Map<String, String> attributes) {
            this.attributes = new HashMap<>(attributes);
            this.data = data;
            this.element = element;
            this.nested = new ArrayList<>(nested);
        }

        @Override
        public String element() {
            return element;
        }

        @Override
        public String data() {
            return data;
        }

        @Override
        public List<XMLData> nested() {
            return nested;
        }

        @Override
        public Map<String, String> attributes() {
            return attributes;
        }

        @Override
        public String toString() {
            return element + " " + data;
        }
    }
}
