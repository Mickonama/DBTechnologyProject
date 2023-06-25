package RTree;

import java.util.ArrayList;

public class LeafNode extends Node {


    public LeafNode(int LEVEL) {
        super(LEVEL);
    }

    public LeafNode(int LEVEL, Entry<Node> parentEntry) {
        super(LEVEL, parentEntry);
    }

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

    public void addEntry(MBR mbr, long blockId, long recordId) {
        this.entries.add(new Entry<>(mbr, new RecordPointer(blockId, recordId)));
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
