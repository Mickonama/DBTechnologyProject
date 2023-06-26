import RTree.*;
import utilities.DiskManager;
import utilities.Point;
import utilities.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
//        utilities.OSMParser osm = new utilities.OSMParser("map.osm");
//        osm.osmToCsv();
        DiskManager dm = new DiskManager();
        dm.makeDatafile();
        RStarTree rst = new RStarTree(8);
        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> block = dm.readBlock(i);
            for (int slot = 0; slot < block.size(); slot++) {
                rst.insertData(new MBR(block.get(slot).getP()), i, slot);
            }
            System.out.println("block " + i + " done");
            System.out.println(rst.DEPTH);
        }
        rst.findRecord(new MBR(new double[][]{{40.6275362, 40.6275362},{22.9635657, 22.9635657}}));
    }
}