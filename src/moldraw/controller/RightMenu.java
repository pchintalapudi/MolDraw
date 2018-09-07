/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.util.EnumSet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import moldraw.model.targets.Element;
import moldraw.resources.Resources;
import moldraw.utils.Utils;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RightMenu extends VBox {

    @FXML
    private SplitMenuButton elementChoice;
    @FXML
    private AngleAssist angleAssist;
    @FXML
    private RotateAssist rotateAssist;
    @FXML
    private Minimap minimap;

    private Element lastSelected;

    public RightMenu() {
        Resources.loadFXMLAsRoot("RightMenu.fxml", this);
    }

    @FXML
    private void initialize() {
        createElementMenu();
    }

    private Reporter reporter;

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
        angleAssist.setReporter(reporter);
        rotateAssist.setReporter(reporter);
    }

    private void createElementMenu() {
        EnumSet.allOf(Element.class).stream().map(e -> {
            MenuItem generate = new MenuItem("Make " + Utils.title(e.toString()) + " (" + Utils.title(Element.getAbbrev(e)) + ")");
            generate.setOnAction(ae -> {
                lastSelected = e;
                elementChoice.setText(generate.getText());
                elementChoice.fire();
            });
            return generate;
        }).forEach(elementChoice.getItems()::add);
    }

    @FXML
    private void elementChoice() {
        if (lastSelected == null) {
            elementChoice.show();
        } else {
            reporter.reportMakeAtom(lastSelected);
        }
    }

    public void bindMinimap(ScrollPane bound) {
        minimap.bind(bound);
    }

    public void updateMinimapImage(Image i) {
        minimap.updateImage(i);
    }
}
