package RTree;

import utilities.Point;

import java.util.*;

public class RStarTree {
    private final int MIN_CAPACITY;
    private final int MAX_CAPACITY;
    public int DEPTH;

    boolean[] overflowOnLevel;
    Node root;

    public RStarTree(int MAX_CAPACITY) {
        root = new Node(0);
        DEPTH = 0;
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.MIN_CAPACITY = (int) Math.round(0.4 * MAX_CAPACITY);
    }

    private Entry<?> chooseSubTree(Node currentNode, MBR newMBR, int lvl) {
        if (currentNode.LEVEL == lvl + 1)
            return Collections.min(currentNode.entries, Entry.minOverlapCostCriterion(newMBR, currentNode.entries).thenComparing(Entry.minAreaEnlargementCostCriterion(newMBR)));
        else
            return Collections.min(currentNode.entries, Entry.minAreaEnlargementCostCriterion(newMBR));
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
                    Node group1 = new Node(node.LEVEL, new ArrayList<>(currentEntries.subList(0, MIN_CAPACITY - 1 + k)));
                    Node group2 = new Node(node.LEVEL, new ArrayList<>(currentEntries.subList(MIN_CAPACITY - 1 + k, currentEntries.size())));

                    S += MBR.fitMBR(group1.entries).marginValue() + MBR.fitMBR(group2.entries).marginValue();
                }
            }
            computedS.add(S);
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
                currentDistribution.group1 = new Node(node.LEVEL, new ArrayList<>(currentEntries.subList(0, MIN_CAPACITY - 1 + k)));
                currentDistribution.group2 = new Node(node.LEVEL, new ArrayList<>(currentEntries.subList(MIN_CAPACITY - 1 + k, currentEntries.size())));

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

    public void insertData(Entry<?> entry) {
        overflowOnLevel = new boolean[DEPTH + 1];
        insert(null, entry, 0);
    }

    public void insertData(MBR mbr, long blockId, long id) {
        Entry<Entry.RecordPointer> entry = new Entry<>(mbr, new Entry.RecordPointer(blockId, id));
        insertData(entry);
    }

    public Entry<?> insert(Entry<?> parentEntry, Entry<?> entry, int lvl) {
        Node currentNode;
        if (parentEntry == null)
            currentNode = root;
        else {
            parentEntry.mbr = parentEntry.mbr.enlargeMBR(entry.mbr);
            currentNode = (Node) parentEntry.pointer;
        }

        if (currentNode.LEVEL == lvl) {
            currentNode.addEntry(entry);
        } else {
            Entry<?> idealEntry = chooseSubTree(currentNode, entry.mbr, lvl);

            Entry<?> newEntry = insert(idealEntry, entry, lvl);

            if (newEntry != null) {
                currentNode.addEntry(newEntry);
            }
        }
        if (currentNode.entries.size() > MAX_CAPACITY) {
            return overFlowTreatment(parentEntry, currentNode);
        }
        return null;

    }

    private Entry<?> overFlowTreatment(Entry<?> parent, Node childNode) {
        int lvl = childNode.LEVEL;
        if (!overflowOnLevel[lvl] && lvl != DEPTH) {
            overflowOnLevel[lvl] = true;
            reInsert(parent, childNode);
            return null;
        }

        Node[] newNodes = split(childNode);

        childNode.entries = newNodes[0].entries;

        if (lvl == DEPTH) {
            DEPTH++;
            Node newRoot = new Node(DEPTH);
            newRoot.addEntry(new Entry<>(MBR.fitMBR(childNode.entries), childNode));
            newRoot.addEntry(new Entry<>(MBR.fitMBR(newNodes[1].entries), newNodes[1]));
            this.root = newRoot;
            return null;
        }

        parent.adjustMbr();
        return new Entry<>(MBR.fitMBR(newNodes[1].entries), newNodes[1]);
    }

    private void reInsert(Entry<?> parent, Node childNode) {
        Node parentNode = (Node) parent.pointer;
        MBR bb = MBR.fitMBR(parentNode.entries);
        childNode.entries.sort(Comparator.comparingDouble(o -> o.mbr.distanceToCenter(bb)));
        int P = (int) Math.round(0.3 * MAX_CAPACITY);
        ArrayList<Entry<?>> removedEntries = new ArrayList<>(childNode.entries.subList(childNode.entries.size() - P, childNode.entries.size()));

        for (int i = 0; i < P; i++) {
            childNode.entries.remove(childNode.entries.size() - 1);
        }

        parent.adjustMbr();

        for (Entry<?> re : removedEntries) {
            insert(null, re, childNode.LEVEL);
        }

    }

    public void deleteData(Point p) {
        delete(root, new MBR(p));
    }

    private ArrayList<Entry<?>> delete(Node currentNode, MBR mbr) {


        Iterator<Entry<?>> iter = currentNode.entries.iterator();

        while (iter.hasNext()) {
            Entry<?> currentEntry = iter.next();
            if (currentEntry.mbr.overlaps(mbr)) {
                if (currentNode.isLeaf()) {
                    iter.remove();
                    return currentNode.entries;
                }
                ArrayList<Entry<?>> orphans = delete(((Node) currentEntry.pointer), mbr);
                if (orphans == null)
                    return null;
                if (orphans.size() >= MIN_CAPACITY) {
                    currentEntry.adjustMbr();
                } else {
                    iter.remove();
                    for (Entry<?> orphan : orphans) {
                        insert(currentEntry, orphan, currentNode.LEVEL - 1);
                    }
                }
            }
        }
        return null;

    }

    public Node getRoot() {
        return root;
    }

}
