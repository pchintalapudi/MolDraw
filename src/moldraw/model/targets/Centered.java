/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.targets;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;

/**
 *
 * @author prem
 */
public interface Centered {

    DoubleProperty centerXProperty();

    DoubleProperty centerYProperty();

    DoubleProperty centerZProperty();

    default double getCenterX() {
        return centerXProperty().get();
    }

    default double getCenterY() {
        return centerYProperty().get();
    }

    default double getCenterZ() {
        return centerZProperty().get();
    }

    default Point3D getCenter() {
        return new Point3D(getCenterX(), getCenterY(), getCenterZ());
    }

    default void setCenterX(double x) {
        if (!centerXProperty().isBound()) {
            centerXProperty().set(x);
        }
    }

    default void setCenterY(double y) {
        if (!centerYProperty().isBound()) {
            centerYProperty().set(y);
        }
    }

    default void setCenterZ(double z) {
        if (!centerZProperty().isBound()) {
            centerZProperty().set(z);
        }
    }

    default void setCenter(Point3D center) {
        setCenter(center.getX(), center.getY(), center.getZ());
    }

    default void setCenter(double x, double y, double z) {
        setCenterX(x);
        setCenterY(y);
        setCenterZ(z);
    }

    default void shiftCenterX(double x) {
        setCenterX(getCenterX() + x);
    }

    default void shiftCenterY(double y) {
        setCenterY(getCenterY() + y);
    }

    default void shiftCenterZ(double z) {
        setCenterZ(getCenterZ() + z);
    }

    default void shiftCenter(Point3D shift) {
        setCenter(shift.add(getCenter()));
    }

    default void shiftCenter(double x, double y, double z) {
        shiftCenterX(x);
        shiftCenterY(y);
        shiftCenterZ(z);
    }
}
