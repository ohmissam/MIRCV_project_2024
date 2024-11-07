package it.unipi.dii.aide.mircv;

import it.unipi.dii.aide.mircv.Config;
import it.unipi.dii.aide.mircv.builder.InvertedIndexBuilder;
import it.unipi.dii.aide.mircv.model.DocumentAfterPreprocessing;
import it.unipi.dii.aide.mircv.model.Lexicon;
import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static it.unipi.dii.aide.mircv.Config.PERCENTAGE;

public class MainComponents {
    // Path of the compressed dataset
    private static final String COMPRESSED_COLLECTION_PATH = Config.COMPRESSED_COLLECTION_PATH;

    public static void main(String[] args) {
        // Print the start of the process
        System.out.println("[MAIN] Starting the extraction of the dataset...");

        // Execute the dataset extraction
        try (InputStreamReader inputStreamReader = extractDataset(COMPRESSED_COLLECTION_PATH)) {
            // Create a BufferedReader to read the document line by line
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder();
            int numberOfDocuments = 1;

            long totalMemory = Runtime.getRuntime().totalMemory();

            //Define the threshold of memory over which the index must be flushed to disk
            long threshold = (long) (totalMemory * PERCENTAGE);


            int blockDocuments = 0;

            // Read the document line by line
            while ((line = bufferedReader.readLine()) != null) {
                // Stampa il documento originale prima del preprocessing
                // System.out.println("[MAIN] Original Document: " + line);

                DocumentAfterPreprocessing documentAfterPreprocessing = DocumentPreProcessor.processDocument(line, numberOfDocuments); // Passa true se necessario per stemming e stopword

                if (documentAfterPreprocessing != null && documentAfterPreprocessing.getTerms().length > 0) {
                    // Stampa il documento dopo il preprocessing
                    // System.out.println("[MAIN] Processed Document: " + documentAfterPreprocessing.toString());

                    invertedIndexBuilder.insertDocument(documentAfterPreprocessing);
                    numberOfDocuments++;

                    blockDocuments++;


                    if(!isMemoryAvailable(threshold)){
                        System.out.println("[MAIN] Flushing" +blockDocuments + "odcuments to disk..");



                    }



                }
            }
            System.out.println("[MAIN] Total documents processed: " + numberOfDocuments);
            // Puoi aggiungere qui il salvataggio dell'inverted index e del lessico

        } catch (IOException e) {
            System.err.println("[ERROR] An error occurred while processing the dataset: " + e.getMessage());
        }
    }

    private static boolean isMemoryAvailable(long threshold){

        //Subtract the free memory at the moment to the total memory allocated obtaining the memory used, then check
        //if the memory used is above the threshold
        return Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory() < threshold;
    }


    private static InputStreamReader extractDataset(String path) throws IOException {
        // Create a File object for the specified path
        File file = new File(path);

        // Attempt to open the tar.gz archive
        FileInputStream fileInputStream = new FileInputStream(file);
        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(fileInputStream));

        // Read the first file from the stream
        TarArchiveEntry currentEntry = tarInput.getNextTarEntry();

        // If the file exists, return the InputStreamReader
        if (currentEntry != null) {
            System.out.println("[MAIN] Extracting file: " + currentEntry.getName());
            return new InputStreamReader(tarInput, StandardCharsets.UTF_8);
        } else {
            throw new IOException("No files found in the archive.");
        }
    }



}

