package it.unipi.dii.aide.mircv;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Lexicon {

    private static Map<String, LexiconEntry> dictionary;

    public Lexicon() {
        dictionary = new HashMap<>();
    }

    /*
    IF token already exists in the dictionary then increase the term frequency
        BUT NOT document frequency (done after while-loop in preProcessing)
    ELSE add the term to the lexicon
    */
    public void addTerm(String term) {
        if(dictionary.containsKey(term)) {
            LexiconEntry entry = dictionary.get(term);
            int tf = entry.getTermFrequency();
            entry.setTermFrequency(tf + 1);
        }
        else {
            dictionary.put(term, new LexiconEntry(1, 0));
        }
    }

    public Set<String> getLexiconTerms(){
        return dictionary.keySet();
    }

    public void setDocumentFrequency(InvertedIndex invertedIndex, String term){
        LexiconEntry entry = dictionary.get(term);
        entry.setDocumentFrequency(invertedIndex.getPostingListLength(term));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, LexiconEntry> entry : dictionary.entrySet()) {
            String term = entry.getKey();
            LexiconEntry lexiconEntry = entry.getValue();

            result.append(term).append("    ");
            result.append(lexiconEntry.getTermFrequency()).append(", ");
            result.append(lexiconEntry.getDocumentFrequency()).append(", ");
            result.append(lexiconEntry.getInverseDocumentFrequency()).append("\n");
        }

        return result.toString();
    }

}