package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.LexiconEntryConfig;

import java.io.*;
import java.nio.ByteBuffer;

public class FileReaderUtility {

    public static void main(String[] args) {
        try {
            // Lettura e stampa del file Lexicon
            System.out.println("[MAIN] Reading lexicon file...");
            readLexiconFile(Config.LEXICON_BLOCK_PATH + "1.txt");  // Modifica con il percorso effettivo del file

            // Lettura e stampa dei file docids e frequenze
            System.out.println("[MAIN] Reading inverted index files...");
            readInvertedIndexFile(Config.DOCIDS_BLOCK_PATH + "1.txt", Config.FREQUENCIES_BLOCK_PATH + "1.txt");  // Modifica con il percorso effettivo

        } catch (IOException e) {
            System.err.println("[ERROR] An error occurred while reading the files: " + e.getMessage());
        }
    }

    // Metodo per leggere e stampare il contenuto del file Lexicon
    public static void readLexiconFile(String filePath) throws IOException {
        try (RandomAccessFile lexiconFile = new RandomAccessFile(filePath, "r")) {
            long fileLength = lexiconFile.length();
            while (lexiconFile.getFilePointer() < fileLength) {
                // Leggere il termine (term) e decodificarlo
                byte[] termBytes = new byte[LexiconEntryConfig.TERM_LENGTH];
                lexiconFile.read(termBytes);
                String term = new String(termBytes).trim(); // Rimuove eventuali spazi

                // Leggere e decodificare l'offset docId e frequenza
                byte[] offsetDocIdBytes = new byte[LexiconEntryConfig.OFFSET_DOCIDS_LENGTH];
                lexiconFile.read(offsetDocIdBytes);
                long offsetDocId = ByteBuffer.wrap(offsetDocIdBytes).getLong();

                byte[] offsetFrequencyBytes = new byte[LexiconEntryConfig.OFFSET_FREQUENCIES_LENGTH];
                lexiconFile.read(offsetFrequencyBytes);
                long offsetFrequency = ByteBuffer.wrap(offsetFrequencyBytes).getLong();

                // Leggere e decodificare la lunghezza della lista dei posting
                byte[] postingListLengthBytes = new byte[LexiconEntryConfig.POSTING_LIST_LENGTH];
                lexiconFile.read(postingListLengthBytes);
                int postingListLength = ByteBuffer.wrap(postingListLengthBytes).getInt();

                // Stampa dei dati letti
                System.out.println("Term: " + term);
                System.out.println("OffsetDocId: " + offsetDocId);
                System.out.println("OffsetFrequency: " + offsetFrequency);
                System.out.println("PostingListLength: " + postingListLength);
                System.out.println("--------------------------------");
            }
        }
    }

    // Metodo per leggere e stampare il contenuto dei file docids e frequenze
    public static void readInvertedIndexFile(String docIdsFilePath, String frequenciesFilePath) throws IOException {
        try (
                RandomAccessFile docIdFile = new RandomAccessFile(docIdsFilePath, "r");
                RandomAccessFile frequencyFile = new RandomAccessFile(frequenciesFilePath, "r")
        ) {
            long docIdFileLength = docIdFile.length();
            long frequencyFileLength = frequencyFile.length();

            // Leggi byte per byte da entrambi i file
            while (docIdFile.getFilePointer() < docIdFileLength && frequencyFile.getFilePointer() < frequencyFileLength) {
                // Leggi e decodifica il docId
                byte[] docIdBytes = new byte[8];  // 8 bytes per docId
                docIdFile.read(docIdBytes);
                long docId = ByteBuffer.wrap(docIdBytes).getLong();

                // Leggi e decodifica la frequenza
                byte[] frequencyBytes = new byte[4];  // 4 bytes per frequenza
                frequencyFile.read(frequencyBytes);
                int frequency = ByteBuffer.wrap(frequencyBytes).getInt();

                // Stampa il docId e la frequenza
                System.out.println("DocId: " + docId + " - Frequency: " + frequency);
            }
        }
    }
}
