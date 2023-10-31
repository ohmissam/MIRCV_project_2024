package it.unipi.mircv;

import java.util.*;

public class PostingList {
    private HashMap<Integer, Integer> postingList;

    public PostingList(int docId) {
        this.postingList = new HashMap<>();
        this.postingList.put(docId,1);     //alla prima occorrenza del termine uso costruzione con freq = 1

    }

    public int getTermFrequency(int docId) {
        return postingList.get(docId);
    }

    public void setTermFrequency(int docId) {
        postingList.put(docId, postingList.get(docId)+1); // setta TF += 1
    }
}
