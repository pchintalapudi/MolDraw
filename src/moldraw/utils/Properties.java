/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

/**
 *
 * @author prem
 */
public final class Properties {

    private final UndoManager undoManager = new UndoManagerImpl();

    public UndoManager getUndoManager() {
        return undoManager;
    }

    private Properties() {
    }

    public static Properties create() {
        return new Properties();
    }
}
