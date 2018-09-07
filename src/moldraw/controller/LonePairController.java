/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.lang.ref.WeakReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import moldraw.model.bonds.Bondable;

/**
 *
 * @author prem
 */
public class LonePairController {

    private final BooleanProperty leftVisibleProperty = new SimpleBooleanProperty(),
            rightVisibleProperty = new SimpleBooleanProperty();
    private final DoubleProperty radiusProperty = new SimpleDoubleProperty(),
            translateXProperty = new SimpleDoubleProperty(),
            translateYProperty = new SimpleDoubleProperty(),
            angleProperty = new SimpleDoubleProperty(),
            pivotXProperty = new SimpleDoubleProperty(),
            pivotYProperty = new SimpleDoubleProperty();

    public double getRadius() {
        return radiusProperty.get();
    }

    public DoubleProperty radiusProperty() {
        return radiusProperty;
    }

    public boolean isLeftVisible() {
        return leftVisibleProperty.get();
    }

    public BooleanProperty leftVisibleProperty() {
        return leftVisibleProperty;
    }

    public boolean isRightVisible() {
        return rightVisibleProperty.get();
    }

    public BooleanProperty rightVisibleProperty() {
        return rightVisibleProperty;
    }

    public double getTranslateX() {
        return translateXProperty.get();
    }

    public DoubleProperty translateXProperty() {
        return translateXProperty;
    }

    public double getTranslateY() {
        return translateYProperty.get();
    }

    public DoubleProperty translateYProperty() {
        return translateYProperty;
    }

    public double getAngle() {
        return angleProperty.get();
    }

    public DoubleProperty angleProperty() {
        return angleProperty;
    }

    public double getPivotX() {
        return pivotXProperty.get();
    }

    public double getPivotY() {
        return pivotYProperty.get();
    }
}
