package queries;

import RTree.Entry;
import RTree.MBR;
import RTree.Node;
import RTree.RStarTree;
import utilities.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *  Class implements different query algorithms
 */
public class IndexedQuery {

    RStarTree tree;

    ArrayList<Entry<Entry.RecordPointer>> foundEntries;

    Point queryPoint;

    PriorityQueue<Entry<Entry.RecordPointer>> kNNQueue;

    public IndexedQuery(RStarTree tree) {
        this.tree = tree;
        this.foundEntries = new ArrayList<>();
    }


    public ArrayList<Entry<Entry.RecordPointer>> rangeQuery(MBR mbr) {
        long start = System.nanoTime();
        ArrayList<Entry<Entry.RecordPointer>> result = rangeQuery(mbr, tree.getRoot());
        System.out.println("Range query completed: " + (System.nanoTime() - start) + " ns");
        return result;
    }
    public ArrayList<Entry<Entry.RecordPointer>> rangeQuery(Point p) {
        MBR mbr = new MBR(p);
        return rangeQuery(mbr);
    }
    private ArrayList<Entry<Entry.RecordPointer>> rangeQuery(MBR mbr, Node currentNode) {
        ArrayList<Entry<Entry.RecordPointer>> foundEntries = new ArrayList<>();
        if (currentNode.isLeaf()) {
            for (Entry<?> entry : currentNode.getEntries()) {
                if (entry.getMbr().overlaps(mbr)) {
                    foundEntries.add(((Entry<Entry.RecordPointer>) entry));
                }
            }
        } else {
            for (Entry<?> entry : currentNode.getEntries())
                if (entry.getMbr().overlaps(mbr)) {
                    ArrayList<Entry<Entry.RecordPointer>> additionalEntries = rangeQuery(mbr, ((Node) entry.getPointer()));
                    foundEntries.addAll(additionalEntries);
                }
        }
        return foundEntries;
    }

    public ArrayList<Entry<Entry.RecordPointer>> kNNQuery(Point p, int k){
        kNNQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> -o.getMbr().minDist(p)));
        long start = System.nanoTime();
        kNNQuery(tree.getRoot(), k, p);
        System.out.println("k-NN completed: " + (System.nanoTime() - start) + " ns");

        return new ArrayList<>(kNNQueue);
    }
    private void kNNQuery(Node currentNode, int k, Point p){

        currentNode.getEntries().sort(Comparator.comparingDouble((o -> o.getMbr().minDist(p))));
        if (!currentNode.isLeaf()) {
            for (Entry<?> entry : currentNode.getEntries()) {
                if(kNNQueue.size() >= k && entry.getMbr().minDist(p) > kNNQueue.peek().getMbr().minDist(p))
                    break;
                kNNQuery(((Node) entry.getPointer()), k, p);
            }
        }else{
            for (Entry<?> entry : currentNode.getEntries()) {
                if(kNNQueue.size() >= k && entry.getMbr().minDist(p) > kNNQueue.peek().getMbr().minDist(p))
                    break;
                if (kNNQueue.size() >= k)
                    kNNQueue.poll();

                kNNQueue.add((Entry<Entry.RecordPointer>) entry);
            }
        }

    }

    public ArrayList<Entry<Entry.RecordPointer>> skyline(){

        long start = System.nanoTime();
        PriorityQueue<Entry<?>> heap = new PriorityQueue<>(Comparator.comparingDouble(o -> o.getMbr().minDist()));
        heap.addAll(tree.getRoot().getEntries());
        ArrayList<Entry<Entry.RecordPointer>> skyline= new ArrayList<>();

        while (!heap.isEmpty()){
            Entry<?> topEntry = heap.poll();
            if (topEntry.dominated(skyline))
                continue;
            if (topEntry.getPointer() instanceof Node) {
                for (Entry<?> child: ((Node) topEntry.getPointer()).getEntries()){
                    if(child.dominated(skyline))
                        continue;
                    heap.add(child);
                }
            }else{
                skyline.add(((Entry<Entry.RecordPointer>) topEntry));
            }
        }

        System.out.println("Skyline completed: " + (System.nanoTime() - start) + " ns");
        return skyline;
    }
}
