import RTree.*;
import queries.IndexedQuery;
import queries.SerialQuery;
import utilities.DiskManager;
import utilities.Point;
import utilities.Record;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
//        utilities.OSMParser osm = new utilities.OSMParser("map.osm");
//        osm.osmToCsv();
        DiskManager dm = new DiskManager();
        dm.makeDatafile();
        System.out.println(dm.RECORDS_PER_BLOCK);
        RStarTree rst = new RStarTree(8);
        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> block = dm.readBlock(i);
            for (int slot = 0; slot < block.size(); slot++) {
                rst.insertData(new MBR(block.get(slot).getP()), i, slot);
            }
            System.out.println("block " + i + " done");
//            System.out.println(rst.DEPTH);
        }
        System.out.println();

//        MBR queryMBR = new MBR(new double[][]{{40.5915634, 40.5916978}, {0, 9999}});
        MBR queryMBR = new MBR(new double[][]{{2, 3}, {1, 2}});

        IndexedQuery query = new IndexedQuery(rst);
        ArrayList<Entry<Entry.RecordPointer>> result = query.rangeQuery(queryMBR);
        ArrayList<Record> rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        rTreeQueryResult.forEach(System.out::println);

        System.out.println();
        SerialQuery srq = new SerialQuery(dm);


        ArrayList<Record> serialQueryResult = srq.rangeQuery(queryMBR);


//        System.out.println();
//        System.out.println();
        serialQueryResult.forEach(System.out::println);

//        System.out.println(rst.DEPTH);

//        System.out.println("DELETINGGGGGGGGGGGGGGGGGGG");
//        rst.deleteData(new Point(dm.DIMENSION, new double[] {40.5916336, 23.0180504}));
//        result = query.rangeQuery(queryMBR);
//        rTreeQueryResult = new ArrayList<>();
//        for (Entry<Entry.RecordPointer> entry: result){
//            rTreeQueryResult.add(dm.retrieveRecord(entry));
//        }
//        rTreeQueryResult.forEach(System.out::println);

        System.out.println("KNNNNNNNNNNNNNNNNNNNN");
        result = query.kNNQuery(new Point(2, new double[] {0, 0}), 3);
        rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        rTreeQueryResult.forEach(System.out::println);


        System.out.println("SERIAL KNN");
        serialQueryResult = srq.serialKNN(new Point(2, new double[] {0, 0}), 3);
        serialQueryResult.forEach(System.out::println);

        System.out.println("SKYLINE BITCHES");
        result = query.skyline();
        rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        rTreeQueryResult.forEach(System.out::println);

        int x = 19, y = 47, z = 0;
        Point p = new Point(3, new double[] {2, 3});
        for (int i = 0; i < Integer.SIZE; i++) {
            for (int axis = 0; axis < 2; axis++) {
                z |= ((((int) p.getX()[axis]) & (1 << i)) << (i + axis));
            }
        }
        System.out.println(z);
    }
}