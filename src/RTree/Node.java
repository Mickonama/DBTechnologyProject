package RTree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a Node of the R*tree
 */
public class Node implements Serializable {

    int LEVEL; //The depth level on which this node is located

    ArrayList<Entry<?>> entries; //The entries that are contained within the node

    public Node(int LEVEL) {
        this.LEVEL = LEVEL;
        entries = new ArrayList<>();
    }


    public Node(int LEVEL, ArrayList<Entry<?>> entries) {
        this(LEVEL);
        this.entries = entries;
    }


    public ArrayList<Entry<?>> getEntries() {
        return entries;
    }

    /**
     * Checks if the current node is at the leaf level
     * @return True if the node is a leaf, False otherwise
     */
    public boolean isLeaf() {
        return LEVEL == 0;
    }

    /**
     * Adds an entry to the nodes entry arraylist
     * @param entry the entry to be added
     */
    public void addEntry(Entry<?> entry) {
        entries.add(entry);
    }

    @Override
    public String toString() {
        return "NODE";
    }
}
