public abstract class Entry {

    MBR mbr;
    public abstract boolean isLeaf();

    public MBR getMbr() {
        return mbr;
    }
}
