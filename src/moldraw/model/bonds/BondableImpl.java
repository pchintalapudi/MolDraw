/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.bonds;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import moldraw.xml.XMLDataBuilder;
import moldraw.xml.XMLSerializable;

/**
 *
 * @author prem
 */
public abstract class BondableImpl implements Bondable {

    private final DoubleProperty centerXProperty = new SimpleDoubleProperty(),
            centerYProperty = new SimpleDoubleProperty(),
            centerZProperty = new SimpleDoubleProperty();
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty(true);

    @Override
    public DoubleProperty centerXProperty() {
        return centerXProperty;
    }

    @Override
    public DoubleProperty centerYProperty() {
        return centerYProperty;
    }

    @Override
    public DoubleProperty centerZProperty() {
        return centerZProperty;
    }

    @Override
    public BooleanProperty visibleProperty() {
        return visibleProperty;
    }

    protected XMLDataBuilder addBondableProperties(XMLDataBuilder builder) {
        return builder.addAttribute("centerX", getCenterX()).addAttribute("centerY", getCenterY())
                .addAttribute("centerZ", getCenterZ()).addAttribute("visible", isVisible());
    }

    @Override
    public boolean equals(Object other) {
        return XMLSerializable.equals(this, other);
    }

    @Override
    public int hashCode() {
        return XMLSerializable.hashCode(this);
    }
}
