package queries;

import RTree.MBR;
import utilities.DiskManager;
import utilities.Point;
import utilities.Record;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This class implements a serial range and k-NN query
 */
public class SerialQuery {
    DiskManager dm;


    public SerialQuery(DiskManager dm) {
        this.dm = dm;
    }

    public ArrayList<Record> rangeQuery(MBR mbr) {
        long start = System.nanoTime();
        ArrayList<Record> result = performRangeQuery(mbr);
        System.out.println("Serial range query completed: " + (System.nanoTime() - start) + " ns");
        return result;
    }

    public ArrayList<Record> rangeQuery(Point p) {
        MBR mbr = new MBR(p);
        return rangeQuery(mbr);
    }

    /**
     * This method implements a serial range query within an n-dimensional bounded rectangle
     * @param mbr is the minimum bounding rectangle of the range query
     * @return an arraylist of Records that lie within the MBR of the range query
     */
    private ArrayList<Record> performRangeQuery(MBR mbr) {
        ArrayList<Record> queriedRecord = new ArrayList<>();
        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> records = dm.readBlock(i);

            for (Record rec : records) {
                MBR recMBR = new MBR(rec.getP());
                if (recMBR.overlaps(mbr))
                    queriedRecord.add(rec);
            }
        }
        return queriedRecord;
    }

    /**
     * This method implements a serial k-nearest neighbor query
     * @param p the point for which the nearest neighbors are calculates
     * @param k the k-neighbors parameter
     * @return an arraylist of the k nearest neighbors of point p
     */
    public ArrayList<Record> serialKNN(Point p, int k) {
        long start = System.nanoTime();
        PriorityQueue<Record> kNNs = new PriorityQueue<>(Comparator.comparingDouble(o -> -new MBR(o.getP()).minDist(p)));
        for (int j = 1; j < dm.NUMBER_OF_BLOCKS; j++) {
            ArrayList<Record> blockRecords = dm.readBlock(j);
            for (Record record : blockRecords) {
                kNNs.add(record);
                if (kNNs.size() > k)
                    kNNs.poll();

            }
        }
        System.out.println("Serial k-NN completetd: " + (System.nanoTime() - start) + " ns");
        return new ArrayList<>(kNNs);
    }

}

