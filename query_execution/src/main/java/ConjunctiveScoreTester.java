import java.util.*;

import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.mircv.scorer.ScorerConjunctive;
import it.unipi.dii.aide.mircv.model.*;
import it.unipi.dii.aide.mircv.model.Tuple;

public class ConjunctiveScoreTester {

    private static MergedLexicon lexicon = new MergedLexicon();
    private static DocumentIndex documentIndex = new DocumentIndex();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Welcome to the ScorerConjunctive Testing Interface ---");
        System.out.println("Loading the lexicon in memory...");
        lexicon.loadMergedLexicon();

        if(Config.IS_DEBUG_MODE){
            System.out.println("[DEBUG] Lexicon size: " + lexicon.getLexicon().size());
        }
        System.out.println("Loading the document index in memory...");
        DocumentIndex documentIndex = new DocumentIndex();
        documentIndex.loadDocumentIndex();
        if(Config.IS_DEBUG_MODE){
            System.out.println("[DEBUG] Document index size: " + documentIndex.getDocumentIndex().size());
        }
        System.out.println("Data structures loaded in memory.");

        // Prompt user to enter a query
        System.out.println("Enter a query (separate terms with commas):");
        String queryInput = scanner.nextLine();
        String[] queryTerms = queryInput.split(",");

        // Parse query terms
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

        scanner.close();
        System.out.println("--- End of Testing ---");
    }



    private static PostingList[] loadPostingLists(String[] queryTerms) {
        PostingList[] postingLists = new PostingList[queryTerms.length];

        for (int i = 0; i < queryTerms.length; i++) {
            //Instantiate the posting for the i-th query term
            postingLists[i] = new PostingList();

            //Load in memory the posting list of the i-th query term
            postingLists[i].openList(lexicon.getLexicon().get(queryTerms[i]));

        }

        return postingLists;
    }
}
