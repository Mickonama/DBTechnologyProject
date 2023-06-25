package RTree;

import java.util.ArrayList;
import java.util.Comparator;

class Entry<T> {
    MBR mbr;
    T pointer;

    public Entry(MBR mbr) {
        this.mbr = mbr;
    }

    public Entry(MBR mbr, T pointer) {
        this(mbr);
        this.pointer = pointer;
    }

    public void adjustMbr() {
        if (pointer instanceof LeafNode.RecordPointer)
            return;
        Node childNode = (Node) pointer;

    }

    @Override
    public String toString() {
        return "Entry{" +
                "mbr=" + mbr +
                '}';
    }

    public static Comparator<Entry<?>> minAreaEnlargementCostCriterion(MBR newMBR) {
        return new Comparator<Entry<?>>() {

            @Override
            public int compare(Entry<?> o1, Entry<?> o2) {
                int comparison = Double.compare(o1.mbr.areaEnlargementCost(newMBR), o2.mbr.areaEnlargementCost(newMBR));
                if (comparison == 0)
                    return Double.compare(o1.mbr.area, o2.mbr.area);
                return comparison;
            }

//            @Override
//            public int compare(Entry<Node> o1, Entry<Node> o2) {
//                int comparison = Double.compare(o1.mbr.areaEnlargementCost(newMBR), o2.mbr.areaEnlargementCost(newMBR));
//                if (comparison == 0)
//                    return Double.compare(o1.mbr.area, o2.mbr.area);
//                return comparison;
//            }
//        };
        };
    }

    public static Comparator<Entry<?>> minOverlapCostCriterion(MBR newMBR, ArrayList<Entry<?>> otherEntries) {
        return new Comparator<Entry<?>>() {
            @Override
            public int compare(Entry<?> o1, Entry<?> o2) {
                double o1OverlapCost = 0, o2OverlapCost = 0;
                for (Entry<?> entry : otherEntries) {
                    o1OverlapCost += o1.mbr.enlargeMBR(newMBR).overlapArea(entry.mbr) - o1.mbr.overlapArea(entry.mbr);
                    o2OverlapCost += o2.mbr.enlargeMBR(newMBR).overlapArea(entry.mbr) - o2.mbr.overlapArea(entry.mbr);
                }
                return Double.compare(o1OverlapCost, o2OverlapCost);
            }
        };
    }
}
