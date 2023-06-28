package RTree;

import utilities.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MBR {
    final int DIMENSION;

    double[][] bounds;
    double area;

    public MBR(int dimension) {
        this.DIMENSION = dimension;
        bounds = new double[DIMENSION][2];
        area = 0;
    }

    public MBR(double[][] bounds) {
        this(bounds.length);
        this.bounds = bounds;
        this.area = areaCalc();
    }

    public MBR(Point p) {
        this(p.DIMENSION);
        for (int i = 0; i < DIMENSION; i++) {
            bounds[i][0] = p.getX()[i];
            bounds[i][1] = p.getX()[i];
        }
        this.area = areaCalc();
    }

    public boolean overlaps(MBR mbr) {
        for (int i = 0; i < DIMENSION; i++) {
            if (!(this.bounds[i][0] <= mbr.bounds[i][1] && mbr.bounds[i][0] <= this.bounds[i][1])) {
                return false;
            }
        }
        return true;
    }

    public double overlapArea(MBR mbr) {
        double overlap = 1;

        for (int i = 0; i < DIMENSION; i++) {
            overlap *= Math.max(0, Math.min(this.bounds[i][1], mbr.bounds[i][1]) - Math.max(this.bounds[i][0], mbr.bounds[i][0]));
        }
        return overlap;
    }

    public double overlapAreaCost(MBR mbr){
        return mbr.area - overlapArea(mbr);
    }

    public MBR enlargeMBR(MBR mbr) {

        double[][] enlargedBounds = new double[DIMENSION][2];
        for (int i = 0; i < DIMENSION; i++) {
            enlargedBounds[i][0] = Math.min(this.bounds[i][0], mbr.bounds[i][0]);

            enlargedBounds[i][1] = Math.max(this.bounds[i][1], mbr.bounds[i][1]);
        }
        return new MBR(enlargedBounds);
    }

    public double areaEnlargementCost(MBR mbr) {
        return enlargeMBR(mbr).area - this.area;
    }

    public double marginValue() {
        double sum = 0;
        for (int i = 0; i < DIMENSION; i++) {
            sum += this.bounds[i][1] - this.bounds[i][0];
        }
        return sum;
    }
    public double areaCalc() {
        double area = 1;
        for (int i = 0; i < DIMENSION; i++) {
            area *= bounds[i][1] - bounds[i][0];
        }
        return area;
    }

    public Point center() {
        Point center = new Point(DIMENSION);
        for (int i = 0; i < DIMENSION; i++) {
            center.getX()[i] = (this.bounds[i][0] + this.bounds[i][1]) / 2;
        }
        return center;
    }
    public double distanceToCenter(MBR mbr) {
        return this.center().distance(mbr.center());
    }

    public static MBR fitMBR(ArrayList<Entry<?>> entries){
        MBR mbr = new MBR(entries.get(0).mbr.getBounds());
        for (Entry<?> entry : entries) {
            mbr = mbr.enlargeMBR(entry.mbr);
        }
        return mbr;
    }

    public double minDist(Point p) {
        assert p.DIMENSION == this.DIMENSION;
        double d = 0;
        for (int i = 0; i < DIMENSION; i++) {
            double r = p.getX()[i];
            if (p.getX()[i] < bounds[i][0])
                r = bounds[i][0];
            if (p.getX()[i] > bounds[i][1])
                r = bounds[i][1];

            d += Math.pow(Math.abs(p.getX()[i] - r), 2);
        }
        return d;
    }
    public double minDist(){
        return minDist(new Point(DIMENSION, new double[] {0, 0}));
    }

    public Point toPoint() {
        double[] p = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            p[i] = bounds[i][0];
        }
        return new Point(DIMENSION, p);
    }
    public double[][] getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(bounds);
    }

    @Override
    public boolean equals(Object o) {

        return Arrays.deepEquals(bounds, ((MBR) o).bounds);
    }



}
