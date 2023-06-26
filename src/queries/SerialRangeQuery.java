package queries;

import RTree.Entry;
import RTree.MBR;
import RTree.Node;
import utilities.DiskManager;
import utilities.Point;
import utilities.Record;

import java.util.ArrayList;

public class SerialRangeQuery {
    DiskManager dm;

    public SerialRangeQuery(DiskManager dm) {
        this.dm = dm;
    }

    public ArrayList<Record> query(MBR mbr) {
        ArrayList<Record> queriedRecord = new ArrayList<>();
        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> records = dm.readBlock(i);

            for (Record rec : records){
                MBR recMBR = new MBR(rec.getP());
                if (recMBR.overlaps(mbr))
                    queriedRecord.add(rec);
            }
        }
        return queriedRecord;
    }

    public ArrayList<Record> query(Point p) {
        MBR mbr = new MBR(p);
        return query(mbr);
    }
}

