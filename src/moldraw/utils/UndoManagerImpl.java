/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author prem
 */
class UndoManagerImpl implements UndoManager {

    private final Deque<Action> done = new ArrayDeque<>(), undone = new ArrayDeque<>();

    @Override
    public void undo() {
        if (done.size() > 0) {
            Action a = done.pop();
            a.backward.run();
            undone.push(a);
        }
    }

    @Override
    public void redo() {
        if (undone.size() > 0) {
            Action a = undone.pop();
            a.forward.run();
            done.push(a);
        }
    }

    @Override
    public boolean canUndo() {
        return done.size() > 0;
    }

    @Override
    public boolean canRedo() {
        return undone.size() > 0;
    }

    @Override
    public void log(Runnable forward, Runnable backward) {
        undone.clear();
        done.push(new Action(forward, backward));
    }

    private static class Action {

        private final Runnable forward, backward;

        public Action(Runnable forward, Runnable backward) {
            this.forward = forward;
            this.backward = backward;
        }
    }
}
