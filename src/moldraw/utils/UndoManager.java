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
public interface UndoManager {

    void undo();

    void redo();
    
    boolean canUndo();
    
    boolean canRedo();

    void log(Runnable forward, Runnable backward);

    default void doAndLog(Runnable forward, Runnable backward) {
        forward.run();
        log(forward, backward);
    }
}
