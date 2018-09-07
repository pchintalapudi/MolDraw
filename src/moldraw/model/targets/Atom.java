/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import moldraw.utils.IDLogger;
import moldraw.utils.Utils;
import moldraw.xml.XMLData;
import moldraw.xml.XMLDataBuilder;

/**
 *
 * @author prem
 */
public class Atom extends RGroup {

    private final ObjectProperty<Element> elementProperty = new SimpleObjectProperty<>();

    private Atom() {
        this(null, null);
    }

    public Atom(IDLogger idLogger) {
        this(null, idLogger);
    }

    private Atom(Element element) {
        this(element, null);
    }
    
    public Atom(Element element, IDLogger idLogger) {
        super(idLogger);
        super.nameProperty().bind(Bindings.createStringBinding(()
                -> getElement() == null ? "" : Utils.title(Element.getAbbrev(getElement())), elementProperty));
        elementProperty.set(element);
    }

    public void setElement(Element e) {
        elementProperty.set(e);
    }

    public Element getElement() {
        return elementProperty.get();
    }

    @Override
    public XMLData serialize() {
        return super.addBondableProperties(XMLDataBuilder.newInstance().setElement("Atom").setData(getId()))
                .addAttribute("element", getElement()).addAttribute("charge", getCharge()).build();
    }

    public static Atom deserialize(XMLData xmlData) {
        if (!"Atom".equals(xmlData.element())) {
            throw new IllegalArgumentException("Not an Atom!!!");
        }
        Atom a = new Atom();
        a.setId(xmlData.data());
        xmlData.attributes().entrySet().forEach(e -> {
            switch (e.getKey()) {
                case "centerX":
                    a.setCenterX(Double.parseDouble(e.getValue()));
                    break;
                case "centerY":
                    a.setCenterY(Double.parseDouble(e.getValue()));
                    break;
                case "centerZ":
                    a.setCenterZ(Double.parseDouble(e.getValue()));
                    break;
                case "charge":
                    a.setCharge(Integer.parseInt(e.getValue()));
                    break;
                case "element":
                    a.setElement(Element.valueOf(e.getValue()));
                    break;
            }
        });
        return a;
    }
}
