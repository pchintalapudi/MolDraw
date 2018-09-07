/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.controller;

import javafx.scene.Node;
import moldraw.model.targets.Element;
import moldraw.model.targets.RGroup;

/**
 *
 * @author prem
 */
public interface Reporter {

    void reportShift(RGroup ofInterest, double x, double y, double z);

    default void reportShift(RGroup ofInterest, double x, double y) {
        reportShift(ofInterest, x, y, 0);
    }
    
    void reportTotalShift(RGroup ofInterest, double x, double y, double z);
    
    void reportClick(Node root, RGroup ofInterest);
    
    void reportUndoableEvent(Runnable redo, Runnable undo);

    void reportHardAngle(double angle);

    void reportMakeAtom(Element e);

    void reportJoin(Node n, RGroup ofInterest);

    void reportRotate(double radians);
}
