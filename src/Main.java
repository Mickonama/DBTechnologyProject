import Examples.Examples;
import Examples.Example;
import RTree.*;
import queries.IndexedQuery;
import queries.SerialQuery;
import utilities.DiskManager;
import utilities.OSMParser;
import utilities.Point;
import utilities.Record;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IOException {
        Example ex1 = new Example("map0.osm", new MBR(new double[][]{{19.6731, 19.7009}, {39.7374, 39.7530}}), new Point(2, new double[] {19.68, 39.74})); // .05
        Example ex2 = new Example("map0.osm", new MBR(new double[][]{{19.6691, 19.7162}, {39.7347, 39.7612}}), new Point(2, new double[] {19.68, 39.74})); // .15
        Example ex3 = new Example("map0.osm", new MBR(new double[][]{{19.6554, 19.7131}, {39.7397, 39.7779}}), new Point(2, new double[] {19.68, 39.74})); // .25
        Example ex4 = new Example("map0.osm", new MBR(new double[][]{{19.6224, 19.7141}, {39.7222, 39.7980}}), new Point(2, new double[] {19.68, 39.74})); // .50
        Example ex5 = new Example("map1.osm", new MBR(new double[][]{{22.9399, 22.9420}, {40.6316, 40.6330}}), new Point(2, new double[] {22.9400, 40.632}));
        Example ex6 = new Example("map2.osm", new MBR(new double[][]{{19.92615, 19.93310}, {39.62207, 39.62514}}), new Point(2, new double[] {19.93, 39.623}));
        Examples exs = new Examples();
        exs.queryExamples.add(ex1);
        exs.queryExamples.add(ex2);
        exs.queryExamples.add(ex3);
        exs.queryExamples.add(ex4);
        exs.queryExamples.add(ex5);
        exs.queryExamples.add(ex6);
        exs.test_mbr();


        System.exit(0);
        System.out.println("Hello world!");
        OSMParser osm = new OSMParser("map.osm");
        osm.osmToCsv("coordinates.csv");
        DiskManager dm = new DiskManager("datafile");
        dm.makeDatafile("coordinates.csv");
//        System.out.println(dm.RECORDS_PER_BLOCK);
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

        rst = new RStarTree(8);
        ArrayList<Entry<Entry.RecordPointer>> recordsToAdd = new ArrayList<>();

        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> block = dm.readBlock(i);
            for (int slot = 0; slot < block.size(); slot++) {
                recordsToAdd.add(new Entry<>(new MBR(block.get(slot).getP()), new Entry.RecordPointer(i, slot)));
            }
            System.out.println("block " + i + " done");
//            System.out.println(rst.DEPTH);
        }
        rst.bulkBuild(recordsToAdd);

        query = new IndexedQuery(rst);
        result = query.rangeQuery(queryMBR);
        rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        rTreeQueryResult.forEach(System.out::println);

        System.out.println();
    }
}