/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.util.Duration;

/**
 *
 * @author prem
 */
public final class ViewUtils {

    private ViewUtils() {
    }

    private static void finish(Timeline tl, Runnable exec) {
        tl.stop();
        exec.run();
    }

    public static void delayFX(long millis, Runnable action) {
        Timeline tl = new Timeline();
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(millis), e -> finish(tl, action)));
        tl.play();
    }

    public static PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    public static PseudoClass HIGHLIGHTED = PseudoClass.getPseudoClass("highlighted");
    public static final PseudoClass LOADING = PseudoClass.getPseudoClass("loading");
}
