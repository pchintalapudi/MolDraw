/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import moldraw.Debug;
import moldraw.model.targets.Centered;
import moldraw.resources.Resources;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class AngleWidget extends Pane implements Centered {

    @FXML
    private Label angleLabel;
    @FXML
    private TextField angleEdit;

    private final DoubleProperty angleProperty = new SimpleDoubleProperty(),
            centerXProperty = new SimpleDoubleProperty(),
            centerYProperty = new SimpleDoubleProperty(),
            centerZProperty = new SimpleDoubleProperty(),
            lengthProperty = new SimpleDoubleProperty(75);
    private boolean report, reset;
    private Reporter reporter;

    public AngleWidget() {
        Resources.loadFXMLAsRoot("AngleWidget.fxml", this);
        translateXProperty().bind(centerXProperty);
        translateYProperty().bind(centerYProperty);
        angleEdit.translateXProperty().bind(lengthProperty.multiply(Math.sqrt(2) / 2).add(10));
        angleLabel.translateXProperty().bind(angleEdit.translateXProperty().add(10));
        angleEdit.translateYProperty().bind(lengthProperty.multiply(Math.sqrt(2) / 2)
                .add(angleEdit.heightProperty()).add(10).negate());
        angleLabel.translateYProperty().bind(lengthProperty.multiply(Math.sqrt(2) / 2)
                .add(angleLabel.heightProperty()).add(10).negate());
        angleLabel.textProperty().bind(Bindings.createStringBinding(()
                -> String.format("%3.2f°", Math.toDegrees(angleProperty.get())), angleProperty));
        angleEdit.textProperty().addListener((o, b, s) -> {
            if (!s.isEmpty()) {
                try {
                    Double.parseDouble(s);
                    if (reset) {
                        reset = false;
                    } else {
                        report = true;
                    }
                } catch (NumberFormatException expected) {
                    reset = true;
                    Platform.runLater(() -> angleEdit.setText(b));
                }
            } else {
                report = false;
            }
        });
        visibleProperty().addListener((o, b, s) -> {
            if (!s) {
                angleLabel.setVisible(true);
            }
        });
    }

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

    public double getLength() {
        return lengthProperty.get();
    }

    public void setLength(double length) {
        lengthProperty.set(length);
    }

    public DoubleProperty lengthProperty() {
        return lengthProperty;
    }

    public DoubleProperty angleProperty() {
        return angleProperty;
    }

    public double getAngle() {
        return angleProperty.get();
    }

    public double getEndX() {
        return getLength() * Math.cos(getAngle()) + getTranslateX();
    }

    public double getEndY() {
        return -getLength() * Math.sin(getAngle()) + getTranslateY();
    }

    @FXML
    private void labelClick(MouseEvent m) {
        if (m.getClickCount() > 1) {
            angleLabel.setVisible(false);
            angleEdit.requestFocus();
        }
        m.consume();
    }

    @FXML
    private void finishEdit() {
        angleLabel.setVisible(true);
        if (report && !angleEdit.getText().isEmpty()) {
            reporter.reportHardAngle(Math.toRadians(Double.parseDouble(angleEdit.getText())));
        }
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }
}
