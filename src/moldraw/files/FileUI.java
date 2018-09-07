/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.files;

import java.io.File;
import java.nio.file.Path;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author prem
 */
public class FileUI {

    private static final FileChooser fc = new FileChooser();

    static {
        fc.setInitialDirectory(FileAccess.MOLECULE_DIRECTORY);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Molecule (*.mldrw)", "*.mldrw"));
    }

    public static Path saveDialog(Window stage) {
        File f = fc.showSaveDialog(stage);
        return f == null ? null : f.toPath();
    }

    public static Path openDialog(Window stage) {
        File f = fc.showOpenDialog(stage);
        return f == null ? null : f.toPath();
    }
}
