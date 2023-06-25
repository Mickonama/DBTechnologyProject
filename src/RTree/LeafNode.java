package RTree;

import java.util.ArrayList;

public class LeafNode extends Node {


    static class RecordPointer {
        long blockId, recordId;

        public RecordPointer(long blockId, long recordId) {
            this.blockId = blockId;
            this.recordId = recordId;
        }

        @Override
        public String toString() {
            return "RecordPointer{" +
                    "blockId=" + blockId +
                    ", recordId=" + recordId +
                    '}';
        }
    }


//    ArrayList<Entry<RecordPointer>> entries;

    public LeafNode(int LEVEL) {
        super(LEVEL);
//        this.entries = new ArrayList<>();
    }

    public LeafNode(int LEVEL, Entry<Node> parentEntry) {
        super(LEVEL, parentEntry);
//        this.entries = new ArrayList<>();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public void addEntry(MBR mbr, long blockId, long recordId) {
        this.entries.add(new Entry<>(mbr, new RecordPointer(blockId, recordId)));
    }

    public void addEntry(Entry<RecordPointer> entry) {
        this.entries.add(entry);
    }
//    public ArrayList<Entry<RecordPointer>> getEntries() {
//        return entries;
//    }

    @Override
    public String toString() {
        return "LeafNode{" +
                "entries=" + entries +
                "}\n";
    }
}
