/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.property.IntegerProperty;

/**
 *
 * @author prem
 */
public interface Charged {

    IntegerProperty chargeProperty();

    default int getCharge() {
        return chargeProperty().get();
    }

    default void setCharge(int charge) {
        if (!chargeProperty().isBound()) {
            chargeProperty().set(charge);
        }
    }
}
