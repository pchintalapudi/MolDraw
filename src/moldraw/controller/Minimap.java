/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.util.function.DoubleConsumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import moldraw.resources.Resources;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class Minimap extends StackPane {

    @FXML
    private ImageView image;
    @FXML
    private Rectangle view;
    private final DoubleBinding minXBinding, minYBinding, maxXBinding, maxYBinding;

    public Minimap() {
        Resources.loadFXMLAsRoot("Minimap.fxml", this);
        minXBinding = widthProperty().subtract(image.fitWidthProperty()).divide(2);
        minYBinding = heightProperty().subtract(image.fitHeightProperty()).divide(2);
        maxXBinding = image.fitWidthProperty().subtract(view.widthProperty()).add(minXBinding);
        maxYBinding = image.fitHeightProperty().subtract(view.heightProperty()).add(minYBinding);
    }

    public void bind(ScrollPane mapped) {
        view.setX(5);
        view.setY(0);
        view.widthProperty().bind(Bindings.createDoubleBinding(()
                -> Math.min(image.getFitWidth(), image.getFitWidth()
                        * mapped.getViewportBounds().getWidth()
                        / mapped.getContent().getLayoutBounds().getWidth()),
                image.fitWidthProperty(), mapped.viewportBoundsProperty(),
                mapped.getContent().layoutBoundsProperty()));
        view.heightProperty().bind(Bindings.createDoubleBinding(()
                -> Math.min(image.getFitHeight(), image.getFitHeight()
                        * mapped.getViewportBounds().getHeight()
                        / mapped.getContent().getLayoutBounds().getHeight()),
                image.fitHeightProperty(), mapped.viewportBoundsProperty(),
                mapped.getContent().layoutBoundsProperty()));
        mapped.hvalueProperty().bind(view.xProperty().divide(maxXBinding.subtract(minXBinding)));
        mapped.vvalueProperty().bind(view.yProperty().divide(maxYBinding.subtract(minYBinding)));
    }

    private double lx, ly;

    @FXML
    private void press(MouseEvent m) {
        lx = m.getSceneX();
        ly = m.getSceneY();
    }

    @FXML
    private void drag(MouseEvent m) {
        shiftView(m.getSceneX() - lx, m.getSceneY() - ly);
        lx = m.getSceneX();
        ly = m.getSceneY();
    }

    private void shiftView(double x, double y) {
        setView(view.getX() + x, view.getY() + y);
    }

    private void setView(double x, double y) {
        setViewCoordinate(x, minXBinding.get(), maxXBinding.get(), view::setX);
        setViewCoordinate(y, minYBinding.get(), maxYBinding.get(), view::setY);
    }

    private void setViewCoordinate(double c, double min, double max, DoubleConsumer func) {
        if (c < min) {
            func.accept(min);
        } else if (c > max) {
            func.accept(max);
        } else {
            func.accept(c);
        }
    }

    public void updateImage(Image i) {
        image.setImage(i);
    }
}
