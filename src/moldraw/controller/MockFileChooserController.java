/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;

/**
 * FXML Controller class
 *
 * @author prem
 * @param <T>
 */
public class MockFileChooserController<T> {

    private final SelectionModel<MockFileTileController> selectionModel = new SingleSelectionModel<MockFileTileController>() {
        @Override
        protected MockFileTileController getModelItem(int index) {
            return items.get(index);
        }

        @Override
        protected int getItemCount() {
            return items.size();
        }
    };

    {
        selectionModel.selectedItemProperty().addListener((o, b, s) -> {
            if (b != null) {
                b.deselect();
            }
            if (s != null) {
                s.select();
            }
        });
    }

    private final ObservableList<MockFileTileController<T>> items = FXCollections.observableArrayList();
    private final List<T> data;

    private final Consumer<? super T> action;
    private boolean success;

    public MockFileChooserController(Collection<? extends T> data, Function<? super T, ? extends String> nameMapper, Consumer<? super T> action) {
        this.action = action;
        this.data = new ArrayList<>(data);
        this.data.forEach(t -> {
            items.add(new MockFileTileController<>(nameMapper.apply(t), null, this));
        });
    }

    public MockFileChooserController(Collection<? extends T> data, Function<? super T, ? extends String> nameMapper,
            Function<? super T, ? extends Image> imageMapper, Consumer<? super T> action) {
        this.action = action;
        this.data = new ArrayList<>(data);
        this.data.forEach(t -> {
            items.add(new MockFileTileController<>(nameMapper.apply(t), imageMapper.apply(t), this));
        });
    }

    @FXML
    private TilePane filePane;
    
    @FXML
    private Label titleLabel;

    @FXML
    private void initialize() {
        filePane.sceneProperty().addListener((o, b, s) -> s.getWindow().setOnHidden(w -> getBack()));
    }
    
    public void setDialogHeader(String headerText) {
        titleLabel.setText(headerText);
    }

    public void open(MockFileTileController<T> mftc) {
        action.accept(data.get(items.indexOf(mftc)));
        success = true;
        filePane.getScene().getWindow().hide();
    }

    public void select(MockFileTileController<T> mftc) {
        selectionModel.select(mftc);
    }

    private void getBack() {
        if (!success) {
            action.accept(null);
        }
    }
}
