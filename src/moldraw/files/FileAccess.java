/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.image.Image;
import javafx.util.Pair;

/**
 *
 * @author prem
 */
public class FileAccess {

    public static final String imageTag = ".jpg", moleculeTag = ".mldrw";
    public static final File MOLECULE_DIRECTORY = new File("molecules"), IMAGE_DIRECTORY = new File("images");

    private static final Map<String, Pair<Path, Path>> cachedDirectoryContents = new ConcurrentHashMap<>();

    static {
        ensureDirectory(MOLECULE_DIRECTORY);
        ensureDirectory(IMAGE_DIRECTORY);
        try {
            Map<String, Path> moleculePaths = Files.walk(MOLECULE_DIRECTORY.toPath())
                    .collect(Collectors.toMap(p -> p.toFile().getName().replace(moleculeTag, ""), Function.identity()));
            Map<String, Path> imagePaths = Files.walk(IMAGE_DIRECTORY.toPath())
                    .collect(Collectors.toMap(p -> p.toFile().getName().replace(imageTag, ""), Function.identity()));
            Map<String, Pair<Path, Path>> merged = moleculePaths.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> new Pair<>(e.getValue(), imagePaths.remove(e.getKey()))));
            imagePaths.forEach((s, p) -> merged.put(s, new Pair<>(null, p)));
            cachedDirectoryContents.putAll(merged);
        } catch (IOException ex) {
            Logger.getLogger(FileAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void ensureDirectory(File f) {
        if (!f.isDirectory()) {
            f.mkdir();
        }
    }

    public static void save(String file, Stream<String> lines, Image fileImage) {
        try {
            Pair<Path, Path> paths = cachedDirectoryContents.get(file);
            Path moleculePath, imagePath;
            if (paths != null) {
                moleculePath = paths.getKey();
                if (moleculePath == null) {
                    File f = new File(MOLECULE_DIRECTORY, file);
                    f.createNewFile();
                    moleculePath = f.toPath();
                }
                imagePath = paths.getValue();
                if (imagePath == null) {
                    File f = new File(IMAGE_DIRECTORY, file);
                    f.createNewFile();
                    imagePath = f.toPath();
                }
            } else {
                File f = new File(MOLECULE_DIRECTORY, file);
                f.createNewFile();
                moleculePath = f.toPath();
                f = new File(IMAGE_DIRECTORY, file);
                f.createNewFile();
                imagePath = f.toPath();
            }
            cachedDirectoryContents.put(file, new Pair<>(moleculePath, imagePath));
        } catch (IOException ex) {
            Logger.getLogger(FileAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Map<String, Pair<Path, Path>> getFiles() {
        return new HashMap<>(cachedDirectoryContents);
    }
}
