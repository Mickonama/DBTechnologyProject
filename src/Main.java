import java.io.IOException;

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
    }
}