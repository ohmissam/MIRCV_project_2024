package it.unipi.mircv;

import java.io.*;

public class preProcessing {

    public static void main(String[] args) {

        String path = "C:\\Users\\max99\\Desktop\\uni\\2_MIRCV\\MIRCV_proj2\\data\\collection.tsv";
        FileReader file;
        DocumentIndex docIndex = new DocumentIndex();
        int numOfDocs = 0;
        int docLength = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line); //anche da eliminare
                String[] tokens = line.split(" ");

                for(String token : tokens){
                    token = token.toLowerCase();

                    //text cleaning
                    //tokeinizzazione
                    //togliere la punteggiatura
                    //rimozione stopwords
                    //stemming

                    //per ogni doc calcaolre la lungehzza dei token
                    //per ogni token registrare il doc in cui appare

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
