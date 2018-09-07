/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.property.StringProperty;

/**
 *
 * @author prem
 */
public interface Named {

    StringProperty nameProperty();

    default String getName() {
        return nameProperty().get();
    }

    default void setName(String name) {
        if (!nameProperty().isBound()) {
            nameProperty().set(name);
        }
    }
}
