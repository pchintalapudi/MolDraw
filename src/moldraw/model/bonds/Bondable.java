/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.bonds;

import javafx.beans.property.BooleanProperty;
import moldraw.model.targets.Centered;
import moldraw.xml.XMLSerializable;

/**
 *
 * @author prem
 */
public interface Bondable extends Centered, XMLSerializable {

    BooleanProperty visibleProperty();

    default boolean isVisible() {
        return visibleProperty().get();
    }

    default void setVisible(boolean value) {
        if (!visibleProperty().isBound()) {
            visibleProperty().set(value);
        }
    }
}
