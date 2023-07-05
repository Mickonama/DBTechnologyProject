package utilities;

import RTree.Entry;
import RTree.RStarTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class manages files in the secondary memory
 */
public class DiskManager {
    public final int BLOCK_SIZE; //Block size in bytes, 32KB is default
    public final int DIMENSION;
    public final int RECORDS_PER_BLOCK;
    public int NUMBER_OF_BLOCKS = 0;
    public int NUMBER_OF_RECORDS = 0;

    private String filename;

    public DiskManager(String filename) throws IOException {
        this.BLOCK_SIZE = 32768;
        this.DIMENSION = 2;
        RECORDS_PER_BLOCK = recordsPerBlockCalc(DIMENSION);
        this.filename = filename;
    }

    public DiskManager(String filename, int DIMENSION) throws IOException {
        this.BLOCK_SIZE = 32768;
        this.DIMENSION = DIMENSION;
        RECORDS_PER_BLOCK = recordsPerBlockCalc(DIMENSION);
        this.filename = filename;

    }

    public DiskManager(String filename, int BLOCK_SIZE, int DIMENSION) throws IOException {
        this.BLOCK_SIZE = BLOCK_SIZE;
        this.DIMENSION = DIMENSION;
        RECORDS_PER_BLOCK = recordsPerBlockCalc(DIMENSION);
        this.filename = filename;
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeObject(obj);

        return out.toByteArray();
    }

    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(in);

        return ois.readObject();
    }

    private int recordsPerBlockCalc(int dim) {
        ArrayList<Record> records = new ArrayList<>();

        int i = 0;
        while (true) {


            Record r = new Record(0, 0, new Point(dim));
            records.add(r);

            byte[] recordInBytes;

            try {
                recordInBytes = serialize(records);

                if (recordInBytes.length > BLOCK_SIZE) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            i++;
        }
        return i;
    }

    private void writeBlock(ArrayList<Record> records) {
        try {
            NUMBER_OF_RECORDS += records.size();
            metadataUpdate();
            byte[] recordInBytes, block;

            recordInBytes = serialize(records);
            block = new byte[BLOCK_SIZE];

//            System.arraycopy(goodPutLengthInBytes, 0, block, 0, goodPutLengthInBytes.length);
            System.arraycopy(recordInBytes, 0, block, 0, recordInBytes.length);

            FileOutputStream fos = new FileOutputStream(this.filename, true);
            BufferedOutputStream bout = new BufferedOutputStream(fos);

            bout.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void metadataUpdate() {
        try {

            ArrayList<Integer> metadata = new ArrayList<>();
            metadata.add(DIMENSION);
            metadata.add(BLOCK_SIZE);
            metadata.add(++NUMBER_OF_BLOCKS);
            metadata.add(NUMBER_OF_RECORDS);

            byte[] metaDataInBytes, goodPutLengthInBytes, block;

            metaDataInBytes = serialize(metadata);
            goodPutLengthInBytes = serialize(metaDataInBytes.length);
            block = new byte[BLOCK_SIZE];

            System.arraycopy(goodPutLengthInBytes, 0, block, 0, goodPutLengthInBytes.length);
            System.arraycopy(metaDataInBytes, 0, block, goodPutLengthInBytes.length, metaDataInBytes.length);

            RandomAccessFile f = new RandomAccessFile(new File(this.filename), "rw");
            f.write(block);
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Record> readBlock(int blockId) {
        try {

            RandomAccessFile raf = new RandomAccessFile(new File(this.filename), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);

            raf.seek((long) blockId * BLOCK_SIZE);

            byte[] block;

            block = new byte[BLOCK_SIZE];

            if (bis.read(block, 0, BLOCK_SIZE) != BLOCK_SIZE) {
                throw new IllegalStateException("Block size read was not of " + BLOCK_SIZE + " bytes");
            }
            ArrayList<Record> records = (ArrayList<Record>) deserialize(block);

            return records;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void makeDatafile(String csv_filename) {
        try {

            // Deleting if already exists
            Files.deleteIfExists(Paths.get(this.filename));
            ArrayList<Record> recordBlock = new ArrayList<>();
            BufferedReader csvReader = (new BufferedReader(new FileReader(csv_filename))); // BufferedReader used to read the data from the csv file

            int slot = 0;
            String line;
            int maxRecordsInBlock = recordsPerBlockCalc(DIMENSION);
            metadataUpdate();
            while ((line = csvReader.readLine()) != null) {

                if (recordBlock.size() == maxRecordsInBlock) {
                    writeBlock(recordBlock);
                    recordBlock = new ArrayList<>();
                    slot = 0;
                }
                String[] items = line.split(",");
                long locId = Long.parseLong(items[0]);
                double[] x = new double[DIMENSION];
                for (int i = 1; i < items.length; i++) {
                    x[i - 1] = Double.parseDouble(items[i]);
                }
                recordBlock.add(new Record(slot, locId, new Point(DIMENSION, x)));
                slot++;
            }
            csvReader.close();

            if (recordBlock.size() > 0) {
                writeBlock(recordBlock);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeIndexFile(RStarTree tree){
        try {

            byte[] treeInBytes, block;

            treeInBytes = serialize(tree);
            block = new byte[BLOCK_SIZE];

            System.arraycopy(treeInBytes, 0, block, 0, treeInBytes.length);

            FileOutputStream fos = new FileOutputStream(this.filename, true);
            BufferedOutputStream bout = new BufferedOutputStream(fos);

            bout.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Record retrieveRecord(Entry<Entry.RecordPointer> entry){
        return retrieveRecord(entry.getPointer().getBlockId(), entry.getPointer().getRecordId());
    }

    public Record retrieveRecord(long blockId, long slot){
        return readBlock(((int) blockId)).get(((int) slot));
    }

}
