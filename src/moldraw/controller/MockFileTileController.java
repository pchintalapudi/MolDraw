/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import moldraw.utils.ViewUtils;

/**
 * FXML Controller class
 *
 * @author prem
 */
public class MockFileTileController<T> {

    @FXML
    private Node root;
    @FXML
    private ImageView fileImage;
    @FXML
    private Label fileName;

    private Image fi;
    private final MockFileChooserController<T> mfcc;
    private final String fn;

    public MockFileTileController(String fileName, Image fileImage, MockFileChooserController<T> mfcc) {
        this.mfcc = mfcc;
        this.fi = fileImage;
        this.fn = fileName;
    }

    @FXML
    private void initialize() {
        if (fi != null) {
            fileImage.setImage(fi);
            fi = null;
        }
        fileName.setText(fn);
    }

    @FXML
    private void clicked(MouseEvent m) {
        if (m.getClickCount() == 1) {
            mfcc.select(this);
        } else if (m.getClickCount() > 1) {
            mfcc.open(this);
        }
    }

    public void select() {
        root.pseudoClassStateChanged(ViewUtils.SELECTED, true);
    }

    public void deselect() {
        root.pseudoClassStateChanged(ViewUtils.SELECTED, false);
    }
}
