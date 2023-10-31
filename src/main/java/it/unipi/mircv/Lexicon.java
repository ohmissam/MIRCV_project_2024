package it.unipi.mircv;

import java.util.HashMap;
import java.util.Map;

public class Lexicon {

    private Map<String, LexiconEntry> dictionary;

    //costruttore
    public Lexicon() {
        this.dictionary = new HashMap<>();
    }

    public Boolean addTerm(String term){
        if(!this.dictionary.containsKey(term)) {
            this.dictionary.put(term, new LexiconEntry(0, 0, 0));
            return true;
        }
        return false;
    }

}