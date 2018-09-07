/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import java.math.BigDecimal;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

/**
 *
 * @author prem
 */
public final class Utils {

    private Utils() {
    }

    public static double square(double a) {
        return a * a;
    }

    public static boolean fuzzyEquals(double d1, double d2, double delta) {
        return Math.abs(d1 - d2) < delta;
    }

    public static String title(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static double truncated(double d) {
        return Utils.truncated(d, ROUNDING);
    }

    private static final int ROUNDING = 5;

    public static double truncated(double d, int decimalPlaces) {
        return new BigDecimal(new BigDecimal(String.valueOf(d)).movePointRight(
                decimalPlaces).toBigInteger()).movePointLeft(decimalPlaces).doubleValue();
    }

    public static double rounded(double d) {
        return rounded(d, ROUNDING);
    }

    public static double rounded(double d, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(d * scale) / scale;
    }

    private static final double lowerBound = Math.pow(10, -ROUNDING);
    private static final double upperBound = 1 - lowerBound;

    public static boolean isInteger(double d) {
        double dif = d - (int) d;
        return dif < lowerBound || dif > upperBound;
    }

    public static boolean isInteger(double d, int decimalPlaces) {
        double dif = Math.abs(d - (int) d);
        double lower = Math.pow(10, -decimalPlaces);
        return dif < lower || dif > 1 - lower;
    }

    public static double integerize(double d, int decimalPlaces) {
        return isInteger(d, decimalPlaces) ? Math.round(d) : d;
    }

    public static double integerize(double d) {
        return integerize(d, ROUNDING);
    }

    public static Point3D toPoint3D(Point2D point) {
        return new Point3D(point.getX(), point.getY(), 0);
    }

    public static Point2D toPoint2D(Point3D point) {
        return new Point2D(point.getX(), point.getY());
    }

    public static <T> boolean contains(T[] arr, T item) {
        for (T t : arr) {
            if (item == null ? t == null : item.equals(t)) {
                return true;
            }
        }
        return false;
    }

    //If you want clockwise rotation merely negate the radians
    public static Point2D rotate(Point2D point, Point2D center, double radians) {
        return rotate(point.getX(), point.getY(), center.getX(), center.getY(), radians);
    }

    public static Point2D rotate(double px, double py, double cx, double cy, double radians) {
        double sx = px - cx, sy = py - cy;
        double cos = Math.cos(radians), sin = Math.sin(radians);
        return new Point2D(sx * cos - sy * sin + cx, sx * sin + sy * cos + cy);
    }
}
