/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import moldraw.resources.Resources;
import moldraw.utils.Utils;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class AngleAssist extends VBox {

    @FXML
    private Slider sideSlider;
    @FXML
    private TextField angleEdit;
    @FXML
    private ListView<Double> resultBox;

    private final DoubleProperty angleProperty = new SimpleDoubleProperty();

    private Reporter reporter;

    public AngleAssist() {
        Resources.loadFXMLAsRoot("AngleAssist.fxml", this);
    }

    @FXML
    private void initialize() {
        resultBox.setCellFactory(lv -> {
            ListCell<Double> lc = new ListCell<Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.format("%3.2f°", item));
                    }
                }
            };
            lc.setOnMouseClicked(m -> {
                if (m.isStillSincePress() && m.getClickCount() > 1 && m.getButton() == MouseButton.PRIMARY) {
                    reporter.reportHardAngle(Math.toRadians(lc.getItem()));
                }
            });
            return lc;
        });
        resultBox.setItems(new ListBinding<Double>() {

            {
                super.bind(sideSlider.valueProperty(), angleProperty);
            }

            @Override
            protected ObservableList<Double> computeValue() {
                return FXCollections.observableArrayList(getAngles());
            }
        });
        angleEdit.textProperty().addListener((o, b, s) -> {
            switch (s) {
                case "":
                case "-":
                case "-.":
                case ".":
                    break;
                default:
                    try {
                        Double.parseDouble(s);
                    } catch (NumberFormatException ex) {
                        Platform.runLater(() -> angleEdit.setText(b));
                    }
                    break;
            }
        });
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @FXML
    private void onEditFinish() {
        try {
            angleProperty.set(Double.parseDouble(angleEdit.getText()));
        } catch (NumberFormatException ex) {
            angleEdit.setText(String.format("%3." + DECIMAL_PLACES + "f", angleProperty.getValue()));
        }
    }

    private static final int DECIMAL_PLACES = 2;

    private List<Double> getAngles() {
        double sides = Math.round(sideSlider.getValue());
        double interiorAngle = (1 - 2.0 / sides) * 180;
        double start = interiorAngle / 2 + angleProperty.get();
        Set<Double> angles = new HashSet<>();
        for (int i = 0; i < sides; i++) {
            double angle = (start + i * interiorAngle + 360) % 360;
            double accepted = formalizeAngle(angle);
            angles.add(accepted);
            double angle180 = (angle + 180) % 360;
            double accepted180 = formalizeAngle(angle180);
            angles.add(accepted180);
        }
        for (int i = 0; i < sides; i++) {

        }
        if (angles.remove(-180d)) {
            angles.add(180d);
        }
        List<Double> sorted = new ArrayList<>(angles);
        Collections.sort(sorted, (d1, d2) -> Double.compare(Math.abs(d1), Math.abs(d2)));
        return sorted;
    }

    private static double formalizeAngle(double raw) {
        return Utils.integerize(Utils.rounded(raw > 180 ? raw - 360 : raw, DECIMAL_PLACES), DECIMAL_PLACES);
    }
}
