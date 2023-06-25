package RTree;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {

    int LEVEL;
    Entry<Node> parentEntry;
    Node parentNode;

    ArrayList<Entry<?>> entries;

    public Node(int LEVEL) {
        this.LEVEL = LEVEL;
        parentEntry = null;
        entries = new ArrayList<>();
    }

    public Node(int LEVEL, Entry<Node> parentEntry) {
        this(LEVEL);
        this.parentEntry = parentEntry;
    }

    public Node(ArrayList<Entry<?>> entries) {
        this.entries = entries;
    }


    public ArrayList<Entry<?>> getEntries() {
        return entries;
    }

    public boolean childIsLeaf() {
        return entries.size() > 0 && entries.get(0).pointer instanceof LeafNode;
    }

    public void addEntry(Entry<?> entry) {
        entries.add(entry);
    }
}
