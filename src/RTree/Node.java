package RTree;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Node implements Serializable {


    private final int LEVEL;
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

    public abstract boolean isLeaf();
}
