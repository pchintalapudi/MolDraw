/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import moldraw.model.Model;
import moldraw.xml.XMLDataReader;
import moldraw.xml.XMLDataWriter;

/**
 *
 * @author prem
 */
public class Marshalling {

    public static void save(Model model, Path file) {
        try (XMLDataWriter writer = new XMLDataWriter(new FileOutputStream(file.toFile()))) {
            writer.write(model.serialize());
        } catch (FileNotFoundException | XMLStreamException ex) {
            Logger.getLogger(Marshalling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Model load(Path file) {
        try (XMLDataReader reader = new XMLDataReader(new FileInputStream(file.toFile()))) {
            return Model.deserialize(reader.read());
        } catch (FileNotFoundException | XMLStreamException ex) {
            Logger.getLogger(Marshalling.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
