public class LeafEntry extends Entry {

    long blockId, recordId;


    @Override
    public boolean isLeaf() {
        return true;
    }
}
