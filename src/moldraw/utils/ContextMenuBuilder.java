/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author prem
 */
public class ContextMenuBuilder {

    private ContextMenuBuilder() {
    }

    private final List<MenuItem> menuItems = new ArrayList<>();

    public ContextMenuBuilder addItem(String name, Runnable action) {
        MenuItem item = new MenuItem(name);
        if (action != null) {
            item.setOnAction(e -> action.run());
        }
        menuItems.add(item);
        return this;
    }

    public ContextMenu build() {
        ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(menuItems);
        return cm;
    }

    public static ContextMenuBuilder create() {
        return new ContextMenuBuilder();
    }
}
