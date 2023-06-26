package RTree;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {

    int LEVEL;
//    Entry<Node> parentEntry;
//    Node parentNode;

    ArrayList<Entry<?>> entries;

    public Node(int LEVEL) {
        this.LEVEL = LEVEL;
//        parentNode = null;
        entries = new ArrayList<>();
    }


//    public Node(int LEVEL, Node parentNode) {
//        this(LEVEL);
//        this.parentNode = parentNode;
//    }

    public Node(int LEVEL, ArrayList<Entry<?>> entries) {
        this(LEVEL);
        this.entries = entries;
    }


    public ArrayList<Entry<?>> getEntries() {
        return entries;
    }

    public boolean childIsLeaf() {
        return LEVEL - 1 == 0;
    }

    public boolean isLeaf() {
        return LEVEL == 0;
    }

    public void addEntry(Entry<?> entry) {
        entries.add(entry);
    }

    @Override
    public String toString() {
        return "NODE";
    }
}
