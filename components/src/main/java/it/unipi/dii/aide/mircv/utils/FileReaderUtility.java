package it.unipi.dii.aide.mircv.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static it.unipi.dii.aide.mircv.utils.DocumentIndexEntryConfig.*;

public class FileReaderUtility {

    public static void main(String[] args) {
        try {
//            System.out.println("[FILE READER] Reading lexicon file...");
            System.out.println("[FILE READER] Reading block 32");
            readLexiconFile(Config.LEXICON_BLOCK_PATH + "32.txt");


//            // Reading and printing the inverted index files
//            System.out.println("[FILE READER] Reading inverted index files...");
//            System.out.println("[FILE READER] Reading inverted index files of Block 1...");
//            readInvertedIndexFile(Config.DOCIDS_BLOCK_PATH + "1.txt", Config.FREQUENCIES_BLOCK_PATH + "1.txt");
//            System.out.println("[FILE READER] Reading inverted index files of Block 2...");
//            readInvertedIndexFile(Config.DOCIDS_BLOCK_PATH + "2.txt", Config.FREQUENCIES_BLOCK_PATH + "2.txt");

            // Reading and printing the document index file
            System.out.println("[FILE READER] Reading document index file...");
            readDocumentIndexFile(Config.DOCINDEX_FILE_PATH);

        } catch (IOException e) {
            System.err.println("[ERROR] An error occurred while reading the files: " + e.getMessage());
        }
    }

    public static void readLexiconFile(String filePath) throws IOException {
        try (RandomAccessFile lexiconFile = new RandomAccessFile(filePath, "r")) {
            long fileLength = lexiconFile.length();
            int count = 0;
            int first10Count = 0;
            List<String> last10Entries = new ArrayList<>();

            // Print first 10 entries
            System.out.println("\nFirst 10 entries:");

            while (lexiconFile.getFilePointer() < fileLength && first10Count < 10) {
                // Read the term (term) and decode it
                byte[] termBytes = new byte[LexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim(); // Remove any trailing spaces

                // Read and decode the offset docId and frequency
                byte[] offsetDocIdBytes = new byte[LexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[LexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                // Read and decode the length of the posting list
                byte[] postingListLengthBytes = new byte[LexiconEntryConfig.POSTING_LIST_LENGTH];
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
                byte[] termBytes = new byte[LexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim(); // Remove any trailing spaces

                // Read and decode the offset docId and frequency
                byte[] offsetDocIdBytes = new byte[LexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[LexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                // Read and decode the length of the posting list
                byte[] postingListLengthBytes = new byte[LexiconEntryConfig.POSTING_LIST_LENGTH];
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

}
