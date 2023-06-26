package utilities;

import utilities.Point;

import java.io.Serializable;

public class Record implements Serializable {
    int id;
    long locId;
    Point p;

    public Record(int id, long locId, Point p) {
        this.id = id;
        this.locId = locId;
        this.p = p;
    }

    @Override
    public String toString() {
        return "recID:" + id +
                " (" + locId + ", " + p + ")";
    }

    public boolean empty() {
        return id == 0 && locId == 0 && p == null;
    }

    public int getId() {
        return id;
    }

    public long getLocId() {
        return locId;
    }

    public Point getP() {
        return p;
    }


}
