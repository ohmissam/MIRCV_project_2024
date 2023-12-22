package org.example;

import it.unipi.dii.aide.mircv.model.Lexicon;
import it.unipi.dii.aide.mircv.model.LexiconEntry;
import it.unipi.dii.aide.mircv.model.PostingList;
import it.unipi.dii.aide.mircv.preProcessing.PreProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

public class DAAT {

    public static PriorityQueue<Map.Entry<Double, Integer>> scoreQuery(String query, boolean isConjuctive, int k, String scoringFunction) throws IOException {

        // text cleaning
        String queryCleaned=PreProcessing.textCleaning(query);

        // tokenize
        String[] tokens = PreProcessing.tokenize(queryCleaned);

      //  if(Flags.isStemStopRemovalEnabled()) {
            // remove stopwords
        tokens = PreProcessing.removeStopwords(tokens);

            // perform stemming
            //getStems(tokens);
        //}

    }
    public static double scoreDocument(String docid, ArrayList<PostingList> postingsToScore, String scoringFunction, Lexicon lexicon){

        // initialization of document's score
        double docScore = 0;

        // find postings about the docid to be processed
        for(PostingList postingList : postingsToScore)
        {
            // check if the current postinglist is pointing to the docid we are currently processing

            int postingToScore = postingList.getTermFrequency(docid);
            Map<String, LexiconEntry> dictionary = lexicon.getDictionary();
            LexiconEntry lexiconEntry = dictionary.get(postingList);
            docScore += Scorer.computeTFIDF(postingToScore,lexiconEntry.getInverseDocumentFrequency());


        }
        return docScore;
    }
}
