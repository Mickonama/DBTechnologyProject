package RTree;

import java.util.ArrayList;
import java.util.Comparator;

public class Entry<T> {
    MBR mbr;
    T pointer;

    public Entry(MBR mbr) {
        this.mbr = mbr;
    }

    public Entry(MBR mbr, T pointer) {
        this(mbr);
        this.pointer = pointer;
    }

    public static class RecordPointer {
        long blockId, recordId;

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

    public void adjustMbr() {
        if (pointer instanceof RecordPointer)
            return;
        Node childNode = (Node) pointer;
        this.mbr = MBR.fitMBR(childNode.entries);


    }

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

    public static Comparator<Entry<?>> minAreaEnlargementCostCriterion(MBR newMBR) {
        return (o1, o2) -> {
            int comparison = Double.compare(o1.mbr.areaEnlargementCost(newMBR), o2.mbr.areaEnlargementCost(newMBR));
            if (comparison == 0)
                return Double.compare(o1.mbr.area, o2.mbr.area);
            return comparison;
        };
    }

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
