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

            System.out.println("MBR Time for Map: " + queryExamples.get(ex_i).map_name);
            System.out.println("MBR: " + queryExamples.get(ex_i).mbr);

            IndexedQuery inq = new IndexedQuery(rst);
            inq.rangeQuery(queryExamples.get(ex_i).mbr);

            SerialQuery srq = new SerialQuery(dm);
            srq.rangeQuery(queryExamples.get(ex_i).mbr);

            System.out.println("\n\n");
        }
    }

    public void test_knn() throws IOException {
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

            System.out.println("KNN Time for Map: " + queryExamples.get(ex_i).map_name);
            System.out.println("Point: " + queryExamples.get(ex_i).point);

            IndexedQuery inq = new IndexedQuery(rst);
            inq.kNNQuery(queryExamples.get(ex_i).point, 100);

            SerialQuery srq = new SerialQuery(dm);
            srq.serialKNN(queryExamples.get(ex_i).point, 100);

            System.out.println("\n\n");
        }
    }

    public void test_bulk() throws IOException {
        for (int ex_i = 0; ex_i < queryExamples.size(); ex_i++) {
            System.out.println("Sequential vs Bulk Insert Time for: " + queryExamples.get(ex_i).map_name);

            OSMParser osm = new OSMParser("Maps/" + queryExamples.get(ex_i).map_name);
            osm.osmToCsv("coordinates.csv");
            DiskManager dm = new DiskManager("datafile" + ex_i);
            dm.makeDatafile("coordinates.csv");

            RStarTree sequential = new RStarTree(8);
            long start = System.nanoTime();
            for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
                ArrayList<Record> block = dm.readBlock(i);
                for (int slot = 0; slot < block.size(); slot++) {
                    sequential.insertData(new MBR(block.get(slot).getP()), i, slot);
                }
            }
            System.out.println("Sequential insert completed: " + (System.nanoTime() - start) + " ns");
            RStarTree bulk = new RStarTree(8);
            start = System.nanoTime();
            ArrayList<Entry<Entry.RecordPointer>> recordsToAdd = new ArrayList<>();

            for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
                ArrayList<Record> block = dm.readBlock(i);
                for (int slot = 0; slot < block.size(); slot++) {
                    recordsToAdd.add(new Entry<>(new MBR(block.get(slot).getP()), new Entry.RecordPointer(i, slot)));
                }
            }
            bulk.bulkBuild(recordsToAdd);
            System.out.println("Bulk insert completed: " + (System.nanoTime() - start) + " ns");


            System.out.println("\n\n");
        }
    }

    public void test_skyline() throws IOException {
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

            System.out.println("Skyline Time for Map: " + queryExamples.get(ex_i).map_name);

            IndexedQuery inq = new IndexedQuery(rst);
            inq.skyline();

            System.out.println("\n\n");
        }
    }
}
