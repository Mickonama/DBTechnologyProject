package RTree;

import java.util.ArrayList;

public class InternalNode extends Node {


    public InternalNode(int LEVEL) {
        super(LEVEL);
    }

    public InternalNode(int LEVEL, Entry<Node> parentEntry) {
        super(LEVEL, parentEntry);
    }

    public void addEntry(MBR mbr, Node child) {
        this.entries.add(new Entry<>(mbr, child));
    }


//    public ArrayList<Entry<Node>> getEntries() {
//        return entries;
//    }


    @Override
    public String toString() {
        return "InternalNode{" +
                "entries=" + entries +
                '}';
    }
}
