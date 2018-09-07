/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import moldraw.model.bonds.VisualState;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import moldraw.model.bonds.Bond;
import moldraw.model.bonds.BondState;
import moldraw.utils.ContextMenuBuilder;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class BondView2DController {

    private final DoubleProperty distanceProperty = new SimpleDoubleProperty(),
            angleProperty = new SimpleDoubleProperty(),
            startXProperty = new SimpleDoubleProperty(),
            startYProperty = new SimpleDoubleProperty(),
            zDifProperty = new SimpleDoubleProperty(),
            visibleOffsetProperty = new SimpleDoubleProperty(),
            invisibleOffsetProperty = new SimpleDoubleProperty();
    private final BooleanProperty startVisibleProperty = new SimpleBooleanProperty(true),
            endVisibleProperty = new SimpleBooleanProperty(true);
    private static final double visibleOffsetRatio = 2.5d / 7, invisibleOffsetRatio = 1.5d / 7,
            visibleOffsetConstant = 20, invisibleOffsetConstant = 10;
    private static final int bondOffset = 10;

    @FXML
    private Group root;
    @FXML
    private Line singleBond;
    @FXML
    private Line dashedBond;
    @FXML
    private Group doubleBondGroup;
    @FXML
    private Line doubleBond1;
    @FXML
    private Line doubleBond2;
    @FXML
    private Group tripleBondGroup;
    @FXML
    private Line tripleBond1;
    @FXML
    private Line tripleBond3;
    @FXML
    private Polygon triangle;
    @FXML
    private HBox receding;

    private final Bond bond;
    private final Reporter reporter;
    private final ContextMenu cm;

    public BondView2DController(Bond bond, Reporter reporter) {
        this.bond = bond;
        this.reporter = reporter;
        cm = ContextMenuBuilder.create()
                .addItem("Single Bond", () -> setBondState(BondState.SINGLE))
                .addItem("Double Bond", () -> setBondState(BondState.DOUBLE))
                .addItem("Triple Bond", () -> setBondState(BondState.TRIPLE))
                .addItem("Partial Bond", () -> setBondState(BondState.PARTIAL))
                .addItem("Delete", () -> {
                    if (onDelete != null) {
                        onDelete.run();
                    }
                }).build();
    }

    @FXML
    private void initialize() {
        root.setUserData(bond);
        visibleOffsetProperty.bind(Bindings.createDoubleBinding(()
                -> Math.min(visibleOffsetConstant, distanceProperty.get() * visibleOffsetRatio), distanceProperty));
        invisibleOffsetProperty.bind(Bindings.createDoubleBinding(()
                -> Math.min(invisibleOffsetConstant, distanceProperty.get() * invisibleOffsetRatio), distanceProperty));
        bindBondables();
        makeDashedBond();
        makeSingleBond();
        makeDoubleBond();
        makeTripleBond();
        makeTriangle();
        makeReceding();
    }

    private void bindBondables() {
        bond.startBondableProperty().addListener((o, b, s) -> {
            if (b != null) {
                startVisibleProperty.unbind();
                distanceProperty.unbind();
                angleProperty.unbind();
                zDifProperty.unbind();
            }
            if (s != null) {
                startVisibleProperty.bind(s.visibleProperty());
                rebindProperties();
            }
        });
        bond.endBondableProperty().addListener((o, b, s) -> {
            if (b != null) {
                endVisibleProperty.unbind();
                distanceProperty.unbind();
                angleProperty.unbind();
                zDifProperty.unbind();
            }
            if (s != null) {
                endVisibleProperty.bind(s.visibleProperty());
                rebindProperties();
            }
        });
        if (bond.getStartBondable() != null && bond.getEndBondable() != null) {
            startVisibleProperty.bind(bond.getStartBondable().visibleProperty());
            endVisibleProperty.bind(bond.getEndBondable().visibleProperty());
            rebindProperties();
        }
    }

    private void rebindProperties() {
        distanceProperty.bind(Bindings.createDoubleBinding(()
                -> Math.hypot(bond.getEndBondable().getCenterX()
                        - bond.getStartBondable().getCenterX(),
                        bond.getEndBondable().getCenterY()
                        - bond.getStartBondable().getCenterY()),
                bond.getStartBondable().centerXProperty(),
                bond.getStartBondable().centerYProperty(),
                bond.getEndBondable().centerXProperty(),
                bond.getEndBondable().centerYProperty()));
        angleProperty.bind(Bindings.createDoubleBinding(()
                -> Math.atan2(bond.getEndBondable().getCenterY()
                        - bond.getStartBondable().getCenterY(),
                        bond.getEndBondable().getCenterX()
                        - bond.getStartBondable().getCenterX()),
                bond.getStartBondable().centerXProperty(),
                bond.getStartBondable().centerYProperty(),
                bond.getEndBondable().centerXProperty(),
                bond.getEndBondable().centerYProperty()));
        startXProperty.bind(bond.getStartBondable().centerXProperty());
        startYProperty.bind(bond.getStartBondable().centerYProperty());
        zDifProperty.bind(bond.getEndBondable().centerZProperty().subtract(bond.getStartBondable().centerZProperty()));
    }

    private void makeSingleBond() {
        singleBond.visibleProperty().bind(Bindings.createBooleanBinding(()
                -> bond.getBondState() == BondState.SINGLE
                && bond.getVisualState() == VisualState.SHORT
                || bond.getVisualState() == VisualState.NONE //                && Utils.fuzzyEquals(zDifProperty.get(), 0, 2)
                ,
                 bond.bondStateProperty(), /*zDifProperty*/ bond.visualStateProperty()));
    }

    private void makeDoubleBond() {
        doubleBondGroup.visibleProperty().bind(bond.bondStateProperty().isEqualTo(BondState.DOUBLE));
        doubleBond1.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            switch (bond.getVisualState()) {
                case LEFT:
                case RIGHT:
                    if (startVisibleProperty.get()) {
                        return startXProperty.get() + visibleOffsetProperty.get();
                    } else {
                        return startXProperty.get() + invisibleOffsetProperty.get();
                    }
                default:
                    return startXProperty.get();
            }
        }, startXProperty, startVisibleProperty, bond.visualStateProperty(), visibleOffsetProperty, invisibleOffsetProperty));
        doubleBond1.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            switch (bond.getVisualState()) {
                case RIGHT:
                    return startYProperty.get() - bondOffset;
                default:
                    return startYProperty.get() + bondOffset;
            }
        }, bond.visualStateProperty(), startYProperty));
        doubleBond2.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            switch (bond.getVisualState()) {
                case RIGHT:
                case LEFT:
                    return startYProperty.get();
                default:
                    return startYProperty.get() - bondOffset;
            }
        }, bond.visualStateProperty(), startYProperty));
        doubleBond1.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            switch (bond.getVisualState()) {
                case LEFT:
                case RIGHT:
                    if (endVisibleProperty.get()) {
                        return startXProperty.get() + distanceProperty.get() - visibleOffsetProperty.get();
                    } else {
                        return startXProperty.get() + distanceProperty.get() - invisibleOffsetProperty.get();
                    }
                default:
                    return startXProperty.get() + distanceProperty.get();
            }
        }, startXProperty, endVisibleProperty, bond.visualStateProperty(), distanceProperty, visibleOffsetProperty, invisibleOffsetProperty));
    }

    private void makeTripleBond() {
        tripleBondGroup.visibleProperty().bind(bond.bondStateProperty().isEqualTo(BondState.TRIPLE));
        tripleBond1.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            if (bond.getVisualState() == VisualState.SHORT) {
                if (startVisibleProperty.get()) {
                    return startXProperty.get() + visibleOffsetProperty.get();
                } else {
                    return startXProperty.get() + invisibleOffsetProperty.get();
                }
            } else {
                return startXProperty.get();
            }
        }, bond.visualStateProperty(), startVisibleProperty, startXProperty, visibleOffsetProperty, invisibleOffsetProperty));
        tripleBond1.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            if (bond.getVisualState() == VisualState.SHORT) {
                if (endVisibleProperty.get()) {
                    return startXProperty.get() + distanceProperty.get() - visibleOffsetProperty.get();
                } else {
                    return startXProperty.get() + distanceProperty.get() - invisibleOffsetProperty.get();
                }
            } else {
                return startXProperty.get() + distanceProperty.get();
            }
        }, bond.visualStateProperty(), startXProperty, distanceProperty, endVisibleProperty, visibleOffsetProperty, invisibleOffsetProperty));
        tripleBond1.startYProperty().bind(startYProperty.subtract(bondOffset));
        tripleBond3.startYProperty().bind(startYProperty.add(bondOffset));
        tripleBond1.endYProperty().bind(tripleBond1.startYProperty());
        tripleBond3.endYProperty().bind(tripleBond3.startYProperty());
    }

    private void makeDashedBond() {
        dashedBond.visibleProperty().bind(bond.bondStateProperty().isEqualTo(BondState.PARTIAL));
    }

    private void makeTriangle() {
        triangle.visibleProperty().bind(singleBond.visibleProperty().not()
                .and(bond.bondStateProperty().isEqualTo(BondState.SINGLE)
                        .and(bond.visualStateProperty().isEqualTo(VisualState.RIGHT))));
        ObservableList<Double> pointsList = new ListBinding<Double>() {

            {
                super.bind(startXProperty, startYProperty, distanceProperty);
            }

            @Override
            protected ObservableList<Double> computeValue() {
                return FXCollections.observableArrayList(startXProperty.get(), startYProperty.get(),
                        startXProperty.get() + distanceProperty.get(), startYProperty.get() + bondOffset,
                        startXProperty.get() + distanceProperty.get(), startYProperty.get() - bondOffset);
            }
        };
        Bindings.bindContent(triangle.getPoints(), pointsList);
    }

    @SuppressWarnings("empty-statement")
    private void makeReceding() {
        receding.visibleProperty().bind(singleBond.visibleProperty().not()
                .and(bond.bondStateProperty().isEqualTo(BondState.SINGLE)
                        .and(bond.visualStateProperty().isEqualTo(VisualState.LEFT))));
        receding.translateXProperty().bind(startXProperty);
        receding.translateYProperty().bind(startYProperty.subtract(receding.heightProperty().divide(2)));
        receding.setAlignment(Pos.CENTER_LEFT);
        receding.setSpacing(4);
        distanceProperty.addListener((o, b, s) -> {
            for (int i = receding.getChildren().size(); i < s.intValue() / (receding.getSpacing() + rectangleWidth);
                    receding.getChildren().add(getRecedingRectangle(i++)));
            for (int i = receding.getChildren().size() - 1; i > s.intValue() / (receding.getSpacing() + rectangleWidth) - 1 && i > -1;
                    ((Rectangle) receding.getChildren().remove(i--)).heightProperty().unbind());
        });
        receding.getStyleClass().add("receding-group");
    }

    private static final double rectangleWidth = 4;

    private Rectangle getRecedingRectangle(int count) {
        Rectangle r = new Rectangle(0, 0, 0, 0);
        r.heightProperty().bind(Bindings.createDoubleBinding(() -> count * 10 * 1.5
                / receding.getChildren().size(), receding.getChildren()));
        r.widthProperty().set(rectangleWidth);
        r.setUserData(r.heightProperty());
        r.getStyleClass().add("receding-bond");
        return r;
    }

    @FXML
    private void onClick(MouseEvent m) {
        if (m.isStillSincePress()) {
            if (m.getButton() == MouseButton.PRIMARY) {
                cm.hide();
                VisualState prev = bond.getVisualState();
                VisualState next;
                switch (bond.getBondState()) {
                    case SINGLE:
                    case DOUBLE:
                        switch (bond.getVisualState()) {
                            case LEFT:
                                next = VisualState.RIGHT;
                                break;
                            case RIGHT:
                                next = VisualState.NONE;
                                break;
                            default:
                                next = VisualState.LEFT;
                                break;
                        }
                        break;
                    case TRIPLE:
                        switch (bond.getVisualState()) {
                            case SHORT:
                                next = VisualState.NONE;
                                break;
                            default:
                                next = VisualState.SHORT;
                                break;
                        }
                        break;
                    default:
                        next = prev;
                        break;
                }
                if (next != prev) {
                    bond.setVisualState(next);
                    reporter.reportUndoableEvent(() -> bond.setVisualState(next), () -> bond.setVisualState(prev));
                }
            } else if (m.getButton() == MouseButton.SECONDARY) {
                cm.show(root, m.getScreenX(), m.getScreenY());
                m.consume();
            }
        }
    }

    public DoubleProperty angleProperty() {
        return angleProperty;
    }

    public double getAngle() {
        return angleProperty.get();
    }

    public ObjectProperty<BondState> bondStateProperty() {
        return bond.bondStateProperty();
    }

    public BondState getBondState() {
        return bond.getBondState();
    }

    private void setBondState(BondState bs) {
        BondState prev = bond.getBondState();
        bond.setBondState(bs);
        reporter.reportUndoableEvent(() -> bond.setBondState(bs), () -> bond.setBondState(prev));
    }

    public DoubleProperty startXProperty() {
        return startXProperty;
    }

    public double getStartX() {
        return startXProperty.get();
    }

    public DoubleProperty startYProperty() {
        return startYProperty;
    }

    public double getStartY() {
        return startYProperty.get();
    }

    public DoubleProperty distanceProperty() {
        return distanceProperty;
    }

    public double getDistance() {
        return distanceProperty.get();
    }

    public Bond getBond() {
        return bond;
    }

    private Runnable onDelete;

    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;
    }
}
