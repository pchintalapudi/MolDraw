/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

/**
 *
 * @author prem
 */
public class Line3D extends Cylinder {

    private final DoubleProperty rho = new SimpleDoubleProperty();
    private final Rotate phi = new Rotate(), theta = new Rotate();

    private final DoubleProperty startXProperty = new SimpleDoubleProperty();
    private final DoubleProperty startYProperty = new SimpleDoubleProperty();
    private final DoubleProperty startZProperty = new SimpleDoubleProperty();
    private final DoubleProperty endXProperty = new SimpleDoubleProperty();
    private final DoubleProperty endYProperty = new SimpleDoubleProperty();
    private final DoubleProperty endZProperty = new SimpleDoubleProperty();

    private void bindProperties() {
        translateXProperty().bind(endXProperty.add(startXProperty).divide(2));
        translateYProperty().bind(endYProperty.add(startYProperty).divide(2));
        translateZProperty().bind(endZProperty.add(startZProperty).divide(2));
        rho.bind(Bindings.createDoubleBinding(() -> Math.sqrt(square(getEndX()
                - getStartX()) + square(getEndY() - getStartY()) + square(getEndZ()
                - getStartZ())), startXProperty, startYProperty, startZProperty,
                endXProperty, endYProperty, endZProperty));
        heightProperty().bind(rho);
        phi.angleProperty().bind(Bindings.createDoubleBinding(() -> Math.toDegrees(
                Math.acos((getEndZ() - getStartZ()) / Math.sqrt(square(getEndX()
                        - getStartX()) + square(getEndY() - getStartY())))) - 90,
                startXProperty, startYProperty, startZProperty, endXProperty,
                endYProperty, endZProperty));
        phi.setAxis(Rotate.X_AXIS);
        theta.angleProperty().bind(Bindings.createDoubleBinding(() -> Math.toDegrees(
                Math.atan2(getEndY() - getStartY(), getEndX() - getStartX())) + 90,
                startXProperty, startYProperty, endXProperty, endYProperty));
        getTransforms().addAll(phi, theta);
        setRadius(4.5);
    }

    public Line3D() {
        bindProperties();
    }

    private static double square(double a) {
        return a * a;
    }

    public final double getStartX() {
        return startXProperty.get();
    }

    public final void setStartX(double value) {
        startXProperty.set(value);
    }

    public DoubleProperty startXProperty() {
        return startXProperty;
    }

    public final double getStartY() {
        return startYProperty.get();
    }

    public final void setStartY(double value) {
        startYProperty.set(value);
    }

    public DoubleProperty startYProperty() {
        return startYProperty;
    }

    public final double getStartZ() {
        return startZProperty.get();
    }

    public final void setStartZ(double value) {
        startZProperty.set(value);
    }

    public DoubleProperty startZProperty() {
        return startZProperty;
    }

    public final double getEndX() {
        return endXProperty.get();
    }

    public final void setEndX(double value) {
        endXProperty.set(value);
    }

    public DoubleProperty endXProperty() {
        return endXProperty;
    }

    public final double getEndY() {
        return endYProperty.get();
    }

    public final void setEndY(double value) {
        endYProperty.set(value);
    }

    public DoubleProperty endYProperty() {
        return endYProperty;
    }

    public final double getEndZ() {
        return endZProperty.get();
    }

    public final void setEndZ(double value) {
        endZProperty.set(value);
    }

    public DoubleProperty endZProperty() {
        return endZProperty;
    }

    public Point3D getStartPoint() {
        return new Point3D(getStartX(), getStartY(), getStartZ());
    }

    public Point3D getEndPoint() {
        return new Point3D(getEndX(), getEndY(), getEndZ());
    }

    public void setStart(double x, double y, double z) {
        setStartX(x);
        setStartY(y);
        setStartZ(z);
    }

    public void setEnd(double x, double y, double z) {
        setEndX(x);
        setEndY(y);
        setEndZ(z);
    }

    public void bindStart(ObservableNumberValue xValue, ObservableNumberValue yValue, ObservableNumberValue zValue) {
        startXProperty.bind(xValue);
        startYProperty.bind(yValue);
        startZProperty.bind(zValue);
    }

    public void bindEnd(ObservableNumberValue xValue, ObservableNumberValue yValue, ObservableNumberValue zValue) {
        endXProperty.bind(xValue);
        endYProperty.bind(yValue);
        endZProperty.bind(zValue);
    }
}
