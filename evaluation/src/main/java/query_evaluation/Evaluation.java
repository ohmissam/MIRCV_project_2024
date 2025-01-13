package query_evaluation;

import it.unipi.dii.aide.mircv.preProcessing.DocumentPreProcessor;
import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.mircv.scorer.ScorerConjunctiveAndDisjunctive;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import utils.ScorerConfig;
import it.unipi.dii.aide.mircv.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main class for the query evaluation module, provide the methods to load a batch of queries used to generate a file
 * containing the results of the queries in a format that can be used to compute the metrics using the trec_eval tool.
 */
public class Evaluation{

    private static final Lexicon lexicon = new Lexicon();
    private static final DocumentIndex documentIndex = new DocumentIndex();



    public static void main( String[] args )
    {
        System.out.println("[EVALUATION] Starting evaluation!");

        System.out.println("[QUERY PROCESSOR] Loading the lexicon in memory...");
        lexicon.loadLexicon();

        System.out.println("[QUERY PROCESSOR] Loading the document index in memory...");
        documentIndex.loadDocumentIndex();

        //disj + bm25
        ScorerConfig.setUseConjunctiveScorer(false);
        ScorerConfig.setUseBm25(true);
        System.out.println("[EVALUATION] Disjunctive + BM25");

        evaluateQueries(getQueries(), documentIndex, lexicon, 0);

        //conj + bm25
        ScorerConfig.setUseConjunctiveScorer(true);
        ScorerConfig.setUseBm25(true);
        System.out.println("[EVALUATION] Conjunctive + BM25");

        evaluateQueries(getQueries(), documentIndex, lexicon, 1);

        //conj + tfidf
        ScorerConfig.setUseConjunctiveScorer(true);
        ScorerConfig.setUseBm25(false);
        System.out.println("[EVALUATION] Conjunctive + tfidf");

        evaluateQueries(getQueries(), documentIndex, lexicon, 2);

        //disj + tfidf
        ScorerConfig.setUseConjunctiveScorer(false);
        ScorerConfig.setUseBm25(false);
        System.out.println("[EVALUATION] Disjunctive + tfidf");

        evaluateQueries(getQueries(), documentIndex, lexicon, 3);
    }

    /**
     * Read from a file a list of queries in the format of qid\tquery and return an array of tuple containing the
     * qid and the query: (qid, query)
     * @return an ArrayList of tuple containing the qid and the query: (qid, query)
     */
    private static ArrayList<Tuple<Long, String>> getQueries(){

        //Path of the collection to be read
        File file = new File(EvaluationConfig.QUERY_PATH);

        //Try to open the collection provided
        try (FileInputStream fileInputStream = new FileInputStream(file)){

            //Read the uncompressed tar file specifying UTF-8 as encoding
            InputStreamReader inputStreamReader = new InputStreamReader(new GzipCompressorInputStream(fileInputStream), StandardCharsets.UTF_8);

            //Create a BufferedReader in order to access one line of the file at a time
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //Variable to keep the current line read from the buffer
            String line;

            //Array list for the results
            ArrayList<Tuple<Long, String>> results = new ArrayList<>();

            //Iterate over the lines
            while ((line = bufferedReader.readLine()) != null ) {

                //Split the line qid\tquery in qid query
                String[] split = line.split("\t");

                //Add it to the results array
                if(split[0] != null && split[1] != null) {
                    results.add(new Tuple<>(Long.parseLong(split[0]), split[1]));
                }
            }

            return results;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs all the queries in the array of queries, using the configuration parameters passed, for the scoring it
     * requires the document index and the lexicon.
     * @param queries array of tuples containing the query id and the query string (queryId, query)
     * @param documentIndex document index containing the document info
     * @param lexicon lexicon containing the terms information
     */
    private static void evaluateQueries(ArrayList<Tuple<Long,String>> queries, DocumentIndex documentIndex, Lexicon lexicon, int k){
        Config.setIsDebugMode(false);
        ScorerConfig.setDebugMode(false);

        //Object used to build the lexicon line into a string
        StringBuilder stringBuilder;

        //Buffered writer used to format the output
        BufferedWriter bufferedWriter;

        try {
            String fileName = EvaluationConfig.RESULTS_PATH;
            if(k == 0){
                fileName+= "_CS_disj_bm25.txt";
            }
            else if(k == 1){
                fileName+= "_CS_conj_bm25.txt";
            }
            else if(k == 2){
                fileName+= "_CS_conj_tfidf.txt";
            }
            else{
                fileName+= "_CS_disj_tfidf.txt";
            }

            bufferedWriter = new BufferedWriter(new FileWriter(fileName,false));

            double completionTimeTot = 0.0;
            int numberOfQueries = 0;

            for( Tuple<Long,String> tuple : queries ){

                //Read the next query, add -1 to indicate that it is a query
                String query = "-1\t" + tuple.getSecond();

                //Parse the query
                String[] queryTerms = parseQuery(query);

                if (queryTerms.length == 0) {
                    System.out.println("No document found for the query. Please try another query.");
                    continue;
                }



                //Remove the duplicates
                queryTerms = Arrays.stream(queryTerms).distinct().toArray(String[]::new);

//                System.out.println("Query: " + query + "\t" + "Terms: " + Arrays.toString(queryTerms));

                //Load the posting list of the terms of the query
                PostingList[] postingLists = new PostingList[queryTerms.length];

                //For each term in the query terms array
                for (int i = 0; i < queryTerms.length; i++) {
                    if (!lexicon.getLexicon().containsKey(queryTerms[i])) {
                        System.err.println("Term not found in lexicon: " + queryTerms[i]);
                        continue;
                    }

                    //Instantiate the posting for the i-th query term
                    postingLists[i] = new PostingList();

                    //Load in memory the posting list of the i-th query term
                    LexiconEntry lexiconEntry =lexicon.getLexicon().get(queryTerms[i]);
                    postingLists[i].openList(lexiconEntry);
                }

                //Array to hold the results of the query
                ArrayList<Tuple<Long, Double>> result;


                //Retrieve the time at the beginning of the computation
                long begin = System.currentTimeMillis();


                //Score all the queries
                if(ScorerConfig.USE_CONJUNCTIVE_SCORER){
                    result = ScorerConjunctiveAndDisjunctive.scoreCollectionConjunctive(postingLists,documentIndex);
                }else {
                    result = ScorerConjunctiveAndDisjunctive.scoreCollectionDisjunctive(postingLists,documentIndex);
                }

                completionTimeTot += (System.currentTimeMillis() - begin);
                numberOfQueries++;

                //Write the results in a format valid for the TREC_EVAL tool
                for(int i = 0; i < result.size(); i++){

                    //New string builder for the current result
                    stringBuilder = new StringBuilder();

                    //build the string
                    stringBuilder
                            .append(tuple.getFirst()).append(" ")
                            .append("q0 ")
                            .append(documentIndex.getDoc(result.get(i).getFirst()).getDocNo()).append(" ")
                            .append(i+1).append(" ")
                            .append(result.get(i).getSecond()).append(" ")
                            .append("runid1").append("\n");

                    //Write the string in the file
                    bufferedWriter.write(stringBuilder.toString());

                }

                //Close the posting lists
                for (PostingList postingList : postingLists) {
                    postingList.closeList();
                }

            }

            System.out.println("Average completion time: " + completionTimeTot/numberOfQueries + "ms");
            System.out.println("Number of queries: " + numberOfQueries);

            //Close the writer
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Parses the query and returns the list of terms containing the query, the parsing process must be the same as the
     * one used during the indexing phase.
     * @param query the query string to parse
     * @return the array of terms after the parsing of the query
     */
    public static String[] parseQuery(String query) {

        //Array of terms to build the result
        ArrayList<String> results = new ArrayList<>();

        //Parse the query using the same configuration of the indexer
        DocumentAfterPreprocessing doc = DocumentPreProcessor.processDocument(query,0);

        //If no terms are returned by the parser then return null
        if(doc == null){
            return null;
        }

        //Remove the query terms that are not present in the lexicon
        for(String term : doc.getTerms()){
            if(lexicon.getLexicon().get(term) != null){
                results.add(term);
            }
        }

        //Return an array of String containing the results of the parsing process
        return results.toArray(new String[0]);
    }


}
