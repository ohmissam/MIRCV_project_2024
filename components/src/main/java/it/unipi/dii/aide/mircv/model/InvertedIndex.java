package it.unipi.dii.aide.mircv.model;

import java.util.*;

public class InvertedIndex {

    private HashMap<String, PostingList> invertedIndex;

    public InvertedIndex(){
        invertedIndex = new HashMap<>();
    }

    /*
    IF there are still no postings for "term" then is created the one related to the "docId",
    ELSE IF the posting related to the "docId" (defined in preProcessing) exists
        then just increase the termFrequency (in that doc)
        ELSE, i.e., the posting with the current doc doesn't exist (means we are in a new doc)
            then we add a new posting (with tf =1)
    */
    // vedere se è effettivamente necessaria
    public void setPosting(String term, Long docId) {
        PostingList postingList = invertedIndex.get(term);
        if(postingList == null){
            invertedIndex.put(term, new PostingList(docId, 1));
        }
        else if(postingList.getPostingList().containsKey(docId)) {
            postingList.incrementTermFrequency(docId);
        }
        else {
            postingList.getPostingList().put(docId,1); // l'1 è a caso
        }
    }

    //public boolean containsKey(String term) {
    //    return invertedIndex.containsKey(term);
    //}

    public HashMap<String, PostingList> getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(HashMap<String, PostingList> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public int getPostingListLength(String term){
        PostingList postingList = invertedIndex.get(term);
        return postingList.length(); // fatto length-1 perchè la prima volta che incontriamo il term in preProcess faccio frequenza = 1 in setPosting()
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
