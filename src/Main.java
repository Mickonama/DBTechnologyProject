import RTree.*;
import queries.SerialRangeQuery;
import utilities.DiskManager;
import utilities.Point;
import utilities.Record;

import javax.management.Query;
import java.awt.image.AreaAveragingScaleFilter;
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

        MBR queryMBR = new MBR(new double[][]{{40.5915634, 40.5916978}, {0, 9999}});

        long start = System.currentTimeMillis();
        ArrayList<Entry<Entry.RecordPointer>> result = rst.rangeQuery(queryMBR);
        System.out.println("RTREE QUERY: " + (System.currentTimeMillis() - start));

        ArrayList<Record> rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        System.out.println();

        SerialRangeQuery srq = new SerialRangeQuery(dm);

        start = System.currentTimeMillis();
        ArrayList<Record> serialQueryResult = srq.query(queryMBR);
        System.out.println("SERIAL QUERY: " + (System.currentTimeMillis() - start));


//        System.out.println();
        rTreeQueryResult.forEach(System.out::println);
//        System.out.println();
//        serialQueryResult.forEach(System.out::println);

//        System.out.println(rst.DEPTH);

        System.out.println("DELETINGGGGGGGGGGGGGGGGGGG");
        rst.deleteData(new Point(dm.DIMENSION, new double[] {40.5916336, 23.0180504}));
        result = rst.rangeQuery(queryMBR);
        rTreeQueryResult = new ArrayList<>();
        for (Entry<Entry.RecordPointer> entry: result){
            rTreeQueryResult.add(dm.retrieveRecord(entry));
        }
        rTreeQueryResult.forEach(System.out::println);
    }
}