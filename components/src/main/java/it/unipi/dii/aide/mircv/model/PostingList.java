package it.unipi.dii.aide.mircv.model;

import java.util.*;

public class PostingList {
    //String->docId
    private ArrayList<Posting> postingList = new ArrayList<>();

    public PostingList(Posting posting) {
        this.postingList.add(posting);
    }

    public PostingList() {
    }

    public ArrayList<Posting> getPostingList() {
        return postingList;
    }

    public void setPostingList(ArrayList<Posting> postingList) {
        this.postingList = postingList;
    }

    @Override
    public String toString() {
        return "PostingList{" +
                "postingList=" + postingList +
                '}';
    }
//    private HashMap<Long, Integer> postingList = new HashMap<>();
//
//    public PostingList(Long docId, Integer termFrequency) {
//        postingList.put(docId, termFrequency);
//    }
//
//    // Returns the entire posting list as a HashMap
//    public HashMap<Long, Integer> getPostingList() {
//        return postingList;
//    }
//
//    // Sets the posting list with a new HashMap of docIds and term frequencies
//    public void setPostingList(HashMap<Long, Integer> postingList) {
//        this.postingList = postingList;
//    }
//
//    // Retrieves the term frequency for a specific docId
//    public int getTermFrequency(Long docId) {  return postingList.get(docId);
//    }
//
//    // Increments the term frequency for a given docId by 1
//    public void incrementTermFrequency(Long docId) {
//        postingList.put(docId, postingList.get(docId)+1); // set tf += 1
//    }
//
//    // Returns the number of entries in the posting list
//    public int length(){
//        return postingList.size();
//    }
//
//
//    public String toString() {
//        StringBuilder result = new StringBuilder();
//
//        int listSize = postingList.entrySet().size();
//        int count = 0;
//        for (Map.Entry<Long, Integer> posting : postingList.entrySet()) {
//            count++;
//
//            Long docId = posting.getKey();
//            Integer termFrequency = posting.getValue();
//
//            result.append(docId).append(":");
//            result.append(termFrequency);
//
//            if(count != listSize)
//                result.append(", "); //se è l'ultimo non fare append di ","
//        }
//
//        return result.toString();
//    }

}
