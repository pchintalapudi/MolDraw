/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author prem
 */
public interface Selectable {

    BooleanProperty selectedProperty();

    default boolean isSelected() {
        return selectedProperty().get();
    }

    default void setSelected(boolean selected) {
        if (!selectedProperty().isBound()) {
            selectedProperty().set(selected);
        }
    }
}
