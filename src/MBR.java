import java.util.ArrayList;
import java.util.Arrays;

public class MBR {
    private final int DIMENSION;

    private double[][] bounds;
    private double area;

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

    public MBR enlargeMBR(MBR mbr) {

        double[][] enlargedBounds = new double[DIMENSION][2];
        for (int i = 0; i < DIMENSION; i++) {
            enlargedBounds[i][0] = Math.min(this.bounds[i][0], mbr.bounds[i][0]);

            enlargedBounds[i][1] = Math.max(this.bounds[i][1], mbr.bounds[i][1]);
        }
        return new MBR(enlargedBounds);
    }

    public double areaEnlargementCost(MBR mbr) {
        System.out.println(enlargeMBR(mbr).area + " " + this.area);
        return enlargeMBR(mbr).area - this.area;
    }

    public double areaCalc() {
        double area = 1;
        for (int i = 0; i < DIMENSION; i++) {
            area *= bounds[i][1] - bounds[i][0];
        }
        return area;
    }

    public double[][] getBounds() {
        return bounds;
    }
}
