package RTree;

import utilities.Point;

import java.io.Serializable;
import java.util.*;

public class RStarTree implements Serializable {
    private final int MIN_CAPACITY; //The minimum capacity of the tree's nodes
    private final int MAX_CAPACITY; //Maximum capacity of the tree's nodes
    public int DEPTH; //The height of the tree

    boolean[] overflowOnLevel; //Array to check if an overflowTreatment has been performed at a certain level
    Node root; //Root of the tree

    public RStarTree(int MAX_CAPACITY) {
        root = new Node(0);
        DEPTH = 0;
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.MIN_CAPACITY = (int) Math.round(0.4 * MAX_CAPACITY);
    }

    /**
     * This method chooses the ideal subtree at which an MBR should be placed (based on the min overlap and min area enlargement criterion)
     * @param currentNode
     * @param newMBR is the MBR to be checked
     * @param lvl the level at which the MBR should be placed (can be both leaves or internal nodes)
     * @return the parent entry of the ideal subtree
     */
    private Entry<?> chooseSubTree(Node currentNode, MBR newMBR, int lvl) {
        if (currentNode.LEVEL == lvl + 1)
            return Collections.min(currentNode.entries, Entry.minOverlapCostCriterion(newMBR, currentNode.entries).thenComparing(Entry.minAreaEnlargementCostCriterion(newMBR)));
        else
            return Collections.min(currentNode.entries, Entry.minAreaEnlargementCostCriterion(newMBR));
    }

    /**
     * Implementation of the choose split axis method. The method finds the idea axis based on which the split should be performed.
     * All the (2 * MIN_CAPACITY + 2) distributions are checked and the axis with minimum S metric (based on the margin value criterion)
     * is chosen.
     * @param node to be split
     * @return an integer indicating the axis on which the split index should be chosen
     */
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

    /**
     * Implements the choose split index method. This method finds the ideal index on which the node should be split into two different groups.
     * Given an axis, the distribution is chosen based on the area-value and the overlap-value.
     * @param axis on which the method is applied
     * @param node to be split
     * @return an Array containing 2 nodes as a result of the split
     */
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

    /**
     * This method initiates the splitting process
     * @param node to be split
     * @return an Array contating 2 nodes as a result of the split
     */
    private Node[] split(Node node) {
        int axis = chooseSplitAxis(node);
        return chooseSplitIndex(axis, node);
    }

    /**
     * This method initiates the insertion of a new entry to the tree.
     * @param entry to be added
     */
    public void insertData(Entry<?> entry) {
        overflowOnLevel = new boolean[DEPTH + 1];
        insert(null, entry, 0);
    }

    /**
     * Overloaded insertData method
     * @param mbr of the new entry
     * @param blockId of the new entry
     * @param id of the new entry
     */
    public void insertData(MBR mbr, long blockId, long id) {
        Entry<Entry.RecordPointer> entry = new Entry<>(mbr, new Entry.RecordPointer(blockId, id));
        insertData(entry);
    }

    /**
     * This a recursive method for inserting an entry on a specified level in the tree. If the level is 0 then the insertion is
     * performed on the leaf level. Insertions in internal nodes is a result of the overflow treatment and delete.
     * @param parentEntry the parent entry of the entry that is being inserted
     * @param entry to be inserted
     * @param lvl on which the entry is placed
     * @return an entry from a lower level to be inserted in the current node
     */
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

    /**
     * This method implements the overflow treatment process. Decides whether a node should be re-inserted or split.
     * @param parent entry of the childNode
     * @param childNode to be checked for re-insertion or split
     * @return the parent entry of the new node if a split was performed, null otherwise
     */
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

    /**
     * This method re-inserts the entries of a node on a certain level.
     * @param parent entry of the childNode
     * @param childNode to be re-inserted
     */
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

    /**
     * This method initiates the deletion process
     * @param p point to be deleted
     */
    public void deleteData(Point p) {
        delete(root, new MBR(p));
    }

    /**
     * Recursive method for deleting an entry from the tree. If the capacity of the node after the deletion is greater than
     * the MIN_CAPACITY the process ends, otherwise the node and its parent entry are removed and the orphaned entries are re-inserted
     * into the tree
     * @param currentNode
     * @param mbr the MBR of the entry to be deleted
     * @return an Arraylist containing the orphaned entries
     */
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

    /**
     * This method performs a bottom-up, bulk built, of the tree. The points of the leaf nodes are mapped to a ranked space
     * where the z-order of each point is calculated and sorted based on it. Then, starting from the bottom of the tree
     * the nodes are created until the root is reached.
     * @param entries of the leaf level
     */
    public void bulkBuild (ArrayList<Entry<Entry.RecordPointer>> entries) {

        class EntryWrapper {
            Entry<Entry.RecordPointer> entry;
            Point rankSpacePoint;
        }

        ArrayList<EntryWrapper> rankedEntries = new ArrayList<>();
        int DIMENSION = entries.get(0).mbr.DIMENSION;
        for (Entry<Entry.RecordPointer> entry: entries) {
            EntryWrapper er = new EntryWrapper();
            er.entry = entry;
            er.rankSpacePoint = new Point(DIMENSION);
            rankedEntries.add(er);
        }

        for (int axis = 0; axis < DIMENSION; axis++) {
            int finalAxis = axis;
            rankedEntries.sort((o1, o2) -> {
                int comparison = 0;
                for (int i = finalAxis; i < o1.entry.getMbr().DIMENSION; i++) {
                    comparison = Double.compare(o1.entry.getMbr().toPoint().getX()[i], o2.entry.getMbr().toPoint().getX()[i]);
                    if (comparison == 0)
                        continue;
                    return comparison;
                }
                for (int i = 0; i < finalAxis; i++) {
                    comparison = Double.compare(o1.entry.getMbr().toPoint().getX()[i], o2.entry.getMbr().toPoint().getX()[i]);
                    if (comparison == 0)
                        continue;
                    return comparison;
                }
                return comparison;
            });
            for(int i = 0; i < rankedEntries.size(); i++){
                Point p = rankedEntries.get(i).rankSpacePoint;
                p.getX()[axis] = i;
            }
        }

        rankedEntries.sort(Comparator.comparingInt(o -> o.rankSpacePoint.zOrder()));

        LinkedList<Node> Q = new LinkedList<>();

        int cap = 0;
        Node node = new Node(0);
        for (int i = 0; i < rankedEntries.size(); i++) {
            if(cap == MAX_CAPACITY){
                cap = 0;
                Q.add(node);
                node = new Node(0);
            }
            node.addEntry(rankedEntries.get(i).entry);
            cap++;
        }

        cap = 0;
        Node nextLevelNode = new Node(1);
        while(Q.size() > 1){

            if(cap == MAX_CAPACITY){
                cap = 0;
                Q.add(nextLevelNode);
                nextLevelNode = new Node(nextLevelNode.LEVEL + 1);
            }
            Node nodeToAdd = Q.removeFirst();
            Entry<Node> entryToAdd = new Entry<>(MBR.fitMBR(nodeToAdd.entries), nodeToAdd);
            nextLevelNode.addEntry(entryToAdd);
            cap++;
        }

        this.root = Q.removeFirst();
    }

    public Node getRoot() {
        return root;
    }

}
