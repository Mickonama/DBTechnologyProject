import java.io.Serializable;
import java.util.ArrayList;

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

    public void setX(double[] x) {
        this.x = x;
    }
}
