package it.unipi.dii.aide.mircv.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import static it.unipi.dii.aide.mircv.utils.Config.ENABLE_COMPRESSION;

import it.unipi.dii.aide.mircv.utils.Config;
import it.unipi.dii.aide.mircv.utils.FileReaderUtility;
import static it.unipi.dii.aide.mircv.utils.Config.IS_DEBUG_MODE;

public class PostingList {
    private final ArrayList<Posting> postingList = new ArrayList<>();  //String->docId

    //variables for query execution:
    private PostingListIterator postingListIterator; //Iterator to iterate over the posting list

    // LexiconEntry of the term, used to retrieve the idf
    private LexiconEntry lexiconEntry;

    RandomAccessFile randomAccessFileDocIds;
    RandomAccessFile randomAccessFileFrequencies;
    RandomAccessFile randomAccessFileSkipBlocks;

    /**
     * Default constructor for PostingList. Initializes an empty posting list and its iterator.
     */
    public PostingList() {
        this.postingListIterator = new PostingListIterator();
    }

    /**
     * Constructor that initializes the posting list with a single posting.
     * @param posting The posting to be added to the posting list.
     */
    public PostingList(Posting posting) {
        this.postingList.add(posting);
    }

    public void addPosting(Posting posting){
        this.postingList.add(posting);
    }

    /**
     * Returns the list of postings.
     * @return An ArrayList of Posting objects.
     */
    public ArrayList<Posting> getPostingList() {
        return postingList;
    }

    /**
     * Returns the iterator for the posting list.
     * @return The PostingListIterator object.
     */
    public PostingListIterator getPostingListIterator() {
        return postingListIterator;
    }

    /**
     * Returns the lexicon entry associated with the current posting list.
     * @return The LexiconEntry object.
     */
    public LexiconEntry getLexiconEntry() {
        return lexiconEntry;
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
                    this.getLexiconEntry().getOffsetDocId() +
                            this.getPostingListIterator().getCurrentSkipBlock().startDocidOffset,
                    this.getPostingListIterator().getCurrentSkipBlock().skipBlockDocidLength);

            frequencies = FileReaderUtility.readPostingListFrequencies(randomAccessFileFrequencies,
                    this.getLexiconEntry().getOffsetFrequency() +
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
    }

    /**
     * Search the next doc id of the current posting list, such that is greater or equal to the searched doc id.
     * It exploits the skip blocks to traverse faster the posting list
     * @param searchedDocId doc id to search
     * posting are present in the posting list
     */
    public void nextGEQ(long searchedDocId) {
        if (this.getPostingListIterator().getCurrentDocId() == searchedDocId) {
            return;
        }

        // Move to the next skip block until we find that the searched doc id can be contained in the
        // portion of the posting list described by the skip block
        while (postingListIterator.getCurrentSkipBlock().maxDocid < searchedDocId) {

            if (IS_DEBUG_MODE) {
                System.out.println("[DEBUG] Max docId in current skipBlock < searched docId: " +
                        postingListIterator.getCurrentSkipBlock().maxDocid +" < "+ searchedDocId);
            }
            // If it's possible to move to the next skip block, then move the iterator
            if (postingListIterator.getSkipBlockIterator().hasNext()) {
                // Debug
                if (IS_DEBUG_MODE) {
                    System.out.println("[DEBUG] Changing the skip block iterator to next skip block");
                }

                // Move the iterator to the next skip block
                postingListIterator.nextSkipBlock();
                loadPostingList(); // Reload the posting list for the current skip block
            }
            else {
                // All the skip blocks are traversed, the posting list doesn't contain a doc id GEQ than
                // the one searched
                if (IS_DEBUG_MODE) {
                    System.out.println("[DEBUG] Reached the end of this posting list");
                }
                // Set the end of posting list flag
                this.postingListIterator.setNoMorePostings();
                return;
            }
        }


        // Iterate over the postings until to find a docid greater or equal than the searched one
        Posting posting;
        while (postingListIterator.getPostingIterator().hasNext()) {
            posting = postingListIterator.getPostingIterator().next();

            if (posting.docId >= searchedDocId) {
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
    public void openList(LexiconEntry lexiconEntry){

        //Set the terminfo of the posting list
        this.lexiconEntry = lexiconEntry;


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

    /**
     * Retrieves the next posting in the current posting list. If the current skipblock is exhausted, it moves to the next block.
     * @return The next Posting object or null if there are no more postings.
     */
    public Posting next() {
        if (!postingListIterator.getPostingIterator().hasNext()) {
            if (postingListIterator.getSkipBlockIterator().hasNext()) {
                postingListIterator.setCurrentSkipBlock(postingListIterator.getSkipBlockIterator().next());
                loadPostingList();
                Posting posting = postingListIterator.getPostingIterator().next();
                postingListIterator.setCurrentDocId(posting.getDocId());
                postingListIterator.setCurrentFrequency(posting.getFrequency());
                return null;
            } else {
                postingListIterator.setNoMorePostings(true);
                return null;
            }
        }

        Posting posting = postingListIterator.getPostingIterator().next();
        postingListIterator.setCurrentDocId(posting.getDocId());
        postingListIterator.setCurrentFrequency(posting.getFrequency());
        return posting;
    }

    /**
     * Checks if there are more postings available in the current posting list or its skip blocks.
     * @return True if more postings are available, false otherwise.
     */
    public boolean hasNext() {
        return postingListIterator.hasNext();
    }

    /**
     * Provides a string representation of the posting list.
     * @return A string describing the posting list.
     */
    @Override
    public String toString() {
        return "PostingList = " + postingList;
    }

}
