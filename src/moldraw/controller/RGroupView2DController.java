package moldraw.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import moldraw.model.targets.Atom;
import moldraw.model.targets.RGroup;
import moldraw.utils.ContextMenuBuilder;
import moldraw.utils.ViewUtils;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class RGroupView2DController {

    @FXML
    private Pane root;

    private final StringProperty nameProperty = new SimpleStringProperty(),
            chargeProperty = new SimpleStringProperty();
    private final BooleanProperty nameVisibleProperty = new SimpleBooleanProperty(true);
    private final DoubleProperty centerXProperty = new SimpleDoubleProperty(),
            centerYProperty = new SimpleDoubleProperty();

    private final Reporter reporter;

    private final ChangeListener<Boolean> selectionListener = (o, b, s) -> {
        if (s) {
            root.getStyleClass().add("selected");
        } else {
            root.getStyleClass().remove("selected");
        }
    };

    private final ContextMenu cm;

    public RGroupView2DController(Reporter reporter) {
        rGroupProperty.addListener((o, b, s) -> {
            if (b != null) {
                centerXProperty.unbind();
                centerYProperty.unbind();
                nameProperty.unbind();
                chargeProperty.unbind();
                nameVisibleProperty.unbind();
                s.selectedProperty().removeListener(selectionListener);
            }
            if (s != null) {
                centerXProperty.bind(s.centerXProperty());
                centerYProperty.bind(s.centerYProperty());
                nameProperty.bind(s.nameProperty());
                chargeProperty.bind(Bindings.createStringBinding(() -> getFormattedCharge(s.getCharge()), s.chargeProperty()));
                nameVisibleProperty.bind(s.visibleProperty());
                s.selectedProperty().addListener(selectionListener);
            }
        });
        this.reporter = reporter;
        cm = makeContentMenu();
    }

    private ContextMenu makeContentMenu() {
        return ContextMenuBuilder.create().addItem("Add Charge", this::incrementCharge)
                .addItem("Subtract Charge", this::decrementCharge)
                .addItem("Join This", () -> reporter.reportJoin(root, getRGroup()))
                .addItem("Delete", () -> {
                    if (onDelete != null) {
                        onDelete.run();
                    }
                }
                ).build();
    }

    private static String getFormattedCharge(int charge) {
        return charge > 1 ? charge + "+" : charge < -1 ? Math.abs(charge) + "-" : charge == 1 ? "+" : charge == -1 ? "-" : "";
    }

    private final ObjectProperty<RGroup> rGroupProperty = new SimpleObjectProperty<>();

    public void setRGroup(RGroup r) {
        rGroupProperty.set(r);
    }

    public RGroup getRGroup() {
        return rGroupProperty.get();
    }

    public boolean isAtomController() {
        return getRGroup() instanceof Atom;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public String getName() {
        return nameProperty.get();
    }

    public StringProperty chargeProperty() {
        return chargeProperty;
    }

    public String getCharge() {
        return chargeProperty.get();
    }

    public BooleanProperty nameVisibleProperty() {
        return nameVisibleProperty;
    }

    public boolean isNameVisible() {
        return nameVisibleProperty.get();
    }

    public DoubleProperty centerXProperty() {
        return centerXProperty;
    }

    public double getCenterX() {
        return centerXProperty.get();
    }

    public DoubleProperty centerYProperty() {
        return centerYProperty;
    }

    public double getCenterY() {
        return centerYProperty.get();
    }

    private double lx, ly, cx, cy;

    @FXML
    private void onPress(MouseEvent m) {
        lx = m.getSceneX();
        ly = m.getSceneY();
        cx = getRGroup().getCenterX();
        cy = getRGroup().getCenterY();
        m.consume();
    }

    @FXML
    private void onDrag(MouseEvent m) {
        if (m.isPrimaryButtonDown()) {
            reporter.reportShift(getRGroup(), m.getSceneX() - lx, m.getSceneY() - ly);
            lx = m.getSceneX();
            ly = m.getSceneY();
            m.consume();
        }
    }

    @FXML
    private void onClick(MouseEvent m) {
        if (m.getButton() == MouseButton.PRIMARY) {
            if (!m.isStillSincePress()) {
                if (cm.isShowing()) {
                    cm.hide();
                } else {
                    reporter.reportTotalShift(getRGroup(), getRGroup().getCenterX() - cx, getRGroup().getCenterY() - cy, 0);
                }
                m.consume();
            } else {
                reporter.reportClick(root, getRGroup());
                m.consume();
            }
        } else if (m.getButton() == MouseButton.SECONDARY) {
            cm.show(root.getScene().getWindow(), m.getScreenX(), m.getScreenY());
            m.consume();
        }
    }

    public void highlight() {
        root.pseudoClassStateChanged(ViewUtils.HIGHLIGHTED, true);
    }

    public void dehighlight() {
        root.pseudoClassStateChanged(ViewUtils.HIGHLIGHTED, false);
    }

    private void incrementCharge() {
        shiftCharge(1);
    }

    private void decrementCharge() {
        shiftCharge(-1);
    }

    private void shiftCharge(int amount) {
        getRGroup().setCharge(getRGroup().getCharge() + amount);
    }

    private Runnable onDelete;

    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;
    }
}
