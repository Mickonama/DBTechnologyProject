import java.util.ArrayList;

public  class Node {

    private final int LEVEL;
    private boolean LEAF;
    ArrayList<Entry> entries;


    public Node(int LEVEL, boolean LEAF) {
        this.LEVEL = LEVEL;
        this.LEAF = LEAF;
        entries = new ArrayList<>();
    }

    public Node(int LEVEL, ArrayList<Entry> entries) {
        this.LEVEL = LEVEL;
        this.LEAF = entries.get(0).isLeaf();
        this.entries = entries;
    }

    public boolean isLeaf() {
        return LEAF;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }
}
