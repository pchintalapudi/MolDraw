/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.resources;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author prem
 */
public class Resources {

    public static FXMLLoader getLoader(String name) {
        return new FXMLLoader(Resources.class.getClassLoader().getResource("fxml/" + name));
    }

    public static <T> T load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException ex) {
            return null;
        }
    }
    
    public static void loadFXMLAsRoot(String name, Object root) {
        FXMLLoader loader = getLoader(name);
        loader.setRoot(root);
        loader.setController(root);
        load(loader);
    }

    public static String loadCSS(String name) {
        return Resources.class.getClassLoader().getResource("css/" + name).toExternalForm();
    }
}
