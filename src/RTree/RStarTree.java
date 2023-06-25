package RTree;

import utilities.DiskManager;
import utilities.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RStarTree {
    private final int MIN_CAPACITY;
    private final int MAX_CAPACITY;
    private int DEPTH;

    boolean[] overflowOnLevel;
    Node root;

    public RStarTree(int MAX_CAPACITY){
        root = new LeafNode(0);
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.MIN_CAPACITY = (int) Math.round(0.4 * MAX_CAPACITY);
    }

    public LeafNode chooseSubTree(Node start, MBR newMBR) {
        return (LeafNode) chooseSubTree(start, newMBR, DEPTH);
    }
    private Node chooseSubTree(Node start, MBR newMBR, int lvl){
        if (start.LEVEL == lvl)
            return start;
        InternalNode currentNode = (InternalNode) start;
        Entry<?> nextNode;
        if (currentNode.childIsLeaf())
            nextNode = Collections.min(currentNode.entries, Entry.minOverlapCostCriterion(newMBR, currentNode.entries).thenComparing(Entry.minAreaEnlargementCostCriterion(newMBR)));
        else
            nextNode = Collections.min(currentNode.entries, Entry.minAreaEnlargementCostCriterion(newMBR));
        return chooseSubTree(((Node) nextNode.pointer), newMBR);
    }
    private int chooseSplitAxis(Node node) {
        int axis = 0;
        int DIMENSION = node.entries.get(0).mbr.DIMENSION;
        ArrayList<Double> computedS = new ArrayList<>(DIMENSION);
        for (; axis < DIMENSION; axis++) {
            double S = 0;
            for (int lowerUpperValue = 0; lowerUpperValue < 2; lowerUpperValue++) {
                ArrayList<Entry<?>> currentEntries = new ArrayList<>(node.entries);
                int finalAxis = axis;
                int finalLowerUpperValue = lowerUpperValue;
                currentEntries.sort(Comparator.comparingDouble(o -> o.mbr.bounds[finalAxis][finalLowerUpperValue]));
                for (int k = 0; k < MAX_CAPACITY - 2 * MIN_CAPACITY + 2; k++) {
                    Node group1 = new Node((ArrayList<Entry<?>>) currentEntries.subList(0, MIN_CAPACITY - 1 + k));
                    Node group2 = new Node((ArrayList<Entry<?>>) currentEntries.subList(MIN_CAPACITY - 1 + k, currentEntries.size()));

                    S += MBR.fitMBR(group1.entries).marginValue() + MBR.fitMBR(group2.entries).marginValue();
                }
                computedS.add(S);
            }
        }
        return computedS.indexOf(Collections.min(computedS));
    }
    private Node[] chooseSplitIndex(int axis, Node node) {
        class Distribution {
            Node group1, group2;
            double overlapValue, areaValue;
        }
        ArrayList<Distribution> distributions = new ArrayList<>();
        Node[] newNodes = new Node[2];
        for (int lowerUpperValue = 0; lowerUpperValue < 2; lowerUpperValue++) {
            ArrayList<Entry<?>> currentEntries = new ArrayList<>(node.entries);
            int finalLowerUpperValue = lowerUpperValue;
            currentEntries.sort(Comparator.comparingDouble(o -> o.mbr.bounds[axis][finalLowerUpperValue]));
            for (int k = 0; k < MAX_CAPACITY - 2 * MIN_CAPACITY + 2; k++) {
                Distribution currentDistribution = new Distribution();
                currentDistribution.group1 = new Node((ArrayList<Entry<?>>) currentEntries.subList(0, MIN_CAPACITY - 1 + k));
                currentDistribution.group2 = new Node((ArrayList<Entry<?>>) currentEntries.subList(MIN_CAPACITY - 1 + k, currentEntries.size()));

                currentDistribution.overlapValue += MBR.fitMBR(currentDistribution.group1.entries).overlapArea(MBR.fitMBR(currentDistribution.group2.entries));
                currentDistribution.areaValue += MBR.fitMBR(currentDistribution.group1.entries).areaCalc() + MBR.fitMBR(currentDistribution.group2.entries).areaCalc();
                distributions.add(currentDistribution);
            }
        }
        Distribution minDistribution = Collections.min(distributions, Comparator.comparingDouble((Distribution o) -> o.overlapValue).thenComparingDouble(o -> o.areaValue));
        newNodes[0] = minDistribution.group1;
        newNodes[1] = minDistribution.group2;
        return newNodes;
    }
    private Node[] split(Node node) {
        int axis = chooseSplitAxis(node);
        return chooseSplitIndex(axis, node);
    }
    public void insertData(Entry<LeafNode.RecordPointer> entry) {
        insert(entry, DEPTH);
    }

    public void insert(Entry<?> entry, int lvl) {
        Node node = chooseSubTree(root, entry.mbr, lvl);
        if (node.entries.size() < MAX_CAPACITY){
            node.addEntry(entry);
            return;
        }
        overflowOnLevel = new boolean[DEPTH];


    }
    private void overFlowTreatment(int lvl) {

    }
}
