import Examples.Examples;
import Examples.Example;
import RTree.*;
import queries.IndexedQuery;
import queries.SerialQuery;
import utilities.DiskManager;
import utilities.OSMParser;
import utilities.Point;
import utilities.Record;

import java.util.Locale;
import java.util.Scanner;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.print("Enter the input (.osm / .csv) file name: ");
        String fileName = scanner.nextLine();

        if (fileName.endsWith(".osm")) {
            // Preprocess OSM file
            OSMParser preprocessor = new OSMParser(fileName);
            fileName = fileName.replace(".osm", ".csv");
            preprocessor.osmToCsv(fileName);
        }

        DiskManager dm = new DiskManager("datafile");
        dm.makeDatafile(fileName);

        System.out.print("Enter the number of dimensions: ");
        int dimensions = scanner.nextInt();

        System.out.println();
        RStarTree tree = new RStarTree(8);
        for (int i = 1; i < dm.NUMBER_OF_BLOCKS; i++) {
            ArrayList<Record> block = dm.readBlock(i);
            for (int slot = 0; slot < block.size(); slot++) {
                tree.insertData(new MBR(block.get(slot).getP()), i, slot);
            }
            System.out.println("block " + i + " done");
        }
        System.out.println();

        IndexedQuery iq = new IndexedQuery(tree);
        SerialQuery sq = new SerialQuery(dm);

        System.out.print("Enter 'mbr' to run range query or 'knn' for k-nearest neighbors: ");
        String option = scanner.next();

        if (option.equalsIgnoreCase("mbr")) {
            double[][] mbrBounds = new double[dimensions][2];
            for (int i = 0; i < dimensions; i++) {
                System.out.print("Enter the minimum and maximum value for dimension " + (i + 1) + ": ");
                mbrBounds[i][0] = scanner.nextDouble();
                mbrBounds[i][1] = scanner.nextDouble();
            }
            MBR mbr = new MBR(mbrBounds);
            iq.rangeQuery(mbr);

            SerialQuery srq = new SerialQuery(dm);
            sq.rangeQuery(mbr);
        } else if (option.equalsIgnoreCase("knn")) {
            double[] knnPoint = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                System.out.print("Enter the value for dimension " + (i + 1) + ": ");
                knnPoint[i] = scanner.nextDouble();
            }
            Point p = new Point(dimensions, knnPoint);
            iq.kNNQuery(p, 100);

            sq.serialKNN(p, 100);
        } else {
            System.out.println("Invalid option!");
        }
    }
}