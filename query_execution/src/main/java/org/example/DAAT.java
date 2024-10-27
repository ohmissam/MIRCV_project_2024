package org.example;

import it.unipi.dii.aide.mircv.model.Lexicon;
import it.unipi.dii.aide.mircv.model.LexiconEntry;
import it.unipi.dii.aide.mircv.model.PostingList;
import it.unipi.dii.aide.mircv.preProcessing.PreProcessing;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class DAAT {

    public static PriorityQueue<Map.Entry<Double, Integer>> scoreQuery(String query, boolean isConjuctive, int k, String scoringFunction) throws IOException {

        // text cleaning
        String queryCleaned=PreProcessing.textCleaning(query);

        // tokenize
        String[] tokens = PreProcessing.tokenize(queryCleaned);

      //  if(Flags.isStemStopRemovalEnabled()) {
            // remove stopwords
//            tokens = PreProcessing.removeStopwords(tokens);  //DA RIMETTERE

                // perform stemming
            String[] list = getStems(tokens);
        //}
        return null;

    }
    public static double scoreDocument(Long docid, ArrayList<PostingList> postingsToScore, String scoringFunction, Lexicon lexicon){

        // initialization of document's score
        double docScore = 0;

        // find postings about the docid to be processed
        for(PostingList postingList : postingsToScore)
        {
            // check if the current postinglist is pointing to the docid we are currently processing

            int postingToScore = postingList.getTermFrequency(docid);
            Map<String, LexiconEntry> dictionary = lexicon.getLexicon();
            LexiconEntry lexiconEntry = dictionary.get(postingList);
            docScore += Scorer.computeTFIDF(postingToScore,lexiconEntry.getInverseDocumentFrequency());


        }
        return docScore;
    }

    private static String[] getStems(String[] terms){

        //Instance of a porter stemmer
        PorterStemmer porterStemmer = new PorterStemmer();

        //Create an array list of stems by computing different phases from a stream of tokens:
        //  The stream is obtained by splitting the text using the whitespace as delimiter;
        //  It's used a map stage where each word is stemmed
        //  The overall result is collected into an Array of strings
        return Stream.of(terms)
                .map(porterStemmer::stem).toArray(String[]::new);
    }
}
