package it.unipi.dii.aide.mircv.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static it.unipi.dii.aide.mircv.utils.DocumentIndexEntryConfig.*;

public class MainFileReaderForDebugging {

    public static void main(String[] args) {
        try {
            System.out.println("[FILE READER] Reading lexicon file...");
            readLexiconFile(Config.LEXICON_FILE_PATH);

            // Reading and printing the final inverted index file!
            System.out.println("[FILE READER] Reading inverted index files...");
            readInvertedIndexFile(Config.INVINDEX_DOC_IDS_FILE_PATH, Config.INVINDEX_FREQUENCIES_FILE_PATH);

            // Reading and printing the document index file
            System.out.println("[FILE READER] Reading document index file...");
            readDocumentIndexFile(Config.DOCINDEX_FILE_PATH);

            //----------------------for blocks debug-----------------------------------------------------
//            System.out.println("[FILE READER] Reading Lexicon block 1");
//            readBlockLexiconFile(Config.LEXICON_BLOCK_PATH + "1.txt");

//            System.out.println("[FILE READER] Reading inverted index files of Block 1...");
//            readInvertedIndexFile(Config.DOCIDS_BLOCK_PATH + "1.txt", Config.FREQUENCIES_BLOCK_PATH + "1.txt");


        } catch (IOException e) {
            System.err.println("[ERROR] An error occurred while reading the files: " + e.getMessage());
        }
    }

    public static void readBlockLexiconFile(String filePath) throws IOException {
        try (RandomAccessFile lexiconFile = new RandomAccessFile(filePath, "r")) {
            long fileLength = lexiconFile.length();
            int count = 0;
            int first10Count = 0;
            List<String> last10Entries = new ArrayList<>();

            // Print first 10 entries
            System.out.println("\nFirst 10 entries:");

            while (lexiconFile.getFilePointer() < fileLength && first10Count < 10) {
                // Read the term (term) and decode it
                byte[] termBytes = new byte[BlockLexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim(); // Remove any trailing spaces

                // Read and decode the offset docId and frequency
                byte[] offsetDocIdBytes = new byte[BlockLexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[BlockLexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                // Read and decode the length of the posting list
                byte[] postingListLengthBytes = new byte[BlockLexiconEntryConfig.POSTING_LIST_LENGTH];
                lexiconFile.read(postingListLengthBytes);
                int postingListLength = ByteBuffer.wrap(postingListLengthBytes).getInt();

                // Create a string to store the lexicon entry
                String entry = "Term: " + term + "\n" +
                        "OffsetDocId: " + offsetDocId + "\n" +
                        "OffsetFrequency: " + offsetFrequency + "\n" +
                        "PostingListLength: " + postingListLength + "\n" +
                        "--------------------------------";

                // Print the first 10 entries
                System.out.println(entry);
                first10Count++;
                count++;  // Count the total number of entries
            }

            // Now, go back and read all remaining entries to gather the last 10
            List<String> allEntries = new ArrayList<>();
            while (lexiconFile.getFilePointer() < fileLength) {
                // Read the term (term) and decode it
                byte[] termBytes = new byte[BlockLexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim(); // Remove any trailing spaces

                // Read and decode the offset docId and frequency
                byte[] offsetDocIdBytes = new byte[BlockLexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[BlockLexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                // Read and decode the length of the posting list
                byte[] postingListLengthBytes = new byte[BlockLexiconEntryConfig.POSTING_LIST_LENGTH];
                lexiconFile.read(postingListLengthBytes);
                int postingListLength = ByteBuffer.wrap(postingListLengthBytes).getInt();

                // Store the lexicon entry as a string
                String entry = "Term: " + term + "\n" +
                        "OffsetDocId: " + offsetDocId + "\n" +
                        "OffsetFrequency: " + offsetFrequency + "\n" +
                        "PostingListLength: " + postingListLength + "\n" +
                        "--------------------------------";

                allEntries.add(entry);
                count++;  // Count the total number of entries
            }

            // Print the last 10 entries
            System.out.println("\nLast 10 entries:");
            for (int i = Math.max(0, allEntries.size() - 10); i < allEntries.size(); i++) {
                System.out.println(allEntries.get(i));
            }

            // Print the total count of entries
            System.out.println("\nTotal number of lexicon entries: " + count);
        }
    }

    // Method to read and print the content of the inverted index files
    public static void readInvertedIndexFile(String docIdsFilePath, String frequenciesFilePath) throws IOException {
        try (
                RandomAccessFile docIdFile = new RandomAccessFile(docIdsFilePath, "r");
                RandomAccessFile frequencyFile = new RandomAccessFile(frequenciesFilePath, "r")
        ) {
            long docIdFileLength = docIdFile.length();
            long frequencyFileLength = frequencyFile.length();
            int count = 0;

            // Read byte by byte from both files
            while (docIdFile.getFilePointer() < docIdFileLength && frequencyFile.getFilePointer() < frequencyFileLength && count < 105) {
                // Read and decode the docId
                byte[] docIdBytes = new byte[8];  // 8 bytes for docId
                docIdFile.read(docIdBytes);
                long docId = ByteBuffer.wrap(docIdBytes).getLong();

                // Read and decode the frequency
                byte[] frequencyBytes = new byte[4];  // 4 bytes for frequency
                frequencyFile.read(frequencyBytes);
                int frequency = ByteBuffer.wrap(frequencyBytes).getInt();

                // Print the docId and frequency
                System.out.println("DocId: " + docId + " - Frequency: " + frequency);
                count++;
            }
        }
    }

//     Method to read and print the first 200 elements of the DocumentIndex file
    public static void readDocumentIndexFile(String documentIndexFilePath) throws IOException {
        try (RandomAccessFile documentIndexFile = new RandomAccessFile(documentIndexFilePath, "r")) {
            long fileLength = documentIndexFile.length();
            long numberOfRecords = fileLength / DOCUMENT_INDEX_ENTRY_LENGTH;
            int count = 0;

            System.out.println("[INFO] Reading DocumentIndex file... Total records: " + numberOfRecords);

            // Read each record from the file
            while (documentIndexFile.getFilePointer() < fileLength && count < 200) {
                // Read docId
                byte[] docIdBytes = new byte[DOCID_LENGTH];
                documentIndexFile.read(docIdBytes);
                long docId = ByteBuffer.wrap(docIdBytes).getLong();

                // Read docNo
                byte[] docNoBytes = new byte[DOCNO_LENGTH];
                documentIndexFile.read(docNoBytes);
                String docNo = new String(docNoBytes).trim();

                // Read docLength
                byte[] docLengthBytes = new byte[DOCLENGTH_LENGTH];
                documentIndexFile.read(docLengthBytes);
                int docLength = ByteBuffer.wrap(docLengthBytes).getInt();

                // Print the read data
                System.out.println("DocId: " + docId);
                System.out.println("DocNo: " + docNo);
                System.out.println("DocLength: " + docLength);
                System.out.println("-----------------------------");

                count++;
            }
        }
    }

    public static void readLexiconFile(String filePath) throws IOException {
        try (RandomAccessFile lexiconFile = new RandomAccessFile(filePath, "r")) {
            long fileLength = lexiconFile.length();
            int count = 0;
            int first10Count = 0;
            List<String> last10Entries = new ArrayList<>();
            List<String> allEntries = new ArrayList<>();

            System.out.println("\nFirst 10 entries:");

            while (lexiconFile.getFilePointer() < fileLength) {
                // Leggi i dati di una singola entry
                byte[] termBytes = new byte[MergedLexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim();

                byte[] offsetDocIdBytes = new byte[MergedLexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[MergedLexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                byte[] idfBytes = new byte[MergedLexiconEntryConfig.IDF_LENGTH];
                lexiconFile.read(idfBytes);
                double inverseDocumentFrequency = ByteBuffer.wrap(idfBytes).getDouble();

                byte[] docIdsBytesLengthBytes = new byte[MergedLexiconEntryConfig.BYTES_DOCID_LENGTH];
                lexiconFile.read(docIdsBytesLengthBytes);
                int docIdsBytesLength = ByteBuffer.wrap(docIdsBytesLengthBytes).getInt();

                byte[] frequenciesBytesLengthBytes = new byte[MergedLexiconEntryConfig.BYTES_FREQUENCY_LENGTH];
                lexiconFile.read(frequenciesBytesLengthBytes);
                int frequenciesBytesLength = ByteBuffer.wrap(frequenciesBytesLengthBytes).getInt();

                byte[] postingListLengthBytes = new byte[MergedLexiconEntryConfig.POSTING_LIST_LENGTH];
                lexiconFile.read(postingListLengthBytes);
                int postingListLength = ByteBuffer.wrap(postingListLengthBytes).getInt();

                byte[] offsetSkipBlocksBytes = new byte[MergedLexiconEntryConfig.OFFSET_SKIPBLOCKS_LENGTH];
                lexiconFile.read(offsetSkipBlocksBytes);
                long offsetSkipBlock = ByteBuffer.wrap(offsetSkipBlocksBytes).getLong();

                byte[] numberOfSkipBlocksBytes = new byte[MergedLexiconEntryConfig.NUMBER_OF_SKIPBLOCKS_LENGTH];
                lexiconFile.read(numberOfSkipBlocksBytes);
                int numberOfSkipBlocks = ByteBuffer.wrap(numberOfSkipBlocksBytes).getInt();

                byte[] tfidfTermUpperBoundBytes = new byte[MergedLexiconEntryConfig.MAXSCORE_LENGTH];
                lexiconFile.read(tfidfTermUpperBoundBytes);
                int tfidfTermUpperBound = ByteBuffer.wrap(tfidfTermUpperBoundBytes).getInt();

                byte[] bm25TermUpperBoundBytes = new byte[MergedLexiconEntryConfig.MAXSCORE_LENGTH];
                lexiconFile.read(bm25TermUpperBoundBytes);
                int bm25TermUpperBound = ByteBuffer.wrap(bm25TermUpperBoundBytes).getInt();

                // Crea una rappresentazione della entry
                String entry = "Term: " + term + "\n" +
                        "OffsetDocId: " + offsetDocId + "\n" +
                        "OffsetFrequency: " + offsetFrequency + "\n" +
                        "PostingListLength: " + postingListLength + "\n" +
                        "InverseDocumentFrequency: " + inverseDocumentFrequency + "\n" +
                        "DocIdsBytesLength: " + docIdsBytesLength + "\n" +
                        "FrequenciesBytesLength: " + frequenciesBytesLength + "\n" +
                        "NumberOfSkipBlocks: " + numberOfSkipBlocks + "\n" +
                        "OffsetSkipBlock: " + offsetSkipBlock + "\n" +
                        "TFIDFTermUpperBound: " + tfidfTermUpperBound + "\n" +
                        "BM25TermUpperBound: " + bm25TermUpperBound + "\n" +
                        "--------------------------------";

                // Aggiungi l'entry alla lista totale
                allEntries.add(entry);

                // Stampa le prime 10 entry
                if (first10Count < 10) {
                    System.out.println(entry);
                    first10Count++;
                }

                count++; // Incrementa il contatore totale
            }

            // Estrai le ultime 10 entry
            System.out.println("\nLast 10 entries:");
            for (int i = Math.max(0, allEntries.size() - 10); i < allEntries.size(); i++) {
                System.out.println(allEntries.get(i));
            }

            // Stampa il numero totale di entry
            System.out.println("\nTotal number of lexicon entries: " + count);
        }
    }




}
