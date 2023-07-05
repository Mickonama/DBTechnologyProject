package RTree;

import utilities.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This class represents a Minimum Bounding Rectangle in the n-dimensional space
 */
public class MBR {
    final int DIMENSION; //The dimension of the rectangle

    double[][] bounds; //The bounds of the rectangle
    double area; //The area of the rectangle

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

    /**
     * This method checks if the current MBR overlaps with another one
     * @param mbr is the MBR for which the overlap is checked
     * @return True if the two MBR's overlap, False otherwise
     */
    public boolean overlaps(MBR mbr) {
        for (int i = 0; i < DIMENSION; i++) {
            if (!(this.bounds[i][0] <= mbr.bounds[i][1] && mbr.bounds[i][0] <= this.bounds[i][1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method calculates the overlap area between this and another MBR
     * @param mbr is the MBR with which the overlap are of the current MBR is calculated
     * @return the value of the overlap area
     */
    public double overlapArea(MBR mbr) {
        double overlap = 1;

        for (int i = 0; i < DIMENSION; i++) {
            overlap *= Math.max(0, Math.min(this.bounds[i][1], mbr.bounds[i][1]) - Math.max(this.bounds[i][0], mbr.bounds[i][0]));
        }
        return overlap;
    }

    /**
     * This method calculates the enlarged MBR as a result of the union between the two MBR's
     * @param mbr is the second MBR
     * @return a new MBR that surrounds the union of the other two MBR's
     */
    public MBR enlargeMBR(MBR mbr) {

        double[][] enlargedBounds = new double[DIMENSION][2];
        for (int i = 0; i < DIMENSION; i++) {
            enlargedBounds[i][0] = Math.min(this.bounds[i][0], mbr.bounds[i][0]);

            enlargedBounds[i][1] = Math.max(this.bounds[i][1], mbr.bounds[i][1]);
        }
        return new MBR(enlargedBounds);
    }

    /**
     * This method calculates the cost of enlarging an MBR
     * @param mbr the second MBR for the cost calculation
     * @return the value of the enlargement cost
     */
    public double areaEnlargementCost(MBR mbr) {
        return enlargeMBR(mbr).area - this.area;
    }

    /**
     * Calculating the margin value of the current MBR (used in chooseSplitAxis)
     * @return the margin value
     */
    public double marginValue() {
        double sum = 0;
        for (int i = 0; i < DIMENSION; i++) {
            sum += this.bounds[i][1] - this.bounds[i][0];
        }
        return sum;
    }

    /**
     * Calculating the area of the bounding rectangle
     * @return the area of the MBR
     */
    public double areaCalc() {
        double area = 1;
        for (int i = 0; i < DIMENSION; i++) {
            area *= bounds[i][1] - bounds[i][0];
        }
        return area;
    }

    /**
     * This method calculates the center of the MBR
     * @return the center of the MBR as a Point
     */
    public Point center() {
        Point center = new Point(DIMENSION);
        for (int i = 0; i < DIMENSION; i++) {
            center.getX()[i] = (this.bounds[i][0] + this.bounds[i][1]) / 2;
        }
        return center;
    }

    /**
     * Calculates the distance between the center of two MBR's
     * @param mbr is the second MBR in the calculation
     * @return the calculated distance
     */
    public double distanceToCenter(MBR mbr) {
        return this.center().distance(mbr.center());
    }

    /**
     * Static method for fitting an MBR around a set of MBR's
     * @param entries the arraylist of entries of which their MBRs' are being fitted
     * @return an MBR which surround all the entries' MBR's
     */
    public static MBR fitMBR(ArrayList<Entry<?>> entries){
        MBR mbr = new MBR(entries.get(0).mbr.getBounds());
        for (Entry<?> entry : entries) {
            mbr = mbr.enlargeMBR(entry.mbr);
        }
        return mbr;
    }

    /**
     * Calculates the minimum distance between a point and the current MBR (used in k-NN query)
     * @param p the point for the distance calculation
     * @return the distance between the point and the MBR
     */
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

    /**
     * Calculates the minimum distance between the current MBR and the beginning of the axis (0,0)
     * @return the calculated distance
     */
    public double minDist(){
        return minDist(new Point(DIMENSION));
    }

    /**
     * Turns the current MBR into a Point based on the lower left bound
     * @return the new Point calculated
     */
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
