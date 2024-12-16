package it.unipi.dii.aide.mircv.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static it.unipi.dii.aide.mircv.utils.Config.STATISTICS_PATH;

public class Statistics {
    private int numberOfBlocks;
    private int numberOfDocuments;
    private int avdl; //average number of terms per document

    private Statistics() {
        try {
            //creates a new file instance
            File file = new File(STATISTICS_PATH);

            //reads the file
            FileReader fr = new FileReader(file);

            //creates a buffering character input stream
            BufferedReader br = new BufferedReader(fr);

            String line;

            if ((line = br.readLine()) != null) {
                numberOfBlocks = Integer.parseInt(line);
            }
            if ((line = br.readLine()) != null) {
                numberOfDocuments = Integer.parseInt(line);
            }
            if ((line = br.readLine()) != null) {
                avdl = Integer.parseInt(line);
            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Statistics readStatistics() {
        return new Statistics();
    }

    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public int getAvdl() {
        return avdl;
    }

    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "numberOfBlocks=" + numberOfBlocks +
                ", numberOfDocuments=" + numberOfDocuments +
                ", avdl=" + avdl +
                '}';
    }
}
