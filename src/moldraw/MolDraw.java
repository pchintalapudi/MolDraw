/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import moldraw.resources.Resources;

/**
 *
 * @author prem
 */
public class MolDraw extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setScene(new Scene(Resources.load(Resources.getLoader("MainView.fxml")), 1600, 900));
        primaryStage.getScene().getStylesheets().addAll(Resources.loadCSS("view.css"), Resources.loadCSS("default-colors.css"));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
