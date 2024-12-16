package it.unipi.dii.aide.mircv.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import static it.unipi.dii.aide.mircv.utils.Config.ENABLE_COMPRESSION;
import static it.unipi.dii.aide.mircv.utils.Config.IS_DEBUG_MODE;

import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.FileReaderUtility;

public class PostingList {
    private final ArrayList<Posting> postingList = new ArrayList<>();  //String->docId

    //variables for query execution:
    private PostingListIterator postingListIterator; //Iterator to iterate over the posting list

    //mergedLexiconEntry of the term, used to retrieve the idf
    private MergedLexiconEntry mergedLexiconEntry;
    RandomAccessFile randomAccessFileDocIds;
    RandomAccessFile randomAccessFileFrequencies;
    RandomAccessFile randomAccessFileSkipBlocks;

    public PostingList() {
        this.postingListIterator = new PostingListIterator();
    }

    public PostingList(Posting posting) {
        this.postingList.add(posting);
    }

    public ArrayList<Posting> getPostingList() {
        return postingList;
    }

    public PostingListIterator getPostingListIterator() {
        return postingListIterator;
    }

    public MergedLexiconEntry getMergedLexiconEntry() {
        return mergedLexiconEntry;
    }

    /**
     * Loads the posting list of the current block
     */
    public void loadPostingList(){
        //Retrieve the docids and the frequencies
        ArrayList<Long> docids;
        ArrayList<Integer> frequencies;

        //If the compression is enabled, then read the posting lists files with the compression
        if(ENABLE_COMPRESSION) {
            System.out.println("TO DO: Compression");

//            docids = readPostingListDocIdsCompressed(randomAccessFileDocIds,
//                    termInfo.getOffsetDocId() + currentSkipBlock.startDocidOffset,
//                    currentSkipBlock.skipBlockDocidLength);
//
//            frequencies = readPostingListFrequenciesCompressed(randomAccessFileFrequencies,
//                    termInfo.getOffsetFrequency() + currentSkipBlock.startFreqOffset,
//                    currentSkipBlock.skipBlockFreqLength);
        }else {//Read without compression
            docids = FileReaderUtility.readPostingListDocIds(randomAccessFileDocIds,
                    this.getMergedLexiconEntry().getOffsetDocId() +
                            this.getPostingListIterator().getCurrentSkipBlock().startDocidOffset,
                    this.getPostingListIterator().getCurrentSkipBlock().skipBlockDocidLength);

            frequencies = FileReaderUtility.readPostingListFrequencies(randomAccessFileFrequencies,
                    this.getMergedLexiconEntry().getOffsetFrequency() +
                            this.getPostingListIterator().getCurrentSkipBlock().startFreqOffset,
                    this.getPostingListIterator().getCurrentSkipBlock().skipBlockFreqLength);
        }
            //Remove the previous postings
            postingList.clear();

            //Create the array list of postings
            for(int i = 0; i < docids.size() ; i++){
                postingList.add(new Posting(docids.get(i), frequencies.get(i)));
            }

            //TO DO: Controllare se l'iteratore SkipBlock deve essere inizializzato in questo momento, o basta l'iteratore alle posting
            //Update the iterator for the current posting list
//                iterator = this.iterator();
            postingListIterator.setPostingIterator(postingList.iterator());


            if(Config.IS_DEBUG_MODE){
                System.out.println("------------------");
                System.out.println("[DEBUG] Partial posting list: " + this);
        }


    }

    public void nextGEQ(long searchedDocId) {
        if (this.getPostingListIterator().getCurrentDocId() == searchedDocId) {
            return;  // Se il docId corrente è uguale a quello cercato, ritorna senza fare nulla
        }

        if (IS_DEBUG_MODE) {
            System.out.println("[DEBUG] Max docId in current skipBlock < searched docId: " +
                    this.getPostingListIterator().getCurrentSkipBlock().maxDocid +" < "+ searchedDocId);
        }

        // Move to the next skip block until we find that the searched doc id can be contained in the
        // portion of the posting list described by the skip block
        while (this.getPostingListIterator().getCurrentSkipBlock().maxDocid < searchedDocId) {
            // If it's possible to move to the next skip block, then move the iterator
            if (this.postingListIterator.getSkipBlockIterator().hasNext()) {
                // Debug
                if (IS_DEBUG_MODE) {
                    System.out.println("[DEBUG] Changing the skip block");
                }

                // Move the iterator to the next skip block
                this.postingListIterator.nextSkipBlock();
                //TO DO: Controllare riga se corretta
                this.loadPostingList(); // Reload the posting list for the current skip block

//                postingIterator = currentSkipBlock.getPostingListIterator();
            } else {
                // All the skip blocks are traversed, the posting list doesn't contain a doc id GEQ than
                // the one searched
                if (IS_DEBUG_MODE) {
                    System.out.println("[DEBUG] End of posting list");
                }
                // Set the end of posting list flag
                this.postingListIterator.setNoMorePostings();
                return;
            }

            if (IS_DEBUG_MODE) {
                System.out.println("[DEBUG] Max docId in the new skipBlock < searched docId: " +
                        postingListIterator.getCurrentSkipBlock().maxDocid +" < "+ searchedDocId);
            }
        }

        // While we have more postings, traverse them
        Posting posting;
        while (postingListIterator.getPostingIterator().hasNext()) {
            posting = postingListIterator.getPostingIterator().next();
            if (posting.getDocId() >= searchedDocId) {
                // If the current posting docId is greater than or equal to the searched docId, return it
                postingListIterator.setCurrentDocId(posting.getDocId());
                postingListIterator.setCurrentFrequency(posting.getFrequency());
                return;
            }
        }

        // No postings are GEQ in the current posting list, we've finished the traversing the whole posting list
        if (!postingListIterator.getSkipBlockIterator().hasNext()) {
            postingListIterator.setNoMorePostings();
        }
    }

    /**
     * Loads the posting list of the given term in memory, this list uses the skipping mechanism.
     * @param lexiconEntry Lexicon entry of the term, used to retrieve the offsets and the lengths of the posting list
     */
    public void openList(MergedLexiconEntry lexiconEntry){

        //Set the terminfo of the posting list
        this.mergedLexiconEntry = lexiconEntry;


        //Open the stream with the posting list random access files
        try {
            randomAccessFileDocIds = new RandomAccessFile(Config.INVINDEX_DOC_IDS_FILE_PATH, "r");
            randomAccessFileFrequencies = new RandomAccessFile(Config.INVINDEX_FREQUENCIES_FILE_PATH, "r");
            randomAccessFileSkipBlocks = new RandomAccessFile(Config.SKIP_BLOCKS_FILE_PATH,"r");

            //Load the skip blocks list of the current term's posting list
            //Skip blocks of the posting list
            ArrayList<SkipBlock> skipBlocks = FileReaderUtility.readPostingListSkipBlocks(
                    randomAccessFileSkipBlocks,
                    lexiconEntry.getOffsetSkipBlock(),
                    lexiconEntry.getNumberOfSkipBlocks()
            );

            //initialize the skip blocks iterator
            postingListIterator.setSkipBlockIterator(skipBlocks.iterator());

            //move the skip blocks' iterator to the first skip block
            postingListIterator.nextSkipBlock();

            //Load the posting list of the current block
            loadPostingList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }



    }

    /**
     * Clear the array list
     */
    public void closeList(){
        postingList.clear();
        try {
            if (randomAccessFileDocIds != null) randomAccessFileDocIds.close();
            if (randomAccessFileFrequencies != null) randomAccessFileFrequencies.close();
            if (randomAccessFileSkipBlocks != null) randomAccessFileSkipBlocks.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing random access files", e);
        }
    }

    @Override
    public String toString() {
        return "PostingList{" +
                "postingList=" + postingList +
                '}';
    }

}
