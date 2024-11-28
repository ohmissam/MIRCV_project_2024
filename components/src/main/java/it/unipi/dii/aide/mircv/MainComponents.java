package it.unipi.dii.aide.mircv;

import it.unipi.dii.aide.mircv.builder.InvertedIndexBuilder;
import it.unipi.dii.aide.mircv.model.DocumentAfterPreprocessing;
import it.unipi.dii.aide.mircv.model.DocumentEntry;
import it.unipi.dii.aide.mircv.utils.FileWriterUtility;
import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static it.unipi.dii.aide.mircv.utils.Config.*;

/**
 * MainComponents class coordinates the creation of an inverted index and lexicon from a compressed dataset.
 * It handles dataset extraction, preprocessing, memory management, and writing the intermediate results to disk.
 */
public class MainComponents {
    static int blockNumber = 1; // Counter for the block currently being processed.

    public static void main(String[] args) throws IOException {
        System.out.println("[MAIN] Starting the extraction of the dataset...");

        // Perform dataset extraction and processing.
        try (InputStreamReader inputStreamReader = extractDataset(COMPRESSED_COLLECTION_PATH);
             RandomAccessFile documentIndexFile = new RandomAccessFile(DOCINDEX_FILE_PATH, "rw")) {

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            // Initialize necessary components
            InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder();
            FileWriterUtility fileWriterUtility = new FileWriterUtility();
            int numberOfDocuments = 0; // Total document counter
            float avdl = 0; // Average document length tracker
            int numberOfBlockDocuments = 0; // Counter for documents in the current block
            DocumentAfterPreprocessing documentAfterPreprocessing;

            // Track computation start time and memory usage
            long begin = System.nanoTime();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long threshold = (long) (totalMemory * PERCENTAGE); // Memory threshold for flushing to disk

            System.out.printf("[MAIN] Memory threshold: %.2fMB (%.0f%% of total memory)%n",
                    threshold / (1024.0 * 1024.0), PERCENTAGE * 100);

            // Start reading and processing the dataset line by line
            while ((line = bufferedReader.readLine()) != null) {
                // Preprocess the document line and extract terms
                documentAfterPreprocessing = DocumentPreProcessor.processDocument(line, numberOfDocuments);

                if (documentAfterPreprocessing != null && documentAfterPreprocessing.getTerms().length > 0) {
                    // Update average document length (avdl)
                    avdl = (avdl * numberOfDocuments) / (numberOfDocuments + 1)
                            + ((float) documentAfterPreprocessing.getTerms().length) / (numberOfDocuments + 1);

                    numberOfDocuments++;
                    numberOfBlockDocuments++;

                    // Assign a document ID
                    documentAfterPreprocessing.setDocId(numberOfDocuments);

                    // Insert terms into the inverted index
                    invertedIndexBuilder.insertDocument(documentAfterPreprocessing);

                    // Write the document entry to the document index file
                    DocumentEntry documentEntry = new DocumentEntry(
                            documentAfterPreprocessing.getDocNo(),
                            documentAfterPreprocessing.getDocumentLength()
                    );
                    fileWriterUtility.writeDocumentEntryToFile(
                            documentAfterPreprocessing.getDocId(),
                            documentEntry,
                            documentIndexFile
                    );

                    // Check memory usage and flush index if needed
                    if (!isMemoryAvailable(threshold)) {
                        flushIndexAndLexiconToDisk(fileWriterUtility, invertedIndexBuilder, blockNumber, numberOfBlockDocuments);
                        blockNumber++;
                        numberOfBlockDocuments = 0;
                    }

                    // Display progress every 50,000 documents
                    if (numberOfDocuments % 50000 == 0) {
                        System.out.printf("[MAIN] %d documents processed in %.2fs%n",
                                numberOfDocuments, (System.nanoTime() - begin) / 1_000_000_000.0);
                        getMemoryUsedPercentage(); // Display memory usage stats
                    }
                }
            }

            // Final flush for remaining documents in the last block
            if (numberOfBlockDocuments > 0) {
                System.out.println("[MAIN] Flushing last block...");
                flushIndexAndLexiconToDisk(fileWriterUtility, invertedIndexBuilder, blockNumber, numberOfBlockDocuments);
                fileWriterUtility.writeStatisticsToFile(blockNumber, numberOfDocuments, avdl);
                System.out.println("[MAIN] Statistics of the blocks written to disk");

            }
            else {
                //Write the blocks statistics
                fileWriterUtility.writeStatisticsToFile(blockNumber-1, numberOfDocuments, avdl);
                System.out.println("[MAIN] Statistics of the blocks written to disk");
            }

            System.out.printf("[MAIN] Processing completed. Total documents: %d. Total time: %.2fs%n",
                    numberOfDocuments, (System.nanoTime() - begin) / 1_000_000_000.0);

        } catch (IOException e) {
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
     * Extracts the dataset from a tar.gz archive and provides a reader for the extracted content.
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
     * Flushes the inverted index and lexicon to disk, sorting them first.
     * @param fileWriterUtility Utility class for file writing operations.
     * @param invertedIndexBuilder The builder containing the index and lexicon data.
     * @param blockNumber Block number being processed.
     * @param numberOfBlockDocuments Number of documents in the current block.
     * @throws IOException If there is an issue during file writing.
     */
    public static void flushIndexAndLexiconToDisk(FileWriterUtility fileWriterUtility, InvertedIndexBuilder invertedIndexBuilder,
                                                  int blockNumber, int numberOfBlockDocuments) throws IOException {
        System.out.printf("[FILE WRITER] Flushing block %d (%d documents) to disk...%n", blockNumber, numberOfBlockDocuments);

        // Sort and write index and lexicon to disk
        invertedIndexBuilder.sortLexicon();
        invertedIndexBuilder.sortInvertedIndex();
        fileWriterUtility.writeInvertedIndexAndLexiconToFiles(invertedIndexBuilder, blockNumber);

        System.out.printf("[FILE WRITER] Block %d successfully written.%n", blockNumber);

        // Clear memory-intensive data structures
        invertedIndexBuilder.clear();
    }

    /**
     * Outputs memory usage and returns the percentage of memory currently used.
     * @return The percentage of memory used.
     */
    private static double getMemoryUsedPercentage() {
        Runtime rt = Runtime.getRuntime();
        long totalMem = rt.totalMemory();
        long usedMem = totalMem - rt.freeMemory();
        double usedPercentage = 100.0 * usedMem / totalMem;

        System.out.printf("[MEMORY] Used memory: %.2f%% of total allocated%n", usedPercentage);
        return usedPercentage;
    }
}
