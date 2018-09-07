/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
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
    @FXML
    private TableView<Pair<Element, Integer>> shortcutTable;
    @FXML
    private TableColumn<Pair<Element, Integer>, Integer> keyCol;
    @FXML
    private TableColumn<Pair<Element, Integer>, Element> elementCol;

    private Element lastSelected;

    public RightMenu() {
        Resources.loadFXMLAsRoot("RightMenu.fxml", this);
    }

    private static final List<Element> elementBindings = Arrays.asList(Element.CARBON,
            Element.HYDROGEN, Element.OXYGEN, Element.NITROGEN, Element.PHOSPHORUS,
            Element.SULFUR, Element.CHLORINE, Element.SODIUM, Element.BROMINE, Element.IODINE);

    @FXML
    private void initialize() {
        createElementMenu();
        ObservableList<Pair<Element, Integer>> list = FXCollections.observableArrayList();
        for (int i = 0; i < elementBindings.size(); i++) {
            list.add(new Pair<>(elementBindings.get(i), i));
        }
        shortcutTable.setItems(list);
        elementCol.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper(cdf.getValue().getKey()));
        keyCol.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper(cdf.getValue().getValue()));
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

    public void elementChoice() {
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
