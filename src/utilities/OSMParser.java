package utilities;

import java.io.*;
import java.util.Scanner;

public class OSMParser {
    private final String PATH_TO_OSM;

    public OSMParser(String PATH_TO_OSM) {
        this.PATH_TO_OSM = PATH_TO_OSM;
    }

    public void osmToCsv(){
        StringBuilder nodeId = new StringBuilder();
        StringBuilder lat = new StringBuilder();
        StringBuilder lon = new StringBuilder();

        try {
            File map = new File(PATH_TO_OSM); // .osm file name
            Scanner myReader = new Scanner(map);
            BufferedWriter writer = new BufferedWriter(new FileWriter("coordinates.csv")); // the new .txt file

            char temp;
            int i;
            long tempNodeId= 0;
            double tempLat = 0,tempLon = 0;

            long startTime = System.currentTimeMillis();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine(); // Reading the .osm file

                //Finding the coordinates
                if (data.contains("<node id=\"")) {
                    i = 1;

                    // Get node Id
                    while ((temp = data.charAt(data.lastIndexOf("<node id=\"") + 9 + i)) != '"') {
                        nodeId.append(temp);
                        i++;
                    }

                    tempNodeId = Long.parseLong(nodeId.toString());


                    i = 1;
                    // Get lat
                    while ((temp = data.charAt(data.lastIndexOf("lat=\"") + 4 + i)) != '"') {
                        lat.append(temp);
                        i++;
                    }

                    tempLat = Double.parseDouble(lat.toString());

                    i = 1;
                    // Get lon
                    while ((temp = data.charAt(data.lastIndexOf("lon=\"") + 4 + i)) != '"') {
                        lon.append(temp);
                        i++;
                    }

                    tempLon = Double.parseDouble(lon.toString());

                    writer.write(tempNodeId + "," + tempLat + "," + tempLon + "\n");



                    nodeId.setLength(0);
                    lat.setLength(0);
                    lon.setLength(0);


                }


            }
            long endTime = System.currentTimeMillis();

            myReader.close();
            writer.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
