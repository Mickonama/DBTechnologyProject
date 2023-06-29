package Examples;

import RTree.MBR;
import utilities.Point;

public class Example {
    String map_name;
    MBR mbr;

    Point point;

    public Example(String map_name, MBR mbr, Point point) {
        this.map_name = map_name;
        this.mbr = mbr;
        this.point = point;
    }
}
