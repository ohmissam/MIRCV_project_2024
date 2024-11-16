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

    public Lexicon() {
        lexicon = new HashMap<>();
    }

    //public Lexicon(HashMap<String, LexiconEntry> lexicon) {
    //    this.lexicon = lexicon;
    //}

    //public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
    //    this.lexicon = lexicon;
    //}
    public  HashMap<String, LexiconEntry> getLexicon() {
        return lexicon;
    }

    public void setLexicon(HashMap<String, LexiconEntry> lexicon) {
        this.lexicon = lexicon;
    }

    // Method to clear the lexicon
    public void clear() {
        lexicon.clear();  // Clears the internal map
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lexicon Details:\n");
        sb.append("Total terms: ").append(lexicon.size()).append("\n");

        int previewLimit = 10; // Limit the number of entries to preview
        int count = 0;

        for (Map.Entry<String, LexiconEntry> entry : lexicon.entrySet()) {
            if (count >= previewLimit) {
                sb.append("... and ").append(lexicon.size() - previewLimit).append(" more entries.\n");
                break;
            }
            sb.append("  Term: ").append(entry.getKey())
                    .append(" -> ").append(entry.getValue()).append("\n");
            count++;
        }

        return sb.toString();
    }

}