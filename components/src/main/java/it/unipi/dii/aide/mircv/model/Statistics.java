package it.unipi.dii.aide.mircv.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static it.unipi.dii.aide.mircv.utils.Config.STATISTICS_PATH;

public class Statistics {
    private int numberOfBlocks;
    private int numberOfDocuments;
    // Average document length tracker
    private int avdl;

    public Statistics() {
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
            if ((line = br.readLine())!= null) {
                avdl = Integer.parseInt(line);
            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
