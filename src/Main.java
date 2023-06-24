import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
//        OSMParser osm = new OSMParser("map.osm");
//        osm.osmToCsv();
        DiskManager dm = new DiskManager();
        dm.makeDatafile();
//        System.out.println(dm.NUMBER_OF_BLOCKS + " " + dm.NUMBER_OF_RECORDS);
//        System.out.println();
        for (int i = 1; i < 34; i++) {
            dm.readBlock(i);
        }

        Point p1 = new Point(2, new double[] {2, 4});
        Point p2 = new Point(2, new double[] {3, 6});

        MBR mbr1 = new MBR(new double[][] {{2, 4}, {1, 5}});
        MBR mbr2 = new MBR(new double[][] {{1, 5}, {1, 2}});
        MBR mbr3 = new MBR(new double[][] {{3, 5}, {3, 5}});


    }
}