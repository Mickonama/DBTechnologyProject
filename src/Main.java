import RTree.*;
import utilities.DiskManager;
import utilities.Point;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
//        utilities.OSMParser osm = new utilities.OSMParser("map.osm");
//        osm.osmToCsv();
        DiskManager dm = new DiskManager();
        dm.makeDatafile();
//        System.out.println(dm.NUMBER_OF_BLOCKS + " " + dm.NUMBER_OF_RECORDS);
//        System.out.println();
        for (int i = 1; i < 34; i++) {
            dm.readBlock(i);
        }

        Point p1 = new Point(2, new double[] {2, 4});
        Point p2 = new Point(2, new double[] {3, 6});

        MBR mbr1 = new MBR(new double[][] {{2, 4}, {1, 5}});
        MBR mbr2 = new MBR(new double[][] {{1, 5}, {1, 2}});
        MBR mbr3 = new MBR(new double[][] {{3, 5}, {3, 5}});

        LeafNode ln = new LeafNode(1);
        LeafNode ln2 = new LeafNode(2);

//        RTree.Node.Entry<RTree.Node> entry = new RTree.Node.Entry<>(mbr1, new RTree.InternalNode(2));
        RStarTree rst = new RStarTree();
        InternalNode in1 = new InternalNode(1, null);
        InternalNode in2 = new InternalNode(2);
        InternalNode in3 = new InternalNode(2);
        //a
        in1.addEntry(new MBR(new double[][] {{1, 3}, {0, 2}}), in3);
        //b
//        in1.addEntry(new MBR(new double[][] {{4, 5}, {0, 3}}), in3);
        in1.addEntry(new MBR(new double[][] {{0, 2}, {0, 1}}), in2);
//        in1.addEntry(new MBR(new double[][] {{2, 3}, {0, 4}}), in2);
        LeafNode in4 = new LeafNode(3);
        LeafNode in5 = new LeafNode(3);
        //c
        in2.addEntry(new MBR(new double[][] {{3, 3}, {3, 3}}), in4);
        //d
        in3.addEntry(new MBR(new double[][] {{4, 4}, {5, 5}}), in5);
        //e
        in5.addEntry(mbr1,1,2);
        in4.addEntry(mbr1, 1, 3);

        System.out.println(rst.chooseSubTree(in1, new MBR(new double[][] {{2, 3}, {0, 1}})));
//        System.out.println(in1);

    }
}