package RTree;

import utilities.Point;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class implements an entry of an R*tree node
 * @param <T> is the pointer of an entry (can be either a Node or a RecordPointer)
 */
public class Entry<T> {
    MBR mbr; //The entry's MBR
    T pointer; //The entry's pointer (can be either a Node or a RecordPointer)

    public Entry(MBR mbr) {
        this.mbr = mbr;
    }

    public Entry(MBR mbr, T pointer) {
        this(mbr);
        this.pointer = pointer;
    }

    /**
     * Wrapper class of a pointer to a Record in the datafile (block ID, slot ID)
     */
    public static class RecordPointer {
        long blockId, recordId; //The block ID and slot ID to the pointed Record

        public RecordPointer(long blockId, long recordId) {
            this.blockId = blockId;
            this.recordId = recordId;
        }

        public long getBlockId() {
            return blockId;
        }

        public long getRecordId() {
            return recordId;
        }

        @Override
        public String toString() {
            return "blockID: " + blockId + " slot: " + recordId;
        }
    }

    /**
     * This method adjusts the MBR of the entry
     */
    public void adjustMbr() {
        if (pointer instanceof RecordPointer)
            return;
        Node childNode = (Node) pointer;
        this.mbr = MBR.fitMBR(childNode.entries);


    }

    /**
     * This method checks whether the current entry is dominated by any of the entries in the arraylist
     * @param entries is the arraylist of entries
     * @return True if the current entry is dominated by at least one entry of the arraylist, False otherwise
     */
    public boolean dominated(ArrayList<Entry<RecordPointer>> entries){
        for (Entry<?> s: entries) {
            if (s.mbr.toPoint().dominates(this.mbr.toPoint())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "MBR(" + mbr + ") -> " + pointer;
    }

    public MBR getMbr() {
        return mbr;
    }

    public T getPointer() {
        return pointer;
    }

    /**
     * This method implements a Comparator for the minimum enlargement cost criterion when choosing a subtree
     * @param newMBR the MBR for which the cost is calculated
     * @return the Comparator
     */
    public static Comparator<Entry<?>> minAreaEnlargementCostCriterion(MBR newMBR) {
        return (o1, o2) -> {
            int comparison = Double.compare(o1.mbr.areaEnlargementCost(newMBR), o2.mbr.areaEnlargementCost(newMBR));
            if (comparison == 0)
                return Double.compare(o1.mbr.area, o2.mbr.area);
            return comparison;
        };
    }

    /**
     * This method implements a Comparator for the minimum overlap cost criterion when choosing a subtree
     * @param newMBR the MBR for which the cost is calculated
     * @param otherEntries the entries for witch the overlap cost is calculated
     * @return the Comparator
     */
    public static Comparator<Entry<?>> minOverlapCostCriterion(MBR newMBR, ArrayList<Entry<?>> otherEntries) {
        return (o1, o2) -> {
            double o1OverlapCost = 0, o2OverlapCost = 0;
            for (Entry<?> entry : otherEntries) {
                o1OverlapCost += o1.mbr.enlargeMBR(newMBR).overlapArea(entry.mbr) - o1.mbr.overlapArea(entry.mbr);
                o2OverlapCost += o2.mbr.enlargeMBR(newMBR).overlapArea(entry.mbr) - o2.mbr.overlapArea(entry.mbr);
            }
            return Double.compare(o1OverlapCost, o2OverlapCost);
        };
    }


}
