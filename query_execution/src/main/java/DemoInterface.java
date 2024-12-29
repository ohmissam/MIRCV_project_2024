import java.util.*;

import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.mircv.scorer.ScorerConjunctive;
import it.unipi.dii.aide.mircv.model.*;
import utils.Tuple;

public class DemoInterface {

    private static Lexicon lexicon = new Lexicon();
    private static DocumentIndex documentIndex = new DocumentIndex();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Welcome to the Demo Interface ---");
        System.out.println("Loading the lexicon in memory...");
        lexicon.loadLexicon();

        if (Config.IS_DEBUG_MODE) {
            System.out.println("[DEBUG] Lexicon size: " + lexicon.getLexicon().size());
        }
        System.out.println("Loading the document index in memory...");
        DocumentIndex documentIndex = new DocumentIndex();
        documentIndex.loadDocumentIndex();
        if (Config.IS_DEBUG_MODE) {
            System.out.println("[DEBUG] Document index size: " + documentIndex.getDocumentIndex().size());
        }
        System.out.println("Data structures loaded in memory.");

        while (true) {
            // Prompt user to enter a query
            System.out.println("\nEnter a query (or type 'exit' to quit):");
            String queryInput = scanner.nextLine();

            // Check if the input is null or empty
            if (queryInput == null || queryInput.trim().isEmpty()) {
                System.out.println("Invalid query. Please try again.");
                continue;
            }

            // Exit condition
            if (queryInput.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            System.out.println("Your query is: " + queryInput);
            String[] queryTerms = parseQuery(queryInput);

            // Check if there are terms in the lexicon
            if (queryTerms.length == 0) {
                System.out.println("No document found for the query. Please try another query.");
                continue;
            }
            System.out.println("Parsed Query: " + Arrays.toString(queryTerms));

            PostingList[] postingLists = loadPostingLists(queryTerms);

            // Perform scoring
            ArrayList<Tuple<Long, Double>> results = ScorerConjunctive.scoreCollectionConjunctive(postingLists, documentIndex);

            // Display results
            System.out.println("\n--- Scoring Results ---");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ") Document ID: " + results.get(i).getFirst() + ", Score: " + results.get(i).getSecond());
            }

            // Close resources
            for (PostingList postingList : postingLists) {
                if (postingList != null) {
                    postingList.closeList();
                }
            }
        }

        scanner.close();
        System.out.println("--- End ---");
    }

    private static PostingList[] loadPostingLists(String[] queryTerms) {
        PostingList[] postingLists = new PostingList[queryTerms.length];

        for (int i = 0; i < queryTerms.length; i++) {
            // Instantiate the posting for the i-th query term
            postingLists[i] = new PostingList();
            // Load in memory the posting list of the i-th query term
            postingLists[i].openList(lexicon.getLexicon().get(queryTerms[i]));
        }
        return postingLists;
    }

    /**
     * Parse a query string into an array of terms. The query is processed using tokenization,
     * stopword removal, and stemming if these steps are enabled in the pre-processing pipeline.
     *
     * @param query The input query string to be parsed.
     * @return An array of terms extracted from the query after processing.
     */
    public static String[] parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query input cannot be null or empty.");
        }
        // Divide the line using \t as delimiter to separate text
        StringTokenizer stringTokenizer = new StringTokenizer(query, "\t");
        String text = null;

        // Retrieve the first token, which is the doc ID
        if (stringTokenizer.hasMoreTokens()) {
            text = stringTokenizer.nextToken().toLowerCase();
        }

        // Remove punctuation and split text by whitespace
        String[] splittedText = DocumentPreProcessor.removePunctuation(text).split("\\s+");

        // Check if stopwords removal and stemming are enabled
        if (Config.ENABLE_STEMMING_AND_STOPWORD_REMOVAL) {
            // Remove stopwords from the tokenized text
            splittedText = DocumentPreProcessor.removeStopWords(splittedText, DocumentPreProcessor.getStopWords());

            // Apply stemming to the remaining tokens
            splittedText = DocumentPreProcessor.getStems(splittedText);
        }

        ArrayList<String> results = new ArrayList<>();

        // Remove the query terms that are not present in the lexicon
        for (String term : splittedText) {
            if (lexicon.getLexicon().get(term) != null) {
                results.add(term);
            }
        }
        return results.toArray(new String[0]);
    }
}
