/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.bonds;

import javafx.beans.property.ObjectProperty;
import moldraw.xml.XMLSerializable;

/**
 *
 * @author prem
 */
public interface Bond extends XMLSerializable {

    ObjectProperty<Bondable> startBondableProperty();

    ObjectProperty<Bondable> endBondableProperty();

    default Bondable getStartBondable() {
        return startBondableProperty().get();
    }

    default Bondable getEndBondable() {
        return endBondableProperty().get();
    }

    default void setStartBondable(Bondable bondable) {
        startBondableProperty().set(bondable);
    }

    default void setEndBondable(Bondable bondable) {
        endBondableProperty().set(bondable);
    }

    ObjectProperty<BondState> bondStateProperty();

    default void setBondState(BondState bondState) {
        bondStateProperty().set(bondState);
    }

    default boolean contains(Bondable b) {
        return b == null ? getStartBondable() == null || getEndBondable() == null
                : (b.equals(getStartBondable()) || b.equals(getEndBondable()));
    }

    default BondState getBondState() {
        return bondStateProperty().get();
    }

    default int getBondOrder() {
        return getBondState().ordinal();
    }

    ObjectProperty<VisualState> visualStateProperty();

    default VisualState getVisualState() {
        return visualStateProperty().get();
    }

    default void setVisualState(VisualState visualState) {
        visualStateProperty().set(visualState);
    }
}
