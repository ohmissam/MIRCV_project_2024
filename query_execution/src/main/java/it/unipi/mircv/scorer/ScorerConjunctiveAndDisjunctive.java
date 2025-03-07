package it.unipi.mircv.scorer;
import it.unipi.dii.aide.mircv.model.*;
import static utils.ScorerConfig.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ScorerConjunctiveAndDisjunctive {


    public static ArrayList<Tuple<Long,Double>> scoreCollectionConjunctive(PostingList[] postingLists, DocumentIndex documentIndex) {
        if(IS_DEBUG_MODE){
            System.out.println("[DEBUG] Initial posting lists: \n"+Arrays.toString(postingLists));
        }
        //Priority queue to store the document id and its score, based on the priority of the document
        RankedDocs rankedDocs = new RankedDocs(BEST_K_VALUE);
        ArrayList<Integer> essential = new ArrayList<>();

        ArrayList<PostingList> orderedPostingLists = new ArrayList<>();

        //Retrieve the time at the beginning of the computation
        long begin = System.currentTimeMillis();

        // Move the iterators of each posting list to the first position
        for (PostingList postingList : postingLists) {

            if (postingList.hasNext()) {
                postingList.next();
                orderedPostingLists.add(postingList);
            }
        }

        //sort the posting list in ascending order
        if(USE_BM25) {
            if(IS_DEBUG_MODE){
                System.out.println("[DEBUG] USE_BM25: "+USE_BM25);
            }
            orderedPostingLists.sort(Comparator.comparingInt(o -> o.getLexiconEntry().getBm25TermUpperBound()));
        }
        else{
            if(IS_DEBUG_MODE){
                System.out.println("[DEBUG] USE_BM25: "+USE_BM25);
            }
            orderedPostingLists.sort(Comparator.comparingInt(o -> o.getLexiconEntry().getTfidfTermUpperBound()));
        }
        if(IS_DEBUG_MODE){
            System.out.println("--------------------------------");
            System.out.println("[DEBUG] Ordered posting lists in essntial:");
        }

        for(int i = 0; i < orderedPostingLists.size(); i++){
            essential.add(i);
            postingLists[i] = orderedPostingLists.get(i);
            if(IS_DEBUG_MODE){
                System.out.println("[DEBUG] Postinglist["+i+"]: "+postingLists[i].toString());
                System.out.println("[DEBUG] " + postingLists[i].getLexiconEntry());
            }
        }

        //Tuple to store the current maximum document id and the list of posting lists containing it
        long maxDocid;

        //Support variables to accumulate over the iteration the score values
        double tf_tfidf;
        double tf_BM25;
        double score = 0;

        // Access each posting list in a Document-At-a-Time fashion until no more postings are available
        while (!aPostingListEnded(postingLists)) {
            // if essential is empty no more docs can enter the top K ranking
            if(essential.isEmpty()){
                break;
            }
            // Retrieve the maximum document id and the list of posting lists containing it
            maxDocid = maxDocid(postingLists);

            if(IS_DEBUG_MODE) {
                System.out.println("--------------------------------");
                System.out.println("[DEBUG] Searched DocId: " + maxDocid);
            }

            // Perform the nextGEQ operation for each posting list
            for (PostingList postingList : postingLists) {
                // If we reach the end of the posting list then we break the for, the conjunctive query is ended
                // and all the next conditions are not satisfied
                postingList.nextGEQ(maxDocid);
            }
            if (aPostingListEnded(postingLists)) {
                break;
            }

            //If the current doc id is equal in all the posting lists
            if (areAllEqual(postingLists)) {
                //Score the document
                int index = 0;
                for (PostingList postingList : postingLists) {

                    //Debug
                    if(IS_DEBUG_MODE) {
                        System.out.println("[DEBUG] Finded a docID present in all positng lists!");
                    }
                    long currentDocId = postingList.getPostingListIterator().getCurrentDocId();
                    int currentFrequency = postingList.getPostingListIterator().getCurrentFrequency();
                    double idf = postingList.getLexiconEntry().getInverseDocumentFrequency();

                    //If the scoring is BM25
                    if (USE_BM25) {
                        //Compute the BM25's tf for the current posting
                        tf_BM25 = currentFrequency / (K1 * ((1 - B) + B * ((double) documentIndex.getDoc(currentDocId).getDocLength() / STATISTICS.getAvdl()) + currentFrequency));

                        //Add the partial score to the accumulated score
                        score += tf_BM25 * idf;

                        if(IS_DEBUG_MODE){
                            System.out.println("[DEBUG] docID: " + maxDocid + " score: " + score);
                        }

                        double newMaxScore = score;
                        for(int j = index + 1; j < postingLists.length; j++){
                            newMaxScore += postingLists[j].getLexiconEntry().getBm25TermUpperBound();
                        }

                        if(newMaxScore < rankedDocs.getThreshold()){
                            if(IS_DEBUG_MODE) {
                                System.out.println("[DEBUG] New Max Score < rankedDocs.getThreshold: " + newMaxScore + "<" + rankedDocs.getThreshold() +  " docID " + maxDocid + " ruled out");
                            }
                            for(int j = index; j < postingLists.length; j++){
                                postingLists[j].next();
                            }
                            break;
                        }

                    }
                    else {
                        //Compute the TFIDF'S tf for the current posting
                        tf_tfidf = 1 + Math.log(currentFrequency) / Math.log(2);

                        //Add the partial score to the accumulated score
                        score += tf_tfidf * idf;

                        if(IS_DEBUG_MODE){
                            System.out.println("[DEBUG] tfidf docID " + maxDocid + ": " + score);
                        }

                        double newMaxScore = score;
                        for(int j = index + 1; j < postingLists.length; j++){
                            newMaxScore += postingLists[j].getLexiconEntry().getTfidfTermUpperBound();
                        }

                        if(newMaxScore < rankedDocs.getThreshold()){
                            if(IS_DEBUG_MODE) {
                                System.out.println("[DEBUG] New Max Score < rankedDocs.getThreshold: " + newMaxScore + "<" + rankedDocs.getThreshold() +  " docID " + maxDocid + " ruled out");
                            }
                            for(int j = index; j < postingLists.length; j++){
                                postingLists[j].next();
                            }
                            break;
                        }
                    }

                    //Move the cursor to the next posting
                    postingList.next();
                    maxDocid=maxDocid(postingLists);
                    index++;
                }

                //Since we have a document in all the posting lists then its score is relevant for the conjunctive query
                // it's value must be added to the priority queue, otherwise the score is not relevant, and we don't add it.
                //Add the score of the current document to the priority queue
                if(score > rankedDocs.getThreshold()) {
                    double old_threshold = rankedDocs.getThreshold();
                    rankedDocs.add(new Tuple<>(maxDocid, score));

                    //update the non-essential and the essential posting lists
                    if(rankedDocs.getThreshold() > 0){
                        updateEssentialPostingLists(essential, orderedPostingLists, rankedDocs.getThreshold());
                    }
                    if(IS_DEBUG_MODE && old_threshold!=rankedDocs.getThreshold()){
                        System.out.println("--------------------------------");
                        System.out.println("[DEBUG] Threshold changed! " + old_threshold + " -> " + rankedDocs.getThreshold());
                    }
                }
            }
            //clear the support variables for the next iteration
            score = 0;

        }

        //print the time used to score the documents, so to generate an answer for the query
        System.out.println("\n[SCORE DOCUMENT] Total scoring time: " + (System.currentTimeMillis() - begin) + "ms");

        //return the top K documents
        return getBestKDocuments(rankedDocs, BEST_K_VALUE);
    }


    public static ArrayList<Tuple<Long,Double>> scoreCollectionDisjunctive(PostingList[] postingLists, DocumentIndex documentIndex) {

        RankedDocs rankedDocs = new RankedDocs(BEST_K_VALUE);
        ArrayList<Integer> essential = new ArrayList<>();

        ArrayList<PostingList> orderedPostingLists = new ArrayList<>();

        //Retrieve the time at the beginning of the computation
        long begin = System.currentTimeMillis();

        //Move the iterators of each posting list to the first position
        for (PostingList postingList : postingLists) {
            if (postingList.hasNext()) {
                postingList.next();
                orderedPostingLists.add(postingList);
            }
        }

        //sort the posting list in ascending order
        if(USE_BM25) {
            orderedPostingLists.sort(Comparator.comparingInt(o -> o.getLexiconEntry().getBm25TermUpperBound()));
        }
        else{
            orderedPostingLists.sort(Comparator.comparingInt(o -> o.getLexiconEntry().getTfidfTermUpperBound()));
        }

        for(int i = 0; i < orderedPostingLists.size(); i++){
            essential.add(i);
            postingLists[i] = orderedPostingLists.get(i);
            if(IS_DEBUG_MODE){
                System.out.println("[DEBUG] Lexicon entry:\n" + postingLists[i].getLexiconEntry());
            }
        }


        //Tuple to store the current minimum document id and the list of posting lists containing it
        Tuple<Long,ArrayList<Integer>> minDocidTuple;

        //Support variables to accumulate over the iteration the score values
        double tf_tfidf;
        double tf_BM25;
        double score = 0;

        //Access each posting list in a Document-At-a-Time fashion until no more postings are available
        while (!allPostingListsEnded(postingLists)) {
            //if essential is empty no more docs can enter the top K ranking
            if(essential.isEmpty()){
                break;
            }

            //Retrieve the minimum document id and the list of posting lists containing it
            minDocidTuple = minDocid(postingLists);

            if(IS_DEBUG_MODE) {
                System.out.println("------------------");
                System.out.println("[DEBUG] Min docID: " + minDocidTuple.getFirst());
                System.out.println("[DEBUG] Blocks with minDocID: " + minDocidTuple.getSecond());
            }

            //check if some docs can enter the top K ranking
            if(!foundEssential(minDocidTuple.getSecond(), essential)){
                for(Integer index : minDocidTuple.getSecond()){
                    postingLists[index].next();
                }
                continue;
            }

            //For each index in the list of posting lists with min doc id
            for(int index = 0; index < minDocidTuple.getSecond().size(); index++){
                PostingList postList = postingLists[minDocidTuple.getSecond().get(index)];
                int currentFrequency = postList.getPostingListIterator().getCurrentFrequency();
                long currentDocId = postList.getPostingListIterator().getCurrentDocId();
                double idf = postList.getLexiconEntry().getInverseDocumentFrequency();

                //If the scoring is BM25
                if(USE_BM25){
                    //Compute the BM25's tf for the current posting
                    tf_BM25 = currentFrequency/ (K1 * ((1-B) +
                                    B * ((double)documentIndex.getDoc(currentDocId).getDocLength() / STATISTICS.getAvdl())
                                    + currentFrequency));

                    //Add the partial score to the accumulated score
                    score += tf_BM25*idf;

                    if(IS_DEBUG_MODE){
                        System.out.println("[DEBUG] docID: " + minDocidTuple.getFirst() + ": " + score);
                    }

                    double newMaxScore = score;
                    for(int j = index + 1; j < minDocidTuple.getSecond().size(); j++){
                        newMaxScore += postingLists[minDocidTuple.getSecond().get(j)].getLexiconEntry().getBm25TermUpperBound();
                    }

                    if(newMaxScore < rankedDocs.getThreshold()){
                        if(IS_DEBUG_MODE) {
                            System.out.println("[DEBUG] New Max Score < rankedDocs.getThreshold: " + newMaxScore + "<" + rankedDocs.getThreshold() +  " docID " + minDocidTuple.getFirst() + " ruled out");
                        }
                        for(int j = index; j < minDocidTuple.getSecond().size(); j++){
                            postingLists[minDocidTuple.getSecond().get(j)].next();
                        }
                        break;
                    }
                }else {
                    //Compute the TFIDF'S tf for the current posting
                    tf_tfidf = 1 + Math.log(currentFrequency) / Math.log(2);

                    //Add the partial score to the accumulated score
                    score += tf_tfidf*idf;

                    if(IS_DEBUG_MODE){
                        System.out.println("[DEBUG] tfidf docID " + minDocidTuple.getFirst() + ": " + score);
                    }

                    double newMaxScore = score;
                    for(int j = index + 1; j < minDocidTuple.getSecond().size(); j++){
                        newMaxScore += postingLists[minDocidTuple.getSecond().get(j)].getLexiconEntry().getTfidfTermUpperBound();
                    }

                    if(newMaxScore < rankedDocs.getThreshold()){
                        if(IS_DEBUG_MODE) {
                            System.out.println("[DEBUG] New Max Score < rankedDocs.getThreshold: " + newMaxScore + "<" + rankedDocs.getThreshold() +  " docID " + minDocidTuple.getFirst() + " ruled out");
                        }
                        for(int j = index; j < minDocidTuple.getSecond().size(); j++){
                            postingLists[minDocidTuple.getSecond().get(j)].next();
                        }
                        break;
                    }

                }

                //Move the cursor to the next posting, if there is one, otherwise the flag of the posting list is set to
                // true, in this way we mark the end of the posting list
                postingLists[minDocidTuple.getSecond().get(index)].next();
            }

            //Add the score of the current document to the priority queue
            if(score > rankedDocs.getThreshold()){
                double old_threshold = rankedDocs.getThreshold();
                rankedDocs.add(new Tuple<>(minDocidTuple.getFirst(), score));

                //update the non-essential and the essential posting lists
                if(rankedDocs.getThreshold() > 0) {
                    updateEssentialPostingLists(essential, orderedPostingLists, rankedDocs.getThreshold());
                    if(IS_DEBUG_MODE){
                        System.out.println("[DEBUG] Threshold changed: " + old_threshold + " -> " + rankedDocs.getThreshold());
                    }
                }
            }

            //Clear the support variables for the next iteration
            score = 0;
        }

        //Print the time used to score the documents, so to generate an answer for the query

        System.out.println("\n[SCORE DOCUMENT] Total scoring time: " + (System.currentTimeMillis() - begin) + "ms");

        return getBestKDocuments(rankedDocs, BEST_K_VALUE);
    }

    /**
     * method to check if at least a posting list is ended
     * @param postingLists array of posting lists
     * @return true if at least a posting list is ended, otherwise false
     */
    public static boolean aPostingListEnded(PostingList[] postingLists){

        //For each posting list check if it has a next posting
        for (PostingList postingList : postingLists) {

            //If at least one posting is ended return true
            if (postingList.getPostingListIterator().isNoMorePostings()) {
                return true;
            }
        }

        //If all the posting lists are traversed then return false
        return false;
    }

    /**
     * Checks if all the posting lists are ended, so the iterator has reached the end of each posting list.
     * @param postingLists Array of posting lists.
     * @return true if all the posting lists are ended (no more postings), false otherwise.
     */
    public static boolean allPostingListsEnded(PostingList[] postingLists){

        //For each posting list check if it has a next posting
        for (PostingList postingList : postingLists) {

            //If at least one posting is available return false
            if (!postingList.getPostingListIterator().isNoMorePostings()) {
                return false;
            }
        }

        //If all the posting lists are traversed then return false
        return true;
    }

    /**
     * Get the maximum document id from the passed posting list array
     * @param postingLists posting list from which analyze the current docid to retrieve the maximum
     * @return the maximum document id
     */
    private static long maxDocid(PostingList[] postingLists){

        long max = -1;

        //Traverse the array of posting list and find the maximum document id among the current doc ids
        for(PostingList postingList : postingLists){
            if(postingList.getPostingListIterator().getCurrentDocId() > max){
                max = postingList.getPostingListIterator().getCurrentDocId();
            }
        }
        return max;
    }

    /**
     * Compute the minimum document id in a collection of posting lists, then populates an arrayList containing
     * the indices of the postingList array with the minimum document id. Returns a tuple containing the minimum
     * doc id and the array of indices.
     * We exploit the fact that the posting lists are sorted by document id, so we can optimize the search accessing
     * only the current term pointed by the iterator of each posting list.
     * @param postingLists Array of posting lists.
     * @return a tuple containing the minimum doc id and the array of indices of the postingList array with min doc id.
     */
    public static Tuple<Long, ArrayList<Integer>>  minDocid(PostingList[] postingLists) {

        //Variable to store the minimum document id
        long minDocid = Long.MAX_VALUE;

        //Array to store the posting lists with the minimum document id
        ArrayList<Integer> postingListsWithMinDocid = new ArrayList<>();

        //Retrieve the minimum document id, we've just to check the first element of each posting list since we have
        //the document id in the posting lists ordered by increasing document id value
        //For each posting list we check the current document id
        for(int i = 0; i < postingLists.length; i++){

            //Skip the i-th posting list if the list don't contain more postings, we've reached the end of the list
            if(postingLists[i].getPostingListIterator().isNoMorePostings()){
                continue;
            }

            //If the current docid is smaller than the minDocId, then update the minDocId.
            if (postingLists[i].getPostingListIterator().getCurrentDocId() < minDocid) {

                //If we've found a new minimum document id, store it
                minDocid = postingLists[i].getPostingListIterator().getCurrentDocId();

                //Clear the list of posting lists with the minimum document id
                postingListsWithMinDocid.clear();

                //Add the current posting list to the list of posting lists with the minimum document id
                postingListsWithMinDocid.add(i);

                //Else if the current docid is equal to the min term, then add the current posting list
                // to the list of posting lists with the min docid.
            }else if (postingLists[i].getPostingListIterator().getCurrentDocId() == minDocid) {

                //Add the current posting list to the list of posting lists with the min docid
                postingListsWithMinDocid.add(i);
            }
        }

        //Return the minimum document id and the list of posting lists with the minimum document id
        return new Tuple<>(minDocid, postingListsWithMinDocid);
    }

    /**
     * Extract the first k tuples (docID, score) from the priority queue, in descending order of score.
     * @param rankedDocs The priority queue containing the documents and their scores.
     * @param k The number of tuples to extract.
     * @return an ordered array of tuples containing the document id and the score associated with the document.
     */
    public static ArrayList<Tuple<Long, Double>> getBestKDocuments(PriorityQueue<Tuple<Long,Double>> rankedDocs, int k) {

        //Array list used to build the result
        ArrayList<Tuple<Long, Double>> results = new ArrayList<>();

        //Tuple used to contain the current (docID, score) tuple
        Tuple<Long, Double> tuple;

        //Until k tuples are polled from the priority queue
        while (results.size() < k) {

            //Retrieve the first tuple from the priority queue based on the score value (descending order)
            tuple = rankedDocs.poll();

            //If the tuple is null then we've reached the end of the priority queue, less than k tuples were present
            if (tuple == null) {
                break;
            }

            //Add the tuple to the result list
            results.add(tuple);
        }

        //Return the result list
        return results;
    }

    /**
     * Check if all the current doc ids of each posting list are equal.
     * @param postingLists array of posting lists to check
     * @return true if all the current doc ids are equal, false otherwise
     */
    private static boolean areAllEqual (PostingList[]postingLists){

        long docid = -1;
        if (postingLists.length == 1)
            return true;

        //Traverse all the posting lists if two different docids are found, then return false
        for (PostingList postingList : postingLists) {

            //If at least one is ended
            if (postingList == null) {
                return false;
            }

            if (docid == -1) {
                docid = postingList.getPostingListIterator().getCurrentDocId();
            } else if (docid != postingList.getPostingListIterator().getCurrentDocId()) {
                return false;
            }
        }

        //All the docids are equal
        return true;
    }

    /**
     * function to update the essential posting lists for MaxScore
     *
     * @param essential arraylist that will contain the indexes of the essential posting lists
     * @param orderedPostingLists list of all the posting lists related to the query
     * @param threshold current threshold of the ranking
     */
    private static void updateEssentialPostingLists(ArrayList<Integer> essential, ArrayList<PostingList> orderedPostingLists, double threshold) {
        int tmp_count = 0;
        essential.clear();
        for(int i = 0; i < orderedPostingLists.size(); i++){
            //check the ranking metric
            if(USE_BM25){
                tmp_count += orderedPostingLists.get(i).getLexiconEntry().getBm25TermUpperBound();
            }
            else {
                tmp_count += orderedPostingLists.get(i).getLexiconEntry().getTfidfTermUpperBound();
            }

            //check if the posting list is an essential or a non-essential one
            if(tmp_count > threshold){
                essential.add(i);
            }
        }
    }

    /**
     * function to check if at least an element of postingListIndex (posting lists related to the query) is in the essential set
     *
     * @param postingListsIndex indexes of the query-related posting lists
     * @param essential arraylist containing the current essential posting lists
     * @return true if at least a posting list related to the query is in the essential set, otherwise false
     */
    private static boolean foundEssential(ArrayList<Integer> postingListsIndex, ArrayList<Integer> essential) {
        for(int i : postingListsIndex){
            for(int j: essential){
                if(i == j) {
                    return true;
                }
            }
        }
        return false;
    }

}




