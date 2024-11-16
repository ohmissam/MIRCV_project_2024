package it.unipi.dii.aide.mircv.preProcessing;

import java.io.*;
import java.util.*;

import it.unipi.dii.aide.mircv.utils.Config; // Import the Config class to access configuration settings
import opennlp.tools.stemmer.PorterStemmer;

public class PreProcessing {

    // File paths and flags retrieved from the configuration settings
    private static final String STOPWORDS_PATH = Config.STOPWORDS_PATH; // Path to the stopwords file
    private static final String COLLECTION_PATH = Config.COLLECTION_PATH; // Path to the document collection file
    // Flag to enable both stemming and stopword removal
    private static final boolean ENABLE_STEMMING_AND_STOPWORD_REMOVAL = Config.ENABLE_STEMMING_AND_STOPWORD_REMOVAL;

    // Regex to match camel case strings
    private static final String CAMEL_CASE_MATCHER = "(?<=[a-z])(?=[A-Z])";
    // Maximum length a term should have
    private static final int THRESHOLD = 64;

    // Method to apply stemming to a token using the Porter stemmer
    public static String stemWord(String token, PorterStemmer stemmer) {
        return stemmer.stem(token);
    }

    // Method to check if a token is a stopword
    public static boolean removeStopwords(String token, ArrayList<String> stopwords) {
        return stopwords.contains(token); // Returns true if the token is found in the stopwords list
    }

    // Method to retrieve stopwords from the specified file path
    public static ArrayList<String> retrieveStopwords(String stopwordsPath) throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(stopwordsPath)); // Create a BufferedReader to read the file
        ArrayList<String> stopwordsList = new ArrayList<>(); // List to store stopwords
        String word;
        while ((word = bfr.readLine()) != null) {
            stopwordsList.add(word); // Add each stopword to the list
        }
        bfr.close(); // Close the BufferedReader
        return stopwordsList; // Return the list of stopwords
    }

    // Method to clean the input text by removing URLs, tags, and non-letter characters
    public static String textCleaning(String text) {
        // Remove URLs
        text = text.replaceAll("(https?):\\/\\/(w+)?\\.[a-zA-Z0-9!\"£$%&\\/()=?^'*@:\\._\\-\\+~#]+", " ");
        // Remove tags (e.g., HTML tags)
        text = text.replaceAll("<[a-zA-Z0-9\\s+!\"£$%&\\/()=?^'*@:\\._\\-\\+~#]+>", "");
        // Remove non-letter and non-numeric characters
        text = text.replaceAll("[^\\w\\s]+", " ");
        // Replace multiple spaces with a single space
        text = text.replaceAll("[\\s]{2,}", " ");
        return text.trim().toLowerCase(); // Return cleaned text in lowercase
    }

    // Method to tokenize the input text into individual tokens
    public static String[] tokenize(String text) {
        ArrayList<String> tokens = new ArrayList<>(); // List to store tokens
        String[] splittedText = text.split("\\s"); // Split text by whitespace

        for (String token : splittedText) {
            String[] subtokens = token.split(CAMEL_CASE_MATCHER); // Split CamelCase words
            for (String subtoken : subtokens) {
                // If a token exceeds the threshold length, cut it to the threshold value
                subtoken = subtoken.substring(0, Math.min(subtoken.length(), THRESHOLD));
                // Add token in lowercase to the list
                tokens.add(subtoken.toLowerCase(Locale.ROOT));
            }
        }
        return tokens.toArray(new String[0]); // Return tokens as an array
    }

    // Main method to execute the preprocessing steps
    public static void main(String[] args) throws IOException {
        // Load stopwords from the specified path
        ArrayList<String> stopWords = retrieveStopwords(STOPWORDS_PATH);
        System.out.println("Stopwords loaded: " + stopWords.size()); // Print the number of stopwords loaded

        PorterStemmer stemmer = new PorterStemmer(); // Create a new PorterStemmer instance
        String line; // Variable to hold each line read from the collection
        long numOfDocs = 0; // Counter for valid documents processed
        long docId; // Variable to hold document ID
        int malformedLines = 0; // Counter for malformed lines in the collection

        try (BufferedReader br = new BufferedReader(new FileReader(COLLECTION_PATH))) { // Open the collection file for reading
            System.out.println("Processing collection from: " + COLLECTION_PATH); // Print the path of the collection being processed

            // Process each line of the collection
            while ((line = br.readLine()) != null) {
                String[] temp = line.split("\t"); // Split the line into parts based on tab

                // Check if the line is malformed (must have at least 2 non-empty elements)
                if (temp.length < 2 || temp[0].isEmpty() || temp[1].isEmpty()) {
                    malformedLines++; // Increment the counter for malformed lines
                    continue; // Skip the malformed line
                }

                numOfDocs++; // Increment the valid document counter
                docId = numOfDocs; // Initialize docId with the current counter

                // Clean the content of the document
                line = textCleaning(temp[1]);
                System.out.println("Processing document ID: " + docId); // Print the document ID being processed

                String[] tokens = line.split(" "); // Tokenize the cleaned line into individual tokens
                for (String token : tokens) {
                    // Check if stemming and stopword removal are enabled
                    if (ENABLE_STEMMING_AND_STOPWORD_REMOVAL) {
                        // Remove stopwords if applicable
                        if (removeStopwords(token, stopWords)) {
                            System.out.println("Skipped stopword: " + token); // Print the skipped stopword
                            continue; // Skip stopwords
                        }
                        // Apply stemming if enabled
                        token = stemWord(token, stemmer); // Apply stemming
                        System.out.println("Stemmed token: " + token); // Print the stemmed token
                    } else {
                        // If the flag is not set, process token without stemming or stopword removal
                        System.out.println("Token without processing: " + token);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an error occurs
        }

        // Print summary of processing
        System.out.println("Total documents processed: " + numOfDocs); // Print the total number of documents processed
        System.out.println("Malformed lines skipped: " + malformedLines); // Print the number of malformed lines skipped
    }
}
