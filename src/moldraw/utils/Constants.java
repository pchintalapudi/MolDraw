/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author prem
 */
public class Constants {

    //Yes I actually calculated these out (I may have written a 5 second program
    //to do it for me though :))
    private static final List<Double> niceAngles = Collections.unmodifiableList(Arrays.asList(0.0, 90.0, 270.0,
            180.0, 45.0, 315.0, 225.0, 135.0, 120.0, 240.0, 330.0, 150.0, 210.0,
            30.0, 60.0, 300.0, 72.0, 288.0, 18.0, 198.0, 144.0, 216.0, 306.0,
            126.0, 234.0, 54.0, 162.0, 342.0, 40.0, 320.0, 50.0, 230.0, 80.0,
            280.0, 10.0, 190.0, 160.0, 200.0, 290.0, 110.0, 250.0, 70.0, 170.0,
            350.0, 130.0, 310.0, 36.0, 324.0, 108.0, 252.0));

    public static List<Double> getNiceAngles() {
        return niceAngles;
    }
}
