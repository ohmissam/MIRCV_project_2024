package it.unipi.dii.aide.mircv.preProcessing;

import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.model.DocumentAfterPreprocessing;
import opennlp.tools.stemmer.PorterStemmer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentPreProcessor {
    // Path to the stopwords file
    private static final String STOPWORDS_PATH = Config.STOPWORDS_PATH;

    // List of strings containing the stopwords
    private static List<String> stopWords =  loadStopWords();

    // Flag to enable both stemming and stopword removal
    private static final boolean ENABLE_STEMMING_AND_STOPWORD_REMOVAL = Config.ENABLE_STEMMING_AND_STOPWORD_REMOVAL;

    public static List<String> getStopWords() {
        return stopWords;
    }

    /**
     * Process a single document by tokenizing, removing stopwords, and applying stemming if enabled.
     *
     * @param line String containing a document in the format: [doc_id]\t[text]\n
     * @return ParsedDocument object containing the document ID and tokenized text
     */
    public static DocumentAfterPreprocessing processDocument(String line, long docId) {
        // Utility variables to keep the current doc ID and text
        String docno;
        String text;

        // Divide the line using \t as delimiter to separate doc ID and text
        StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");

        // Retrieve the first token, which is the doc ID
        if (stringTokenizer.hasMoreTokens()) {
            docno = stringTokenizer.nextToken();

            // Retrieve the second token, which is the text and convert it to lowercase
            if (stringTokenizer.hasMoreTokens()) {
                text = stringTokenizer.nextToken().toLowerCase();
            } else {
                // If no text is available, return null
                return null;
            }
        } else {
            // If the line is empty, return null
            return null;
        }

        // Remove punctuation and split text by whitespace
        String[] splittedText = removePunctuation(text).split("\\s+");

        // Check if stopwords removal and stemming are enabled
        if (ENABLE_STEMMING_AND_STOPWORD_REMOVAL) {
            // Remove stopwords from the tokenized text
            splittedText = removeStopWords(splittedText, stopWords);

            // Apply stemming to the remaining tokens
            splittedText = getStems(splittedText);
        }

        // Return a new ParsedDocument containing the doc ID and tokenized text
        return new DocumentAfterPreprocessing(docId, docno, splittedText);
    }

    /**
     * Remove punctuation from the text by replacing it with an empty string.
     *
     * @param text String containing the text to be cleaned
     * @return Cleaned text without punctuation
     */
    public static String removePunctuation(String text) {
        // Replace all punctuation marks with a whitespace character, then trim the string to remove leading/trailing whitespaces
        return text.replaceAll("[^\\w\\s]", " ").trim();
    }

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

    /**
     * Remove the specified stopwords from the tokenized text.
     *
     * @param text Array of tokens from the text
     * @param stopwords List of stopwords to be removed
     * @return Array of tokens without the stopwords
     */

    public static String[] removeStopWords(String[] text, List<String> stopwords) {
        // Use streams for efficient performance in removing stopwords
        ArrayList<String> words = Stream.of(text)
                .collect(Collectors.toCollection(ArrayList<String>::new));
        words.removeAll(stopwords);
        return words.toArray(new String[0]);
    }

    /**
     * Apply the Porter Stemmer to stem each token in the text.
     *
     * @param terms Array of tokens to be stemmed
     * @return Array of stemmed tokens
     */
    public static String[] getStems(String[] terms) {
        // Instance of the Porter Stemmer
        PorterStemmer porterStemmer = new PorterStemmer();

        // Stem each token and collect the results into an array
        return Stream.of(terms)
                .map(porterStemmer::stem)
                .toArray(String[]::new);
    }

    /**
     * Load the stopwords from the specified file.
     *
     * @return List of stopwords loaded from the file
     */
    private static List<String> loadStopWords() {
        System.out.println("[DocumentPreProcessor] Loading stop words...");
        try {
            // Read stopwords from the file and return as a list
            return Files.readAllLines(Paths.get(STOPWORDS_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load stop words", e);
        }
    }
}
