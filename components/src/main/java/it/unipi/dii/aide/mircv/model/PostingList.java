package it.unipi.dii.aide.mircv.model;

import java.util.*;

public class PostingList {
    //String->docId
    private HashMap<String, Integer> postingList = new HashMap<>();

    public HashMap<String, Integer> getPostingList() {
        return postingList;
    }

    public void setPostingList(HashMap<String, Integer> postingList) {
        this.postingList = postingList;
    }

    public PostingList(String docId, Integer termFrequency) {
        postingList.put(docId, termFrequency);
    }

    public int getTermFrequency(String docId) {
        return postingList.get(docId);
    }

    public void setTermFrequency(String docId) {
        postingList.put(docId, postingList.get(docId)+1); // set tf += 1
    }

    public void addPosting(String docId){
        postingList.put(docId, 1);
    }

    public boolean containsKey(String docId){
        return postingList.containsKey(docId);
    }

    public int length(){
        return postingList.size();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        int listSize = postingList.entrySet().size();
        int count = 0;
        for (Map.Entry<String, Integer> posting : postingList.entrySet()) {
            count++;

            String docId = posting.getKey();
            Integer termFrequency = posting.getValue();

            result.append(docId).append(":");
            result.append(termFrequency);

            if(count != listSize)
                result.append(", "); //se è l'ultimo non fare append di ","
        }

        return result.toString();
    }

}
