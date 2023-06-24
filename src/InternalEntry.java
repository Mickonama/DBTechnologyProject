public class InternalEntry extends Entry{

    Node child;
    @Override
    public boolean isLeaf() {
        return false;
    }
}
