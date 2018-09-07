/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author prem
 */
public class Debug {
    
    public static void debugProperty(ObservableValue ov, String identifier) {
        ov.addListener((o, b, s) -> System.out.println(identifier + ": " + s));
    }
    
    public static void debugList(ObservableList list, String identifier) {
        list.addListener((ListChangeListener.Change c) -> {
            System.out.println(identifier + ": " + list);
        });
    }
}
