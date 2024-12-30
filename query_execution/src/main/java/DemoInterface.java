import java.util.*;

import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.mircv.scorer.ScorerConjunctiveAndDisjunctive;
import it.unipi.dii.aide.mircv.model.*;
import utils.ScorerConfig;
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
        documentIndex.loadDocumentIndex();
        if (Config.IS_DEBUG_MODE) {
            System.out.println("[DEBUG] Document index size: " + documentIndex.getDocumentIndex().size());
        }
        System.out.println("Data structures loaded in memory.");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Search Query");
            System.out.println("2. Exit");
            System.out.println("3. Settings");
            System.out.print("Select an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    searchQuery(scanner);
                    break;
                case "2":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                case "3":
                    settingsMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void searchQuery(Scanner scanner) {
        System.out.println("Enter a query:");
        String queryInput = scanner.nextLine();

        if (queryInput == null || queryInput.trim().isEmpty()) {
            System.out.println("Invalid query. Please try again.");
            return;
        }

        System.out.println("Your query is: " + queryInput);
        String[] queryTerms = parseQuery(queryInput);

        if (queryTerms.length == 0) {
            System.out.println("No document found for the query. Please try another query.");
            return;
        }

        if (ScorerConfig.IS_DEBUG_MODE) {
            System.out.println("[DEBUG] Parsed Query: " + Arrays.toString(queryTerms));
        }

        PostingList[] postingLists = loadPostingLists(queryTerms);
        ArrayList<Tuple<Long, Double>> results;

        if(ScorerConfig.USE_CONJUNCTIVE_SCORER){
            results = ScorerConjunctiveAndDisjunctive.scoreCollectionConjunctive(postingLists, documentIndex);
        }
        else{
            results = ScorerConjunctiveAndDisjunctive.scoreCollectionDisjunctive(postingLists, documentIndex);
        }


        System.out.println("\n--- Scoring Results ---");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ") Document ID: " + results.get(i).getFirst() + ", Score: " + results.get(i).getSecond());
        }

        for (PostingList postingList : postingLists) {
            if (postingList != null) {
                postingList.closeList();
            }
        }
    }

    private static void settingsMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Settings Menu ---");
            System.out.println("1. Toggle USE_BM25 (Current: " + ScorerConfig.USE_BM25 + ")");
            System.out.println("2. Toggle IS_DEBUG_MODE (Current: " + ScorerConfig.IS_DEBUG_MODE + ")");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    ScorerConfig.setUseBm25(!ScorerConfig.USE_BM25);
                    System.out.println("USE_BM25 is now set to: " + ScorerConfig.USE_BM25);
                    break;
                case "2":
                    ScorerConfig.setDebugMode(!ScorerConfig.IS_DEBUG_MODE);
                    System.out.println("IS_DEBUG_MODE is now set to: " + ScorerConfig.IS_DEBUG_MODE);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static PostingList[] loadPostingLists(String[] queryTerms) {
        PostingList[] postingLists = new PostingList[queryTerms.length];

        for (int i = 0; i < queryTerms.length; i++) {
            postingLists[i] = new PostingList();
            postingLists[i].openList(lexicon.getLexicon().get(queryTerms[i]));
        }
        return postingLists;
    }

    public static String[] parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query input cannot be null or empty.");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(query, "\t");
        String text = null;

        if (stringTokenizer.hasMoreTokens()) {
            text = stringTokenizer.nextToken().toLowerCase();
        }

        String[] splittedText = DocumentPreProcessor.removePunctuation(text).split("\\s+");

        if (Config.ENABLE_STEMMING_AND_STOPWORD_REMOVAL) {
            splittedText = DocumentPreProcessor.removeStopWords(splittedText, DocumentPreProcessor.getStopWords());
            splittedText = DocumentPreProcessor.getStems(splittedText);
        }

        ArrayList<String> results = new ArrayList<>();
        for (String term : splittedText) {
            if (lexicon.getLexicon().get(term) != null) {
                results.add(term);
            }
        }
        return results.toArray(new String[0]);
    }
}
