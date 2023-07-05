import RTree.Entry;
import RTree.MBR;
import RTree.RStarTree;
import queries.IndexedQuery;
import queries.SerialQuery;
import utilities.DiskManager;
import utilities.OSMParser;
import utilities.Point;
import utilities.Record;
import Examples.Example;
import Examples.Examples;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) throws IOException {
        Example ex1 = new Example("map0.osm", new MBR(new double[][]{{19.6731, 19.7009}, {39.7374, 39.7530}}), new Point(2, new double[]{19.68, 39.74})); // .05
        Example ex2 = new Example("map0.osm", new MBR(new double[][]{{19.6691, 19.7162}, {39.7347, 39.7612}}), new Point(2, new double[]{19.68, 39.74})); // .15
        Example ex3 = new Example("map0.osm", new MBR(new double[][]{{19.6554, 19.7131}, {39.7397, 39.7779}}), new Point(2, new double[]{19.68, 39.74})); // .25
        Example ex4 = new Example("map0.osm", new MBR(new double[][]{{19.6224, 19.7141}, {39.7222, 39.7980}}), new Point(2, new double[]{19.68, 39.74})); // .50
        Example ex5 = new Example("map1.osm", new MBR(new double[][]{{22.9399, 22.9420}, {40.6316, 40.6330}}), new Point(2, new double[]{22.9400, 40.632}));
        Example ex6 = new Example("map2.osm", new MBR(new double[][]{{19.92615, 19.93310}, {39.62207, 39.62514}}), new Point(2, new double[]{19.93, 39.623}));
        Examples exs = new Examples();
        exs.queryExamples.add(ex1);
        exs.queryExamples.add(ex2);
        exs.queryExamples.add(ex3);
        exs.queryExamples.add(ex4);
        exs.queryExamples.add(ex5);
        exs.queryExamples.add(ex6);
        exs.test_mbr();
        exs.test_knn();
        exs.test_skyline();
        exs.test_bulk();

        System.exit(0);

    }
}
