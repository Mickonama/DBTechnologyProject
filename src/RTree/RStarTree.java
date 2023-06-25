package RTree;

import utilities.DiskManager;
import utilities.Record;

import java.util.Collections;

public class RStarTree {
    //    private final int MIN_CAPACITY;
//    private final int MAX_CAPACITY;
    private int DEPTH;

    Node root;
    public RStarTree() {
        root = new LeafNode(0);
    }

    public void insert(Entry<LeafNode.RecordPointer> entry) {
        LeafNode leaf = chooseSubTree(root, entry.mbr);
        leaf.addEntry(entry);

    }

    public LeafNode chooseSubTree(Node start, MBR newMBR) {
        if (start.isLeaf())
            return (LeafNode) start;
        InternalNode currentNode = (InternalNode) start;
        Entry<?> nextNode;
        if (currentNode.childIsLeaf())
            nextNode = Collections.min(currentNode.entries, Entry.minOverlapCostCriterion(newMBR, currentNode.entries).thenComparing(Entry.minAreaEnlargementCostCriterion(newMBR)));
        else
            nextNode = Collections.min(currentNode.entries, Entry.minAreaEnlargementCostCriterion(newMBR));
        return chooseSubTree(((Node) nextNode.pointer), newMBR);
    }

}
