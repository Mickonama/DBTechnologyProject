package RTree;

import java.util.ArrayList;

public class InternalNode extends Node {

//    ArrayList<Entry<Node>> entries;

    public InternalNode(int LEVEL) {
        super(LEVEL);
//        entries = new ArrayList<>();
    }

    public InternalNode(int LEVEL, Entry<Node> parentEntry) {
        super(LEVEL, parentEntry);
//        this.entries = new ArrayList<>();
    }

    public void addEntry(MBR mbr, Node child) {
        this.entries.add(new Entry<>(mbr, child));
    }

    public void addEntry(Entry<Node> entry) {
        this.entries.add(entry);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

//    public ArrayList<Entry<Node>> getEntries() {
//        return entries;
//    }

    public boolean childIsLeaf() {
        return entries.size() > 0 && ((Node)entries.get(0).pointer).isLeaf();

    }
    @Override
    public String toString() {
        return "InternalNode{" +
                "entries=" + entries +
                '}';
    }
}
