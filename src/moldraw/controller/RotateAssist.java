/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import moldraw.resources.Resources;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RotateAssist extends VBox {

    @FXML
    private TextField angleField;
    @FXML
    private Hyperlink lastAngle;

    private final DoubleProperty lastAngleProperty = new SimpleDoubleProperty();

    private Reporter reporter;
    
    public RotateAssist() {
        Resources.loadFXMLAsRoot("RotateAssist.fxml", this);
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @FXML
    private void rotate() {
        try {
            lastAngleProperty.set(Double.parseDouble(angleField.getText()));
            lastAngle();
            angleField.clear();
        } catch (NumberFormatException ex) {
        }
    }

    @FXML
    private void lastAngle() {
        reporter.reportRotate(Math.toRadians(lastAngleProperty.get()));
    }

}
