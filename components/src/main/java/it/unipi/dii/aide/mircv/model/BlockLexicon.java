package it.unipi.dii.aide.mircv.model;

import java.util.HashMap;
import java.util.Map;

public class BlockLexicon {

    /*String -> term*/
    private  HashMap<String, BlockLexiconEntry> lexicon;

    public BlockLexicon() {
        lexicon = new HashMap<>();
    }

    public  HashMap<String, BlockLexiconEntry> getLexicon() {
        return lexicon;
    }

    public void setLexicon(HashMap<String, BlockLexiconEntry> lexicon) {
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

        int previewLimit = 100; // Limit the number of entries to preview
        int count = 0;

        for (Map.Entry<String, BlockLexiconEntry> entry : lexicon.entrySet()) {
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