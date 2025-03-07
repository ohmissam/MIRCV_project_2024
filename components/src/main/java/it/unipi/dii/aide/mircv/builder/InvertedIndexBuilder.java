package it.unipi.dii.aide.mircv.builder;

import it.unipi.dii.aide.mircv.model.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
* Represents a builder for the creation of documents
* */

public class InvertedIndexBuilder {
    BlockLexicon lexicon;
    InvertedIndex invertedIndex;

    public InvertedIndexBuilder() {
        this.lexicon = new BlockLexicon();
        this.invertedIndex = new InvertedIndex();
    }

    public InvertedIndexBuilder(BlockLexicon lexicon, InvertedIndex invertedIndex) {
        this.lexicon = lexicon;
        this.invertedIndex = invertedIndex;
    }

    public void insertDocument(DocumentAfterPreprocessing documentAfterPreprocessing) {
        long docId = documentAfterPreprocessing.getDocId();

        for (String term : documentAfterPreprocessing.getTerms()) {


            // if the term is already present in the lexicon
            if (lexicon.getLexicon().containsKey(term)) {
                // get the list of the postings of the term
                PostingList postingList = invertedIndex.getInvertedIndex().get(term);
                //Flag to set if the doc id's posting is present in the posting list of the term
                boolean found = false;
                // Check if a posting already exists for the current docId
                //Iterate through the posting
                for(Posting p : postingList.getPostingList()) {

                    //If the doc id is present, increment the frequency and terminate the loop
                    if (p.getDocId() == documentAfterPreprocessing.getDocId()) {

                        //Increment the frequency of the doc id
                        p.incrementFrequency();

                        found = true;
                        break;
                    }
                }
                    //If the posting of the document is not present in the posting list, it must be added
                    if (!found) {

                        //Posting added to the posting list of the term
                        postingList.getPostingList().add(new Posting(documentAfterPreprocessing.getDocId(), 1));
                    }

            } else {
                BlockLexiconEntry blockLexiconEntry = new BlockLexiconEntry();
                // If the term is not present in the lexicon
                lexicon.getLexicon().put(term, blockLexiconEntry);

                // Create a new posting list with the current docId
                Posting posting = new Posting(docId, 1);
                PostingList postingsList = new PostingList(posting);

                // Insert the posting list into the inverted index
                invertedIndex.getInvertedIndex().put(term, postingsList);
            }
        }
    }


    /**
     * Sort the lexicon with complexity O(nlog(n)) where n is the # of elements in the lexicon.
     */
    public void sortLexicon(){

        //To not double the memory instantiating a new data structure we've decided to use the following sorting
        lexicon.setLexicon(lexicon.getLexicon().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new))); //LinkedHashMap to keep O(1) time complexity
    }

    /**
     * Sort the inverted index with complexity O(nlog(n)) where n is the # of elements in the inverted index.
     */
    public void sortInvertedIndex(){
        invertedIndex.setInvertedIndex(invertedIndex.getInvertedIndex().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new)));
    }

    public BlockLexicon getLexicon() {
        return lexicon;
    }

    public InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }

    public Set<String> getLexiconTerms() {
            return lexicon.getLexicon().keySet();
        }

    public void setLexicon(BlockLexicon lexicon) {
            this.lexicon = lexicon;
        }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, BlockLexiconEntry> entry : lexicon.getLexicon().entrySet()) {
            String term = entry.getKey();
            BlockLexiconEntry blockLexiconEntry = entry.getValue();

            result.append(term).append("    ");
        }

        return result.toString();
    }

    /**
     * Clear the class instances in order to be used for a new block processing.
     */
    public void clear(){
        lexicon.clear();
        invertedIndex.clear();

        //Call the garbage collector to thrash the data structures cleared above, if it is not done the memory will be
        // over the threshold until the gc will be called automatically, causing the writes of a block at every document
        // processed after the trespassing of the threshold.
        Runtime.getRuntime().gc();
    }


}

