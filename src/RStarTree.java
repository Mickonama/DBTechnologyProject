import java.util.ArrayList;

public class RStarTree {
//    private final int MIN_CAPACITY;
//    private final int MAX_CAPACITY;
    private int DEPTH;

    Node root;

    public RStarTree() {
        root = new Node(0, true);
    }

    public Node chooseSubTree(Node start, MBR newMbr) {
        if (start.isLeaf())
            return start;
        if (!start.)
    }

    public Leaf chooseLeaf(Node startNode, Rectangle newMbr) {
        if(startNode.isLeaf()) {
            return (Leaf)startNode;
        }

        else {
            ArrayList<Long> childPointers = startNode.pointersToChildren;
            assert childPointers.size() > 0;
            ArrayList<Node> children = new ArrayList<Node>(childPointers.size());
            //φορτωση όλων των παιδιών
            for (long childId : childPointers) {
                try {
                    children.add(disk.loadNode(childId));
                } catch (FileNotFoundException e) {
                    System.err.println("Exception while loading node from disk. message = "+e.getMessage());
                }
            }

            //είναι τα παιδιά φύλλα;
            if (children.get(0).isLeaf()) {
                ArrayList<Double> minOverlap = new ArrayList<Double>();
                ArrayList<Node> cands = new ArrayList<Node>();

                for (Node child : children) {
                    Rectangle union = child.returnMBR().union(newMbr);
                    double deltaOverlap = 0;

                    for (Node otherChild : children) {
                        if (otherChild == child) {
                            continue;
                        }

                        deltaOverlap += union.overlap(otherChild.returnMBR()) -
                                child.returnMBR().overlap(otherChild.returnMBR());

                    }

                    if (minOverlap.size() == 0) {
                        cands.add(child);
                        minOverlap.add(deltaOverlap);
                    } else {
                        if (minOverlap.get(0) > deltaOverlap) {
                            minOverlap.removeAll(minOverlap);
                            cands.removeAll(cands);
                            minOverlap.add(deltaOverlap);
                            cands.add(child);
                        }
                        else if (minOverlap.get(0) == deltaOverlap) {
                            minOverlap.add(deltaOverlap);
                            cands.add(child);
                        }
                    }
                }

                if(cands.size() == 1)
                    return chooseLeaf(cands.get(0), newMbr);
                else{
                    ArrayList<Double> minAreas = new ArrayList<Double>();
                    ArrayList<Node> cands2 = new ArrayList<Node>();

                    double deltaV;
                    for (Node candNode : cands) {
                        deltaV = candNode.returnMBR().incrementCalculation(newMbr);
                        if(minAreas.size() == 0 || minAreas.get(0) > deltaV) {
                            minAreas.removeAll(minAreas);
                            cands2.removeAll(cands2);
                            minAreas.add(deltaV);
                            cands2.add(candNode);
                        }
                        else if (minAreas.get(0) == deltaV) {
                            minAreas.add(deltaV);
                            cands2.add(candNode);
                        }
                    }

                    if(cands2.size() == 1)
                        return chooseLeaf(cands2.get(0), newMbr);
                    else {
                        double minArea = Double.MAX_VALUE;
                        Node candidate = null;
                        for (Node candNode : cands2) {
                            double vol = candNode.returnMBR().volume();
                            if( vol < minArea ){
                                minArea = vol;
                                candidate = candNode;
                            }
                        }
                        return chooseLeaf(candidate, newMbr);
                    }
                }
            } else {
                ArrayList<Double> minAreas = new ArrayList<Double>();
                ArrayList<Node> cands = new ArrayList<Node>();

                double deltaV;
                for (Node candNode : children) {
                    deltaV = candNode.returnMBR().incrementCalculation(newMbr);
                    if(minAreas.size() == 0 || minAreas.get(0) > deltaV) {
                        minAreas.removeAll(minAreas);
                        cands.removeAll(cands);
                        minAreas.add(deltaV);
                        cands.add(candNode);
                    }
                    else if (minAreas.get(0) == deltaV) {
                        minAreas.add(deltaV);
                        cands.add(candNode);
                    }
                }

                if(cands.size() == 1)
                    return chooseLeaf(cands.get(0), newMbr);
                else {
                    double minArea = Double.MAX_VALUE;
                    Node candidate = null;
                    for (Node candNode : cands) {
                        double vol = candNode.returnMBR().volume();
                        if( vol < minArea ){
                            minArea = vol;
                            candidate = candNode;
                        }
                    }
                    return chooseLeaf(candidate, newMbr);
                }
            }
        }
    }
}
