/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import moldraw.model.bonds.BondableImpl;
import moldraw.utils.IDLogger;
import moldraw.xml.XMLData;
import moldraw.xml.XMLDataBuilder;

/**
 *
 * @author prem
 */
public class RGroup extends BondableImpl implements Named, Charged, Selectable {

    private final StringProperty nameProperty = new SimpleStringProperty();
    private final IntegerProperty chargeProperty = new SimpleIntegerProperty();
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty();

    private static int count;
    private String id;

    private RGroup() {
    }

    public RGroup(IDLogger idLogger) {
        this();
        if (idLogger != null) {
            id = idLogger.forceAdd("Bondable " + (count = count == Integer.MAX_VALUE ? 0 : count + 1));
        }
    }

    @Override
    public StringProperty nameProperty() {
        return nameProperty;
    }

    @Override
    public IntegerProperty chargeProperty() {
        return chargeProperty;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public XMLData serialize() {
        return super.addBondableProperties(XMLDataBuilder.newInstance().setElement("RGroup").setData(id))
                .addAttribute("name", getName()).addAttribute("charge", getCharge()).build();
    }

    public static RGroup deserialize(XMLData xmlData) {
        if (!"RGroup".equals(xmlData.element())) {
            throw new IllegalArgumentException("Not an Atom!!!");
        }
        RGroup r = new RGroup();
        r.setId(xmlData.data());
        xmlData.attributes().entrySet().forEach(e -> {
            switch (e.getKey()) {
                case "centerX":
                    r.setCenterX(Double.parseDouble(e.getValue()));
                    break;
                case "centerY":
                    r.setCenterY(Double.parseDouble(e.getValue()));
                    break;
                case "centerZ":
                    r.setCenterZ(Double.parseDouble(e.getValue()));
                    break;
                case "charge":
                    r.setCharge(Integer.parseInt(e.getValue()));
                    break;
                case "name":
                    r.setName(e.getValue());
                    break;
            }
        });
        return r;
    }
}
