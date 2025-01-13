package it.unipi.dii.aide.mircv;

import it.unipi.dii.aide.mircv.builder.InvertedIndexBuilder;
import it.unipi.dii.aide.mircv.merger.IndexMerger;
import it.unipi.dii.aide.mircv.model.DocumentAfterPreprocessing;
import it.unipi.dii.aide.mircv.model.DocumentIndexEntry;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.FileWriterUtility;
import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static it.unipi.dii.aide.mircv.utils.Config.*;

/**
 * MainComponents class is responsible for extracting a dataset, preprocessing documents,
 * building the inverted index and lexicon, and managing memory usage during the processing.
 * After processing blocks of documents, it merges them into the final index.
 */
public class MainComponents {

    public static void main(String[] args) {
        System.out.println("[MAIN] Starting the extraction of the dataset...");
        System.out.println("[MAIN] Stemming and stopword removal are " + (ENABLE_STEMMING_AND_STOPWORD_REMOVAL ? "ENABLED" : "DISABLED"));

        // Track the start time of the merging phase
        long processBegin = System.nanoTime();


        // Step 1: Extract the dataset and process each document
        processDocuments(TAR_COLLECTION_PATH);
        long blockProcessEnd = System.nanoTime();

        // Step 2: Start the block merging phase
        System.out.println("[MAIN] Starting the merging of the blocks...");
        System.out.println("[MAIN] Compression is " + (ENABLE_COMPRESSION ? "ENABLED" : "DISABLED"));
        System.out.println("[MAIN] Debug Mode is " + (IS_DEBUG_MODE ? "ENABLED" : "DISABLED"));

        // Track the start time of the merging phase
        long mergeBegin = System.nanoTime();

        // Perform the merging operation
        IndexMerger.merge();
        // Track and display the times taken
        System.out.printf("[MAIN] Block indexing completed in %.2fs%n", (blockProcessEnd - processBegin) / 1_000_000_000.0);
        System.out.printf("[MAIN] Merging completed in %.2fs%n", (System.nanoTime() - mergeBegin) / 1_000_000_000.0);
        System.out.printf("[MAIN] Process completed in %.2fs%n", (System.nanoTime() - processBegin) / 1_000_000_000.0);

    }

    /**
     * This method processes the documents from the dataset, builds the inverted index and lexicon,
     * and handles memory flushing and document indexing.
     *
     */
    private static void processDocuments(String datasetPath) {
        try (InputStreamReader inputStreamReader = extractDataset(datasetPath);
             RandomAccessFile documentIndexFile = new RandomAccessFile(DOCINDEX_FILE_PATH, "rw")) {

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            // Initialize necessary components for index building and file writing
            InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder();
            FileWriterUtility fileWriterUtility = new FileWriterUtility();
            int blockNumber = 1; // Counter for the current block being processed.
            int numberOfDocuments = 0; // Counter for total number of documents processed
            float avdl = 0; // Average document length (avdl)
            int numberOfBlockDocuments = 0; // Counter for the number of documents in the current block
            DocumentAfterPreprocessing documentAfterPreprocessing;

            // Track computation start time and set memory usage threshold
            long begin = System.nanoTime();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long threshold = (long) (totalMemory * PERCENTAGE); // Memory threshold to trigger flushing

            System.out.printf("[MAIN] Memory threshold: %.2fMB (%.0f%% of total memory)%n",
                    threshold / (1024.0 * 1024.0), PERCENTAGE * 100);

            // Process each document in the dataset
            while ((line = bufferedReader.readLine()) != null) {
                // Preprocess the document line and extract terms for indexing
                documentAfterPreprocessing = DocumentPreProcessor.processDocument(line, numberOfDocuments);

                if (documentAfterPreprocessing != null && documentAfterPreprocessing.getTerms().length > 0) {
                    // Update average document length (avdl)
                    avdl = (avdl * numberOfDocuments) / (numberOfDocuments + 1)
                            + ((float) documentAfterPreprocessing.getTerms().length) / (numberOfDocuments + 1);

                    numberOfDocuments++;
                    numberOfBlockDocuments++;

                    // Assign a document ID
                    documentAfterPreprocessing.setDocId(numberOfDocuments);

                    // Insert the document's terms into the inverted index
                    invertedIndexBuilder.insertDocument(documentAfterPreprocessing);

                    // Create and write the document entry (ID and length) to the document index file
                    DocumentIndexEntry documentIndexEntry = new DocumentIndexEntry(
                            documentAfterPreprocessing.getDocNo(),
                            documentAfterPreprocessing.getDocumentLength()
                    );
                    fileWriterUtility.writeDocumentEntryToFile(
                            documentAfterPreprocessing.getDocId(),
                            documentIndexEntry,
                            documentIndexFile
                    );

                    // Check if memory usage exceeds threshold and flush if necessary
                    if (!isMemoryAvailable(threshold)) {
                        sortAndFlushIndexAndLexiconBlockToDisk(fileWriterUtility, invertedIndexBuilder, blockNumber, numberOfBlockDocuments);
                        blockNumber++;
                        numberOfBlockDocuments = 0;
                    }

                    // Display progress after processing every 50,000 documents
                    if (numberOfDocuments % 50000 == 0) {
                        System.out.printf("[MAIN] %d documents processed in %.2fs%n",
                                numberOfDocuments, (System.nanoTime() - begin) / 1_000_000_000.0);
                        printMemoryUsedPercentage(); // Output memory usage statistics
                    }
                }
            }

            // Final flush for any remaining documents in the last block
            if (numberOfBlockDocuments > 0) {
                System.out.println("[MAIN] Flushing last block...");
                sortAndFlushIndexAndLexiconBlockToDisk(fileWriterUtility, invertedIndexBuilder, blockNumber, numberOfBlockDocuments);
                fileWriterUtility.writeStatisticsToFile(blockNumber, numberOfDocuments, avdl);
                System.out.println("[MAIN] Block statistics written to disk");
            } else {
                // Write statistics for the final block if no documents remain
                fileWriterUtility.writeStatisticsToFile(blockNumber - 1, numberOfDocuments, avdl);
                System.out.println("[MAIN] Block statistics written to disk");
            }

            // Print processing completion stats
            System.out.printf("[MAIN] Processing completed. Total documents: %d. Total time: %.2fs%n",
                    numberOfDocuments, (System.nanoTime() - begin) / 1_000_000_000.0);

        }
        catch (IOException e) {
            System.err.println("[ERROR] An error occurred: " + e.getMessage());
        }
    }

    /**
     * Checks if the available memory is above the defined threshold.
     * @param threshold The memory threshold in bytes.
     * @return True if memory usage is below the threshold, otherwise False.
     */
    private static boolean isMemoryAvailable(long threshold) {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() < threshold;
    }

    /**
     * Extracts the dataset from a tar.gz archive and returns an InputStreamReader for the extracted content.
     * @param path Path to the tar.gz archive.
     * @return InputStreamReader for the extracted content.
     * @throws IOException If there are issues accessing or extracting the archive.
     */
    private static InputStreamReader extractDataset(String path) throws IOException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(fileInputStream));
        TarArchiveEntry currentEntry = tarInput.getNextTarEntry();

        if (currentEntry != null) {
            System.out.println("[MAIN] Extracting file: " + currentEntry.getName());
            return new InputStreamReader(tarInput, StandardCharsets.UTF_8);
        } else {
            throw new IOException("No files found in the archive.");
        }
    }

    /**
     * Sorts the inverted index and lexicon and flushes them to disk.
     * This method is called when memory usage exceeds the threshold.
     * @param fileWriterUtility Utility class for file writing operations.
     * @param invertedIndexBuilder The builder containing the inverted index and lexicon data.
     * @param blockNumber Block number currently being processed.
     * @param numberOfBlockDocuments Number of documents processed in the current block.
     * @throws IOException If there is an issue during file writing.
     */
    public static void sortAndFlushIndexAndLexiconBlockToDisk(FileWriterUtility fileWriterUtility, InvertedIndexBuilder invertedIndexBuilder,
                                                              int blockNumber, int numberOfBlockDocuments) throws IOException {
        System.out.printf("[FILE WRITER] Flushing block %d (%d documents) to disk...%n", blockNumber, numberOfBlockDocuments);

        // Sort and write the lexicon and inverted index to disk
        invertedIndexBuilder.sortLexicon();
        invertedIndexBuilder.sortInvertedIndex();
        fileWriterUtility.writeInvertedIndexAndLexiconBlockToFiles(invertedIndexBuilder, blockNumber);

        System.out.printf("[FILE WRITER] Block %d successfully written.%n", blockNumber);

        // Clear memory-intensive data structures from the builder
        invertedIndexBuilder.clear();
    }

    /**
     * Outputs memory usage and calculates the percentage of memory currently used.
     */
    private static void printMemoryUsedPercentage() {
        Runtime rt = Runtime.getRuntime();
        long totalMem = rt.totalMemory();
        long usedMem = totalMem - rt.freeMemory();
        double usedPercentage = ((double) usedMem / totalMem) * 100;
        System.out.printf("[MEMORY] Used memory: %.2f%% of total memory.%n", usedPercentage);
    }
}
