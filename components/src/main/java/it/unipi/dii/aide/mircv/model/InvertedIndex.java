package it.unipi.dii.aide.mircv.model;

import java.util.*;

public class InvertedIndex {

    private HashMap<String, PostingList> invertedIndex;

    public InvertedIndex(){
        invertedIndex = new HashMap<>();
    }

    public HashMap<String, PostingList> getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(HashMap<String, PostingList> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, PostingList> entry : invertedIndex.entrySet()) {
            String term = entry.getKey();
            PostingList postingList = entry.getValue();

            result.append(term).append("    ");
            result.append(postingList.toString()).append("\n");
        }

        return result.toString();
    }


    // Method to clear the inverted index
    public void clear() {
        invertedIndex.clear();  // Clears the internal map
    }
}
