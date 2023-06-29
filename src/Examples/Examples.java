package Examples;

import RTree.Entry;
import RTree.MBR;
import RTree.RStarTree;
import queries.IndexedQuery;
import queries.SerialQuery;
import utilities.DiskManager;
import utilities.OSMParser;
import utilities.Point;
import utilities.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Examples {
    public ArrayList<Example> queryExamples = new ArrayList<Example>();

    public Examples() {
    }

    public void test_mbr() throws IOException {
        for (int ex_i = 0; ex_i < queryExamples.size(); ex_i++) {
            OSMParser osm = new OSMParser("Maps/" + queryExamples.get(ex_i).map_name);
            osm.osmToCsv("coordinates.csv");
            DiskManager dm = new DiskManager("datafile" + ex_i);
            dm.makeDatafile("coordinates.csv");
            RStarTree rst = new RStarTree(8);
            for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
                ArrayList<Record> block = dm.readBlock(i);
                for (int slot = 0; slot < block.size(); slot++) {
                    rst.insertData(new MBR(block.get(slot).getP()), i, slot);
                }
            }

            System.out.println("Queries Time for Map: " + queryExamples.get(ex_i).map_name);
            System.out.println("MBR: " + queryExamples.get(ex_i).mbr);
            System.out.println("Point: " + queryExamples.get(ex_i).point);

            IndexedQuery inq = new IndexedQuery(rst);
            inq.rangeQuery(queryExamples.get(ex_i).mbr);

            SerialQuery srq = new SerialQuery(dm);
            srq.rangeQuery(queryExamples.get(ex_i).mbr);

            System.out.println("\n\n");
        }
    }

    public void test_knn() throws IOException {
        System.out.println("Queries Time for Map: " + queryExamples.get(0).map_name);
        System.out.println("MBR: " + queryExamples.get(0).mbr);
        System.out.println("Point: " + queryExamples.get(0).point);
        for (double f = 0.05; f <= 1.01; f += 0.05) {
            OSMParser osm = new OSMParser("Maps/map0.osm");
            osm.osmToCsv("coordinates.csv");
            DiskManager dm = new DiskManager("datafile" + f);
            dm.makeDatafile("coordinates.csv");
            RStarTree rst = new RStarTree(8);
            for (int i = 1; i < Math.floor(dm.NUMBER_OF_BLOCKS * f); i++) {
                ArrayList<Record> block = dm.readBlock(i);
                for (int slot = 0; slot < block.size(); slot++) {
                    rst.insertData(new MBR(block.get(slot).getP()), i, slot);
                }
//                System.out.println("block " + i + " done");
            }
            System.out.println("Percent: " + f);

            IndexedQuery inq = new IndexedQuery(rst);
            inq.kNNQuery(queryExamples.get(0).point, 100);

            SerialQuery srq = new SerialQuery(dm);
            srq.serialKNN(queryExamples.get(0).point, 100);

            System.out.println("\n");
        }
    }

}
