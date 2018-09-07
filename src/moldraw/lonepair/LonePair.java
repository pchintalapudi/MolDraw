/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.lonepair;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import moldraw.model.bonds.Bondable;

/**
 *
 * @author prem
 */
public class LonePair {

    private final Bondable bondable;
    private final DoubleProperty angle = new SimpleDoubleProperty();

    public LonePair(Bondable bondable) {
        this.bondable = bondable;
    }
}
