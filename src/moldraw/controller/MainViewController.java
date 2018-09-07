/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import moldraw.files.FileUI;
import moldraw.files.Marshalling;
import moldraw.model.Model;
import moldraw.model.bonds.Bond;
import moldraw.model.bonds.BondImpl;
import moldraw.model.targets.Atom;
import moldraw.model.targets.RGroup;
import moldraw.model.targets.Element;
import moldraw.utils.Properties;
import moldraw.resources.Resources;
import moldraw.utils.Constants;
import moldraw.utils.Identifiable;
import moldraw.utils.ViewUtils;
import moldraw.xml.XMLData;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class MainViewController implements Reporter {

    /*
    ============================================================================
    
    Layout FXML-injected properties
    
    ============================================================================
     */
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane center;
    @FXML
    private Group bondGroup;
    @FXML
    private Group rGroupGroup;
    @FXML
    private ScrollPane scroll2d;
    @FXML
    private ScrollPane scroll3d;
    @FXML
    private Group drawingPane2d;
    @FXML
    private Rectangle backing2d;
    @FXML
    private RightMenu rightMenu;
    @FXML
    private Rectangle selectRectangle;
    @FXML
    private AngleWidget angleWidget;

    /*
    ============================================================================
    
    Construction
    
    ============================================================================
     */
    private final Properties properties = Properties.create();
    private final DoubleProperty angleProperty = new SimpleDoubleProperty();

    public MainViewController() {
        fillCancellableTasks();
    }

    @FXML
    private void initialize() {
        scroll2d.focusedProperty().addListener((o, b, s) -> {
            if (s) {
                root.requestFocus();
            }
        });
        scroll3d.focusedProperty().addListener((o, b, s) -> {
            if (s) {
                root.requestFocus();
            }
        });
        EventHandler<KeyEvent> filter = this::sceneKeyCapture;
        root.sceneProperty().addListener((o, b, s) -> {
            if (b != null) {
                b.removeEventFilter(KeyEvent.KEY_PRESSED, filter);
            }
            if (s != null) {
                s.addEventFilter(KeyEvent.KEY_PRESSED, filter);
                root.applyCss();
                updateMinimap();
            }
        });
        angleWidget.angleProperty().bind(angleProperty);
        angleWidget.setReporter(this);
        rightMenu.setReporter(this);
        rightMenu.bindMinimap(scroll2d);
    }

    /*
    ============================================================================
    
    FXML Method Handlers
    
    ============================================================================
     */
    private double dpx, dpy, dmx, dmy, psx, psy;
    private boolean addToSelection;

    @FXML
    private void drawPress(MouseEvent m) {
        dpx = m.getX();
        dpy = m.getY();
        Point2D mouseCenterCoords = center.sceneToLocal(m.getSceneX(), m.getSceneY());
        selectRectangle.setX(psx = mouseCenterCoords.getX());
        selectRectangle.setY(psy = mouseCenterCoords.getY());
    }

    @FXML
    private void drawDrag(MouseEvent m) {
        if (m.isPrimaryButtonDown()) {
            Point2D mouseCenterCoords = center.sceneToLocal(m.getSceneX(), m.getSceneY());
            cancelExcluding(EnumSet.of(Cancellable.SELECTION));
            addToSelection = m.isShiftDown();
            selectRectangle.setVisible(true);
            selectRectangle.setX(Math.max(Math.min(psx, mouseCenterCoords.getX()), 0));
            selectRectangle.setY(Math.max(Math.min(psy, mouseCenterCoords.getY()), 0));
            selectRectangle.setWidth(Math.min(Math.abs(psx - Math.max(mouseCenterCoords.getX(), 0)),
                    center.getWidth() - selectRectangle.getX()));
            selectRectangle.setHeight(Math.min(Math.abs(psy - Math.max(mouseCenterCoords.getY(), 0)),
                    center.getHeight() - selectRectangle.getY()));
        }
    }

    @FXML
    private void drawClick(MouseEvent m) {
        if (m.getButton() == MouseButton.PRIMARY) {
            if (!m.isStillSincePress()) {
                select();
            } else if (hangingRGroup != null && hangingRGroupNode != null) {
                if (hangingBond == null && hangingBondNode == null) {
                    placeRGroup(m.getX(), m.getY());
                } else {
                    placeRGroup(hangingRGroup.getCenterX(), hangingRGroup.getCenterY());
                }
            }
            selectRectangle.setVisible(false);
        }
    }

    @FXML
    private void drawMove(MouseEvent m) {
        if (hangingRGroup != null) {
            if (hangingBond == null) {
                hangingRGroup.setCenter(m.getX(), m.getY(), 0);
            } else {
                if (!hardcodedAngle) {
                    updateAngle(m.getX(), m.getY(), angleWidget.getCenterX(), angleWidget.getCenterY());
                }
                hangingRGroup.setCenter(angleWidget.getEndX(), angleWidget.getEndY(), 0);
            }
        }
        dmx = m.getX();
        dmy = m.getY();
        if (selectRectangle.isVisible()) {
            select();
            selectRectangle.setVisible(false);
        }
    }

    /*
    ============================================================================
    
    Undo/Redo
    
    ============================================================================
     */
    @FXML
    private void undo() {
        if (properties.getUndoManager().canUndo()) {
            cancel();
            properties.getUndoManager().undo();
            updateMinimap();
        }
    }

    @FXML
    private void redo() {
        if (properties.getUndoManager().canRedo()) {
            cancel();
            properties.getUndoManager().redo();
            updateMinimap();
        }
    }

    /*
    ============================================================================
    
    Special Key Handling
    
    ============================================================================
     */
    private void sceneKeyCapture(KeyEvent k) {
        switch (k.getCode()) {
            case ESCAPE:
                cancel();
                k.consume();
                break;
            case ENTER:
            case SPACE:
                rightMenu.elementChoice();
                break;
            case DELETE:
                deleteSelected();
                break;
            case NUMPAD0:
                reportMakeAtom(Element.CARBON);
                break;
            case NUMPAD1:
                reportMakeAtom(Element.HYDROGEN);
                break;
            case NUMPAD2:
                reportMakeAtom(Element.OXYGEN);
                break;
            case NUMPAD3:
                reportMakeAtom(Element.NITROGEN);
                break;
            case NUMPAD4:
                reportMakeAtom(Element.PHOSPHORUS);
                break;
            case NUMPAD5:
                reportMakeAtom(Element.SULFUR);
                break;
            case NUMPAD6:
                reportMakeAtom(Element.CHLORINE);
                break;
            case NUMPAD7:
                reportMakeAtom(Element.SODIUM);
                break;
            case NUMPAD8:
                reportMakeAtom(Element.BROMINE);
                break;
            case NUMPAD9:
                reportMakeAtom(Element.IODINE);
        }
    }

    /*
    ============================================================================
    
    Basic Window Actions
    
    ============================================================================
     */
    @FXML
    private void close() {
        root.getScene().getWindow().hide();
    }

    /*
    ============================================================================
    
    Cancellation implementation
    
    ============================================================================
     */
    private final Map<Cancellable, Runnable> cancellableTasks = new EnumMap<>(Cancellable.class);
    private final Set<Cancellable> cancellables = EnumSet.noneOf(Cancellable.class);

    private void fillCancellableTasks() {
        cancellableTasks.put(Cancellable.SELECTION, this::deselectAll);
        cancellableTasks.put(Cancellable.PLACE_RGROUP, this::cancelRGroupPlacement);
        cancellableTasks.put(Cancellable.CONSTRUCT_BOND, this::cancelBondConstruction);
    }

    private void cancel() {
        cancellables.stream().map(cancellableTasks::get).forEach(Runnable::run);
        cancellables.clear();
    }

    private void cancel(Cancellable toCancel) {
        if (cancellables.remove(toCancel)) {
            cancellableTasks.get(toCancel).run();
        }
    }

    private void cancelExcluding(Set<Cancellable> ignore) {
        ignore.retainAll(cancellables);
        cancellables.removeIf(ignore::contains);
        cancel();
        cancellables.addAll(ignore);
    }

    private static enum Cancellable {
        SELECTION, PLACE_RGROUP, CONSTRUCT_BOND;
    }

    /*
    ============================================================================
    
    Selection
    
    ============================================================================
     */
    private void select() {
        if (!addToSelection) {
            deselectAll();
        }
        Bounds rectangleBounds = selectRectangle.localToScene(selectRectangle.getBoundsInLocal());
        if (rGroupGroup.getChildren().stream().filter(n -> rectangleBounds
                .contains(n.localToScene(n.getBoundsInLocal())))
                .map(rGroupControllerMap::get).map(RGroupView2DController::getRGroup)
                .peek(model::select).count() > 0) {
            cancellables.add(Cancellable.SELECTION);
        }
    }

    @FXML
    private void selectAll() {
        model.selectAll();
        cancellables.add(Cancellable.SELECTION);
    }

    private void deselectAll() {
        model.deselectAll();
        cancellables.remove(Cancellable.SELECTION);
    }

    private void deleteSelected() {
        Set<RGroup> selectedRGroups = model.getSelectedCopy();
        Set<Bond> deletedBonds = selectedRGroups.stream().map(model::delete).flatMap(Set::stream).collect(Collectors.toSet());
        Set<Node> rGroupNodes = root.lookupAll(".selected");
        rGroupNodes.forEach(rGroupGroup.getChildren()::remove);
        Set<Node> bondNodes = root.lookupAll(".bond").stream().filter(n
                -> deletedBonds.contains((Bond) n.getUserData()))
                .peek(bondGroup.getChildren()::remove).collect(Collectors.toSet());
        properties.getUndoManager().log(() -> {
            bondGroup.getChildren().removeAll(bondNodes);
            rGroupGroup.getChildren().removeAll(rGroupNodes);
            selectedRGroups.forEach(model::delete);
        }, () -> {
            selectedRGroups.forEach(model::add);
            deletedBonds.forEach(model::add);
            rGroupGroup.getChildren().addAll(rGroupNodes);
            bondGroup.getChildren().addAll(bondNodes);
        });
        updateMinimap();
    }

    /*
    ============================================================================
    
    Model Specific Stuff
    
    ============================================================================
     */
    private final Model model = Model.create();

    private final Map<Node, RGroupView2DController> rGroupControllerMap = new HashMap<>();

    /*
    ============================================================================
    
    Atom methods
    
    ============================================================================
     */
    private RGroup hangingRGroup;
    private Node hangingRGroupNode;

    private void makeAtom(Element e) {
        if (hangingRGroup != null && hangingBond == null) {
            cancel(Cancellable.PLACE_RGROUP);
        }
        if (hangingRGroup == null) {
            Atom a = model.createAtom(e);
            a.setCenter(dpx, dpy, 0);
            generateRGroupView(a);
        }
    }

    private void generateRGroupView(RGroup r) {
        if (hangingRGroup == null) {
            FXMLLoader loader = Resources.getLoader("RGroupView2D.fxml");
            loader.setControllerFactory(clz -> new RGroupView2DController(this));
            Node n = hangingRGroupNode = Resources.load(loader);
            n.setMouseTransparent(true);
            RGroupView2DController controller = loader.getController();
            controller.setRGroup(r);
            rGroupControllerMap.put(n, controller);
            rGroupGroup.getChildren().add(n);
            hangingRGroup = r;
            cancelExcluding(EnumSet.of(Cancellable.PLACE_RGROUP));
            cancellables.add(Cancellable.PLACE_RGROUP);
            controller.setOnDelete(() -> delete(r, n));
        }
    }

    private void placeRGroup(double x, double y) {
        hangingRGroupNode.setMouseTransparent(false);
        RGroup placed = hangingRGroup;
        Node placedNode = hangingRGroupNode;
        RGroupView2DController av2dc = rGroupControllerMap.get(placedNode);
        model.add(placed);
        hangingRGroup.setCenter(x, y, 0);
        hangingRGroup = null;
        hangingRGroupNode = null;
        if (hangingBond == null && hangingBondNode == null) {
            finishRGroupPlacement(placed, placedNode, av2dc);
        } else {
            finishBondPlacement(placed, placedNode, av2dc);
        }
    }

    private void finishRGroupPlacement(RGroup placed, Node placedNode, RGroupView2DController av2dc) {
        properties.getUndoManager().log(() -> {
            model.add(placed);
            rGroupGroup.getChildren().add(placedNode);
            rGroupControllerMap.put(placedNode, av2dc);
        }, () -> {
            model.delete(placed);
            rGroupGroup.getChildren().remove(placedNode);
            rGroupControllerMap.remove(placedNode);
        });
        cancellables.remove(Cancellable.PLACE_RGROUP);
        updateMinimap();
        if (av2dc.isAtomController()) {
            makeAtom(((Atom) placed).getElement());
        }
    }

    private void switchRGroup(RGroupView2DController av2dc) {
        RGroup prev = av2dc.getRGroup(), next = hangingRGroup;
        next.setCenter(prev.getCenter());
        properties.getUndoManager().doAndLog(() -> {
            av2dc.setRGroup(next);
            model.replace(prev, next);
        }, () -> {
            av2dc.setRGroup(prev);
            model.replace(next, prev);
        });
        rGroupGroup.getChildren().remove(hangingRGroupNode);
        rGroupControllerMap.remove(hangingRGroupNode);
        hangingRGroupNode = null;
        hangingRGroup = null;
        av2dc.highlight();
        ViewUtils.delayFX(100, () -> {
            av2dc.dehighlight();
            updateMinimap();
        });
        if (av2dc.isAtomController()) {
            makeAtom(((Atom) next).getElement());
        }
    }

    private void cancelRGroupPlacement() {
        rGroupGroup.getChildren().remove(hangingRGroupNode);
        rGroupControllerMap.remove(hangingRGroupNode);
        hangingRGroup = null;
        hangingRGroupNode = null;
    }

    private void delete(RGroup rg, Node n) {
        Set<Bond> deletedBonds = model.delete(rg);
        rGroupGroup.getChildren().remove(n);
        Set<Node> bondNodes = root.lookupAll(".bond").stream().filter(node
                -> deletedBonds.contains((Bond) node.getUserData()))
                .peek(bondGroup.getChildren()::remove).collect(Collectors.toSet());
        properties.getUndoManager().log(() -> {
            bondGroup.getChildren().removeAll(bondNodes);
            rGroupGroup.getChildren().remove(n);
            model.delete(rg);
        }, () -> {
            model.add(rg);
            deletedBonds.forEach(model::add);
            rGroupGroup.getChildren().add(n);
            bondGroup.getChildren().addAll(bondNodes);
        });
    }

    /*
    ============================================================================
    
    Bond methods
    
    ============================================================================
     */
    private Bond hangingBond;
    private Node hangingBondNode;

    private void makeBond(RGroup r) {
        if (hangingRGroup == null && hangingRGroupNode == null
                && hangingBond == null && hangingBondNode == null) {
            cancelExcluding(EnumSet.of(Cancellable.CONSTRUCT_BOND));
            Pair<Bond, Atom> p = model.getBond(r, Element.CARBON);
            hangingBond = p.getKey();
            generateRGroupView(p.getValue());
            FXMLLoader loader = Resources.getLoader("BondView2D.fxml");
            loader.setControllerFactory(cls -> new BondView2DController(hangingBond, this));
            Node n = hangingBondNode = Resources.load(loader);
            BondView2DController controller = loader.getController();
            bondGroup.getChildren().add(hangingBondNode);
            cancellables.add(Cancellable.CONSTRUCT_BOND);
            angleWidget.setCenterX(r.getCenterX());
            angleWidget.setCenterY(r.getCenterY());
            angleWidget.setVisible(true);
            controller.setOnDelete(() -> delete(p.getKey(), n));
        }
    }

    private void finishBondPlacement(RGroup placed, Node placedNode, RGroupView2DController av2dc) {
        Bond connected = hangingBond;
        Node connectedNode = hangingBondNode;
        properties.getUndoManager().log(() -> {
            model.add(placed);
            rGroupGroup.getChildren().add(placedNode);
            rGroupControllerMap.put(placedNode, av2dc);
            bondGroup.getChildren().add(connectedNode);
            model.add(connected);
        }, () -> {
            model.delete(placed);
            rGroupGroup.getChildren().remove(placedNode);
            rGroupControllerMap.remove(placedNode);
            bondGroup.getChildren().remove(connectedNode);
            model.delete(connected);
        });
        hangingBond = null;
        hangingBondNode = null;
        angleWidget.setVisible(false);
        hardcodedAngle = false;
        cancellables.remove(Cancellable.CONSTRUCT_BOND);
        updateMinimap();
    }

    private void completeBondOnTarget(RGroup target) {
        hangingBond.setEndBondable(target);
        rGroupGroup.getChildren().remove(hangingRGroupNode);
        rGroupControllerMap.remove(hangingRGroupNode);
        model.delete(hangingRGroup);
        Bond placed = hangingBond;
        Node hangingNode = hangingBondNode;
        properties.getUndoManager().log(() -> {
            model.add(placed);
            bondGroup.getChildren().add(hangingNode);
        }, () -> {
            model.delete(placed);
            bondGroup.getChildren().remove(hangingNode);
        });
        cancellables.remove(Cancellable.CONSTRUCT_BOND);
        hangingRGroup = null;
        hangingRGroupNode = null;
        hangingBond = null;
        hangingBondNode = null;
        angleWidget.setVisible(false);
        hardcodedAngle = false;
        updateMinimap();
    }

    private boolean hardcodedAngle;

    private void updateAngle(double endX, double endY, double startX, double startY) {
        if (hardcodedAngle) {
            return;
        }
        double angle = Math.atan2(startY - endY, endX - startX);//The scene graph has y inverted
        double radius = Math.hypot(endX - startX, endY - startY);
        for (double a : Constants.getNiceAngles()) {
            //Chord function: crd(theta) = 2 * sin(theta / 2)
            double crd = Math.abs(2 * Math.sin(Math.toRadians((a - Math.toDegrees(angle)) / 2)));
            if (Math.abs(crd * radius) < 15) {
                angle = Math.toRadians(a > 180 ? a - 360 : a);
                break;
            }
        }
        angleProperty.set(angle);
    }

    private void cancelBondConstruction() {
        model.delete(hangingRGroup);
        rGroupControllerMap.remove(hangingRGroupNode);
        rGroupGroup.getChildren().remove(hangingRGroupNode);
        bondGroup.getChildren().remove(hangingBondNode);
        angleWidget.centerXProperty().unbind();
        angleWidget.centerYProperty().unbind();
        angleWidget.setVisible(false);
        hangingRGroup = null;
        hangingRGroupNode = null;
        hangingBond = null;
        hangingBondNode = null;
        hardcodedAngle = false;
    }

    private void delete(Bond b, Node n) {
        properties.getUndoManager().doAndLog(() -> {
            model.delete(b);
            bondGroup.getChildren().remove(n);
        }, () -> {
            model.add(b);
            bondGroup.getChildren().add(n);
        });
    }

    /*
    ============================================================================
    
    Joining
    
    ============================================================================
     */
    private RGroup joinGroup;
    private Node joinGroupNode;

    private void join(RGroup r2, Node n2) {
        if (model.validateJoin(joinGroup, r2)) {
            Point3D dCenter = r2.getCenter().subtract(joinGroup.getCenter());
            RGroup r1 = this.joinGroup;
            Node n1 = this.joinGroupNode;
            rGroupGroup.getChildren().remove(n1);
            RGroupView2DController rgv2dc = rGroupControllerMap.remove(n1);
            if (model.shift(r1, dCenter.getX(), dCenter.getY(), dCenter.getZ())) {
                Set<RGroup> selected = model.getSelectedCopy();
                Runnable undo = model.join(r1, r2, true);
                properties.getUndoManager().log(() -> {
                    selected.forEach(r -> model.shift(r, dCenter.getX(), dCenter.getY(), dCenter.getZ()));
                    model.join(r2, r1, false);
                    rGroupGroup.getChildren().remove(n1);
                    rGroupControllerMap.remove(n1);
                    rGroupControllerMap.get(n2).setRGroup(r1);
                }, () -> {
                    rGroupControllerMap.get(n2).setRGroup(r2);
                    rGroupControllerMap.put(n1, rgv2dc);
                    rGroupGroup.getChildren().add(n1);
                    undo.run();
                    selected.forEach(r -> model.shift(r, -dCenter.getX(), -dCenter.getY(), -dCenter.getZ()));
                });
            } else {
                Runnable undo = model.join(r2, r1, true);
                properties.getUndoManager().log(() -> {
                    model.shift(r1, dCenter.getX(), dCenter.getY(), dCenter.getZ());
                    model.join(r2, r1, false);
                    rGroupGroup.getChildren().remove(n1);
                    rGroupControllerMap.remove(n1);
                    rGroupControllerMap.get(n2).setRGroup(r1);
                }, () -> {
                    rGroupControllerMap.get(n2).setRGroup(r2);
                    rGroupControllerMap.put(n1, rgv2dc);
                    rGroupGroup.getChildren().add(n1);
                    undo.run();
                    model.shift(r1, -dCenter.getX(), -dCenter.getY(), -dCenter.getZ());
                });
            }
            joinGroup = null;
            joinGroupNode = null;
            updateMinimap();
        }
    }

    /*
    ============================================================================
    
    Minimap implementation
    
    ============================================================================
     */
    private void updateMinimap() {
        ensureSize();
        rightMenu.updateMinimapImage(getImage2DRaw());
    }

    /*
    ============================================================================
    
    Auto-resizing implementation
    
    ============================================================================
     */
    private static final double edgeBoundOffset = 100;

    private void ensureSize() {
        Bounds[] intersectBounds = getEdgeBounds();
        boolean growLeft = rGroupGroup.getChildren().stream().map(Node::getBoundsInParent)
                .anyMatch(b -> intersectBounds[0].intersects(b) || intersectBounds[1].intersects(b)
                || b.getMinX() < intersectBounds[4].getMinX() || b.getMinY() < intersectBounds[4].getMinY());
        if (growLeft) {
            growLeft();
        } else {
            //Grow right check
            rGroupGroup.getChildren().stream().map(Node::getBoundsInParent)
                    .filter(b -> intersectBounds[2].intersects(b) || intersectBounds[3].intersects(b))
                    .findAny().ifPresent(b -> growRight());
        }
    }

    private Bounds[] getEdgeBounds() {
        Bounds drawingPaneBounds = backing2d.getBoundsInParent();
        Bounds left = new BoundingBox(drawingPaneBounds.getMinX(), drawingPaneBounds.getMinY(),
                edgeBoundOffset, drawingPaneBounds.getHeight()),
                top = new BoundingBox(drawingPaneBounds.getMinX(), drawingPaneBounds.getMinY(),
                        drawingPaneBounds.getWidth(), edgeBoundOffset),
                right = new BoundingBox(drawingPaneBounds.getMaxX() - edgeBoundOffset,
                        drawingPaneBounds.getMinY(), edgeBoundOffset, drawingPaneBounds.getHeight()),
                bottom = new BoundingBox(drawingPaneBounds.getMinX(), drawingPaneBounds.getMaxY() - edgeBoundOffset,
                        drawingPaneBounds.getWidth(), edgeBoundOffset);
        return new Bounds[]{left, top, right, bottom, drawingPaneBounds};
    }

    private void growLeft() {
        grow(-520, -350);
    }

    private void growRight() {
        grow(520, 350);
    }

    private void grow(double x, double y) {
        backing2d.setWidth(backing2d.getWidth() + Math.abs(x));
        backing2d.setHeight(backing2d.getHeight() + Math.abs(y));
        if (x < 0 && y < 0) {
            model.correctionShift(-x, -y, 0);
        }
        ensureSize();
    }

    /*
    ============================================================================
    
    Reporter implementations
    
    ============================================================================
     */
    @Override
    public void reportShift(RGroup ofInterest, double x, double y, double z) {
        if (!model.shift(ofInterest, x, y, z)) {
            deselectAll();
        }
    }

    @Override
    public void reportTotalShift(RGroup ofInterest, double x, double y, double z) {
        Set<RGroup> toShift = model.getSelectedCopy();
        if (toShift.isEmpty()) {
            properties.getUndoManager().log(() -> model.shift(ofInterest, x, y, z), () -> model.shift(ofInterest, -x, -y, -z));
        } else {
            properties.getUndoManager().log(() -> toShift.forEach(r -> model.shift(r, x, y, z)), () -> toShift.forEach(r -> model.shift(r, -x, -y, -z)));
        }
        updateMinimap();
    }

    @Override
    public void reportClick(Node n, RGroup ofInterest) {
        if (hangingRGroup != null && hangingRGroupNode != null) {
            if (rGroupControllerMap.containsKey(n)) {
                RGroupView2DController av2dc = rGroupControllerMap.get(n);
                if (hangingBond != null && hangingBondNode != null) {
                    completeBondOnTarget(ofInterest);
                } else {
                    switchRGroup(av2dc);
                }
            }
        } else {
            if (joinGroup != null && joinGroupNode != null) {
                join(ofInterest, n);
            } else {
                makeBond(ofInterest);
            }
        }
    }

    @Override
    public void reportUndoableEvent(Runnable redo, Runnable undo) {
        properties.getUndoManager().log(redo, undo);
    }

    @Override
    public void reportHardAngle(double angle) {
        angleProperty.set(angle);
        hardcodedAngle = true;
        hangingRGroup.setCenter(angleWidget.getEndX(), angleWidget.getEndY(), 0);
    }

    @Override
    public void reportMakeAtom(Element e) {
        makeAtom(e);
    }

    @Override
    public void reportJoin(Node n, RGroup ofInterest) {
        if (joinGroup == null) {
            joinGroup = ofInterest;
            joinGroupNode = n;
        } else {
            join(ofInterest, n);
        }
    }

    @Override
    public void reportRotate(double radians) {
        Map<RGroup, Point3D> old = model.getSelectedCopy().stream().collect(Collectors.toMap(Function.identity(), RGroup::getCenter));
        if (!old.isEmpty()) {
            model.rotateSelected(radians);
            Map<RGroup, Point3D> rotated = model.getSelectedCopy().stream().collect(Collectors.toMap(Function.identity(), RGroup::getCenter));
            properties.getUndoManager().log(() -> rotated.forEach((r, p) -> r.setCenter(p)),
                    () -> old.forEach((r, p) -> r.setCenter(p)));
        }
    }

    /*
    ============================================================================
    
    Async Load API
    
    ============================================================================
     */
    private final ExecutorService asyncPool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    private final AtomicInteger loadCount = new AtomicInteger();

    private Future<?> async(Runnable task) {
        return asyncPool.submit(() -> {
            Platform.runLater(this::startLoadTask);
            task.run();
            Platform.runLater(this::finishLoadTask);
        });
    }

    private void startLoadTask() {
        if (loadCount.addAndGet(1) == 1) {
            root.pseudoClassStateChanged(ViewUtils.LOADING, true);
        }
    }

    private void finishLoadTask() {
        if (loadCount.addAndGet(-1) == 0) {
            root.pseudoClassStateChanged(ViewUtils.LOADING, false);
        }
    }

    /*
    ============================================================================
    
    File Saving/Loading
    
    ============================================================================
     */
    private Path saveFile;

    @FXML
    private void save() {
        if (saveFile == null) {
            saveAs();
        } else {
            async(() -> {
                while (true) {
                    try {
                        synchronized (pasteNotify) {
                            if (!pasteFinished) {
                                pasteNotify.wait();
                                continue;
                            }
                        }
                        synchronized (loadNotify) {
                            if (!loadFinished) {
                                loadNotify.wait();
                                continue;
                            }
                        }
                    } catch (InterruptedException tryingToFinish) {
                    }
                    break;
                }
                Platform.runLater(() -> Marshalling.save(model, saveFile));
            });
        }
    }

    @FXML
    private void saveAs() {
        saveFile = FileUI.saveDialog(root.getScene().getWindow());
        if (saveFile != null) {
            save();
        }
    }

    private final Object loadNotify = new Object();
    private boolean loadFinished = true;

    @FXML
    private void open() {
        saveFile = FileUI.openDialog(root.getScene().getWindow());
        if (saveFile != null) {
            loadFinished = false;
            async(() -> {
                Model m = Marshalling.load(saveFile);
                Stream.concat(m.getBondables().stream(), m.getBonds().stream()).forEach(model::validateId);
                m.getBondables().forEach(model::add);
                m.getBonds().forEach(model::add);
                Platform.runLater(() -> {
                    loadNodes(m.getBondables(), m.getBonds());
                    synchronized (loadNotify) {
                        loadFinished = true;
                        loadNotify.notifyAll();
                    }
                });
            });
        }
    }

    /*
    ============================================================================
    
    Copy/Pasting
    
    ============================================================================
     */
    private static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
    private static final DataFormat MOLECULE = new DataFormat("molecule");
    private final Object pasteNotify = new Object();
    private boolean pasteFinished = true;

    @FXML
    private void cut() {
        copy();
        deleteSelected();
    }

    @FXML
    private void copy() {
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(autoCropImage(getImage2DRaw()));
        Map<String, List<XMLData>> serialized = model.serializeSelected();
        cc.put(MOLECULE, serialized);
        CLIPBOARD.setContent(cc);
    }

    @FXML
    private void paste() {
        Map<String, List<XMLData>> data = (Map) CLIPBOARD.getContent(MOLECULE);
        if (data != null) {
            deselectAll();
            pasteFinished = false;
            async(() -> {
                Map<String, RGroup> bondables = Stream.concat(data.get("Atom").stream().map(Atom::deserialize),
                        data.get("RGroup").stream().map(RGroup::deserialize))
                        .collect(Collectors.toMap(Identifiable::getId, Function.identity()));
                Set<Bond> bondSet = data.get("Bond").stream().map(d
                        -> BondImpl.deserialize(d, bondables::get)).collect(Collectors.toSet());
                Stream.concat(bondables.values().stream(), bondSet.stream())
                        .parallel().forEach(model::validateId);
                Platform.runLater(() -> {
                    loadNodes(bondables.values(), bondSet);
                    synchronized (pasteNotify) {
                        pasteFinished = true;
                        pasteNotify.notifyAll();
                    }
                });
            });
        }
    }

    /*
    ============================================================================
    
    Image Capturing
    
    ============================================================================
     */
    private static final int paddingPixelCount = 15;

    private WritableImage getImage2DRaw() {
        Set<RGroup> selected = model.getSelectedCopy();
        model.deselectAll();
        WritableImage image = drawingPane2d.snapshot(null, null);
        model.selectAll(selected);
        return image;
    }

    private static Image autoCropImage(Image image) {
        PixelReader reader = image.getPixelReader();
        PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbInstance();
        int[] columnContains = IntStream.range(0, (int) image.getWidth()).parallel()
                .filter(x -> IntStream.range(0, (int) image.getHeight()).anyMatch(y
                -> (reader.getArgb(x, y) & 0xffffff) != 0xffffff)).sorted().toArray();
        int[] rowContains = IntStream.range(0, (int) image.getHeight()).parallel()
                .filter(y -> IntStream.range(0, (int) image.getWidth()).anyMatch(x
                -> (reader.getArgb(x, y) & 0xffffff) != 0xffffff)).sorted().toArray();
        if (columnContains.length == 0) {
            return null;
        }
        int minX = Math.max(0, columnContains[0] - paddingPixelCount),
                maxX = Math.min((int) image.getWidth(), columnContains[columnContains.length - 1] + paddingPixelCount),
                minY = Math.max(0, rowContains[0] - paddingPixelCount),
                maxY = Math.min((int) image.getHeight(), rowContains[rowContains.length - 1] + paddingPixelCount);
        return new WritableImage(reader, minX, minY, maxX - minX, maxY - minY);
    }

    /*
    ============================================================================
    
    Node Loading
    
    ============================================================================
     */
    private void loadNodes(Collection<? extends RGroup> rGroups, Collection<? extends Bond> bonds) {
        Map<RGroup, Pair<Node, RGroupView2DController>> grouped
                = rGroups.stream().peek(model::add).collect(Collectors.toMap(Function.identity(), this::placeAndMap));
        rGroups.stream().map(RGroup::getCenter).mapToDouble(Point3D::getX).min().ifPresent(x
                -> rGroups.stream().map(RGroup::getCenter).mapToDouble(Point3D::getY).min().ifPresent(y
                        -> rGroups.forEach(r -> r.shiftCenter(-x, -y, 0))));
        Map<Bond, Node> bondMappings = bonds.stream().peek(model::add).collect(Collectors.toMap(Function.identity(), this::placeAndMap));
        properties.getUndoManager().log(() -> {
            grouped.forEach((r, p) -> {
                rGroupGroup.getChildren().add(p.getKey());
                rGroupControllerMap.put(p.getKey(), p.getValue());
            });
            bondMappings.entrySet().stream().peek(e -> model.add(e.getKey()))
                    .map(Map.Entry::getValue).forEach(bondGroup.getChildren()::add);
        }, () -> {
            grouped.entrySet().stream().peek(e -> model.delete(e.getKey())).map(Map.Entry::getValue)
                    .map(Pair::getKey).peek(rGroupGroup.getChildren()::remove)
                    .forEach(rGroupControllerMap::remove);
            bondMappings.entrySet().stream().peek(e -> model.delete(e.getKey()))
                    .map(Map.Entry::getValue).forEach(bondGroup.getChildren()::remove);
        });
        if (grouped.entrySet().stream().map(Map.Entry::getKey).peek(model::select).count() > 0) {
            cancellables.add(Cancellable.SELECTION);
            RGroup shift = grouped.keySet().stream().findAny().orElse(null);
            Point3D shiftBy = shift.getCenter().subtract(dmx, dmy, 0).multiply(-1);
            model.shift(shift, shiftBy.getX(), shiftBy.getY());
        }
        updateMinimap();
    }

    private Pair<Node, RGroupView2DController> placeAndMap(RGroup rgroup) {
        FXMLLoader loader = Resources.getLoader("RGroupView2D.fxml");
        loader.setControllerFactory(clz -> new RGroupView2DController(this));
        Node n = Resources.load(loader);
        RGroupView2DController controller = loader.getController();
        controller.setRGroup(rgroup);
        rGroupControllerMap.put(n, controller);
        rGroupGroup.getChildren().add(n);
        controller.setOnDelete(() -> delete(rgroup, n));
        return new Pair<>(n, controller);
    }

    private Node placeAndMap(Bond b) {
        FXMLLoader loader = Resources.getLoader("BondView2D.fxml");
        loader.setControllerFactory(clz -> new BondView2DController(b, this));
        Node n = Resources.load(loader);
        bondGroup.getChildren().add(n);
        BondView2DController controller = loader.getController();
        controller.setOnDelete(() -> delete(b, n));
        return n;
    }
}
