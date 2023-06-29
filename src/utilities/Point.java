package utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Point implements Serializable {
    public final int DIMENSION;

    double[] x;

    public Point(int DIMENSION) {
        this.DIMENSION = DIMENSION;
        this.x = new double[DIMENSION];
    }

    public Point(int DIMENSION, double[] x) {
        this.DIMENSION = DIMENSION;
        this.x = x;
    }

    public double[] getX() {
        return x;
    }

    public double distance(Point p) {
        double d = 0;
        for (int i = 0; i < DIMENSION; i++) {
            d += Math.abs(this.x[i] - p.x[i]);
        }
        return d;
    }

    public boolean dominates(Point p) {
        for (int i = 0; i < DIMENSION; i++) {
            if (p.x[i] < x[i])
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        Point point = (Point) o;
        return Arrays.equals(x, point.x);
    }

    @Override
    public String toString() {
        return Arrays.toString(x);
    }
}
