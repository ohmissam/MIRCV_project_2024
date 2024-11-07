package it.unipi.dii.aide.mircv.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Lexicon {




    /*String -> term*/
    private  HashMap<String, LexiconEntry> lexicon;

    public  HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public Lexicon() {
        lexicon = new HashMap<>();
    }

    //public Lexicon(HashMap<String, LexiconEntry> lexicon) {
    //    this.lexicon = lexicon;
    //}

    //public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
    //    this.lexicon = lexicon;
    //}
    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }

    @Override
    public String toString() {
        return "Lexicon{" +
                "lexicon=" + lexicon +
                '}';
    }
}